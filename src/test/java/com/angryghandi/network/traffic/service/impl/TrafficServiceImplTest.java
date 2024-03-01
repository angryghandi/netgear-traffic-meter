package com.angryghandi.network.traffic.service.impl;

import com.angryghandi.network.traffic.dto.TrafficStatistic;
import com.angryghandi.network.traffic.repository.TrafficMeasureRepository;
import com.angryghandi.network.traffic.repository.TrafficTypeRepository;
import com.angryghandi.network.traffic.service.NetgearClient;
import com.angryghandi.network.traffic.service.TrafficService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class TrafficServiceImplTest {

    @Mock
    private TrafficTypeRepository trafficTypeRepositoryMock;

    @Mock
    private TrafficMeasureRepository trafficMeasureRepositoryMock;

    @Mock
    private NetgearClient netgearClientMock;

    private TrafficService cut;

    @BeforeEach
    void beforeEach() {
        cut = new TrafficServiceImpl(trafficTypeRepositoryMock, trafficMeasureRepositoryMock, netgearClientMock);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(trafficTypeRepositoryMock, trafficMeasureRepositoryMock, netgearClientMock);
    }

    @Test
    @SneakyThrows
    void extractStatistics() {
        final TrafficStatistic trafficStatistic1 = TrafficStatistic.builder()
                .label("TODAY").upload(6.86).download(52.42).total(59.28)
                .build();
        final TrafficStatistic trafficStatistic2 = TrafficStatistic.builder()
                .label("YESTERDAY").upload(0.0).download(0.0).total(0.0)
                .build();
        final TrafficStatistic trafficStatistic3 = TrafficStatistic.builder()
                .label("THIS_WEEK").upload(6.86).uploadAverage(0.98).download(52.42).downloadAverage(7.49).total(59.28).totalAverage(8.47)
                .build();
        final TrafficStatistic trafficStatistic4 = TrafficStatistic.builder()
                .label("THIS_MONTH").upload(6.86).uploadAverage(0.23).download(52.42).downloadAverage(1.75).total(59.28).totalAverage(1.98)
                .build();
        final TrafficStatistic trafficStatistic5 = TrafficStatistic.builder()
                .label("LAST_MONTH").upload(0.00).uploadAverage(0.00).download(0.00).downloadAverage(0.00).total(0.00).totalAverage(0.00)
                .build();
        final String html = Files.readString(Path.of("src/test/resources/traffic_meter_1.htm"));

        final List<TrafficStatistic> trafficStatistics = cut.extractStatistics(html);

        assertThat(trafficStatistics).hasSize(5);
        assertThat(trafficStatistics.get(0)).isEqualTo(trafficStatistic1);
        assertThat(trafficStatistics.get(1)).isEqualTo(trafficStatistic2);
        assertThat(trafficStatistics.get(2)).isEqualTo(trafficStatistic3);
        assertThat(trafficStatistics.get(3)).isEqualTo(trafficStatistic4);
        assertThat(trafficStatistics.get(4)).isEqualTo(trafficStatistic5);
    }

    @ParameterizedTest
    @MethodSource("valuesAndAverages")
    void parseValueAndAverage(final String data, final double value1, final double value2) {
        final Double[] results = ((TrafficServiceImpl) cut).parseValueAndAverage(data);
        assertThat(results[0]).isEqualTo(value1);
        assertThat(results[1]).isEqualTo(value2);
    }

    private static Stream<Arguments> valuesAndAverages() {
        return Stream.of(Arguments.of("6.86 / 0.98", 6.86, 0.98),
                Arguments.of("0.00 / 0.00", 0.0, 0.0));
    }

}
