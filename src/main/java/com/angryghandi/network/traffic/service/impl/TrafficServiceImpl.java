package com.angryghandi.network.traffic.service.impl;

import com.angryghandi.network.traffic.dto.TrafficStatistic;
import com.angryghandi.network.traffic.entity.TrafficMeasure;
import com.angryghandi.network.traffic.entity.TrafficType;
import com.angryghandi.network.traffic.repository.TrafficMeasureRepository;
import com.angryghandi.network.traffic.repository.TrafficTypeRepository;
import com.angryghandi.network.traffic.service.NetgearClient;
import com.angryghandi.network.traffic.service.TrafficService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficServiceImpl implements TrafficService {

    private final TrafficTypeRepository trafficTypeRepository;

    private final TrafficMeasureRepository trafficMeasureRepository;

    private final NetgearClient netgearClient;

    @Override
    public void measureTraffic() {
//        // convert traffic types to map so that they can be easily looked up by label / name
        final Map<String, TrafficType> trafficTypes = trafficTypeRepository.findAll().stream()
                .collect(Collectors.toMap(TrafficType::getName, Function.identity()));

        final String html = netgearClient.getTrafficMeter();
        if (isNull(html)) {
            log.error("failed to get html from netgear client");
            return;
        }
        final List<TrafficStatistic> trafficStatistics = extractStatistics(html);
        log.info("{}", trafficStatistics);

        // transform trafficStatistic to trafficMeasure
        final Date timestamp = new Date();
        final List<TrafficMeasure> trafficMeasures = new ArrayList<>();
        for (final TrafficStatistic trafficStatistic : trafficStatistics) {
            final TrafficType trafficType = trafficTypes.get(trafficStatistic.getLabel());
            final TrafficMeasure trafficMeasure = TrafficMeasure.builder()
                    .trafficType(trafficType)
                    .timestamp(timestamp)
                    .download(trafficStatistic.getDownload())
                    .downloadAverage(trafficStatistic.getDownloadAverage())
                    .upload(trafficStatistic.getUpload())
                    .uploadAverage(trafficStatistic.getUploadAverage())
                    .total(trafficStatistic.getTotal())
                    .totalAverage(trafficStatistic.getTotalAverage())
                    .build();
            trafficMeasures.add(trafficMeasure);
        }
        trafficMeasureRepository.saveAll(trafficMeasures);
    }

    @Override
    public List<TrafficStatistic> extractStatistics(final String html) {
        final List<TrafficStatistic> trafficStatistics = new ArrayList<>();
        final Document document = Jsoup.parse(html);

        // measurements are in the third table in document
        final Element table = document.select("table").get(2);

        // select the table body from the table
        final Elements tbody = table.select("tbody");

        /*
         * 7 rows in the table, rows 2 - 6, have the traffic measurements
         * row 2: today
         * row 3: yesterday
         * row 4: week
         * row 5: month
         * row 6: year
         */
        final int numberOfRows = tbody.select("tr").size();
        if (numberOfRows != 7) {
            log.error("expected 7 rows in table, found: {}", numberOfRows);
            return List.of();
        }

        for (int rowNum = 2; rowNum <= 6; rowNum++) {
            // select the row indexed by rowNum
            final Element row = tbody.select("tr").get(rowNum);

            // five columns in each row
            final Elements elements = row.select("td");
            if (elements.size() != 5) {
                log.error("expected 5 columns in row, found: count: {} in rowNum: {}", elements.size(), rowNum);
                return List.of();
            }

            // column 0 has the label, columns 2, 3 and 4 have the upload, download and total values respectively
            final TrafficStatistic trafficStatistic = TrafficStatistic.builder().build();
            trafficStatistic.setLabel(parseLabel(elements.get(0).text()));
            if (rowNum < 4) {
                trafficStatistic.setUpload(Double.valueOf(elements.get(2).text()));
                trafficStatistic.setDownload(Double.valueOf(elements.get(3).text()));
                trafficStatistic.setTotal(Double.valueOf(elements.get(4).text()));
            } else {
                Double[] values = parseValueAndAverage(elements.get(2).text());
                trafficStatistic.setUpload(values[0]);
                trafficStatistic.setUploadAverage(values[1]);
                values = parseValueAndAverage(elements.get(3).text());
                trafficStatistic.setDownload(values[0]);
                trafficStatistic.setDownloadAverage(values[1]);
                values = parseValueAndAverage(elements.get(4).text());
                trafficStatistic.setTotal(values[0]);
                trafficStatistic.setTotalAverage(values[1]);
            }

            trafficStatistics.add(trafficStatistic);
        }

        return trafficStatistics;
    }

    /**
     * Transform statistic labels to all uppercase and convert spaces to underscores.
     *
     * @param value statistic label
     * @return transformed label
     */
    String parseLabel(final String value) {
        return value.trim().replace(' ', '_').toUpperCase();
    }

    /**
     * Parse the two double values from strings like "69.18 / 9.88".
     *
     * @param value string to parse
     * @return double array with two values
     */
    Double[] parseValueAndAverage(final String value) {
        final String[] split = value.split("/");
        return new Double[]{Double.valueOf(split[0]), Double.valueOf(split[1])};
    }

}
