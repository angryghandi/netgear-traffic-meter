package com.angryghandi.network.traffic.service.impl;

import com.angryghandi.network.traffic.dto.TrafficStatistic;
import com.angryghandi.network.traffic.entity.TrafficMeasure;
import com.angryghandi.network.traffic.entity.TrafficType;
import com.angryghandi.network.traffic.repository.TrafficMeasureRepository;
import com.angryghandi.network.traffic.repository.TrafficSourceRepository;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_SOURCE;
import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_TYPE_LAST_MONTH;
import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_TYPE_THIS_MONTH;
import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_TYPE_THIS_WEEK;
import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_TYPE_TODAY;
import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_TYPE_YESTERDAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class TrafficServiceImplTest {

    @Mock
    private TrafficTypeRepository trafficTypeRepositoryMock;

    @Mock
    private TrafficMeasureRepository trafficMeasureRepositoryMock;

    @Mock
    private TrafficSourceRepository trafficSourceRepositoryMock;

    @Mock
    private NetgearClient netgearClientMock;

    @Captor
    private ArgumentCaptor<List<TrafficMeasure>> trafficMeasuresCaptor;

    private TrafficService cut;

    @BeforeEach
    void beforeEach() {
        cut = new TrafficServiceImpl(trafficTypeRepositoryMock, trafficMeasureRepositoryMock, trafficSourceRepositoryMock,
                netgearClientMock);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(trafficTypeRepositoryMock, trafficMeasureRepositoryMock, trafficSourceRepositoryMock,
                netgearClientMock);
    }

    @Test
    @SneakyThrows
    void extractStatistics() {
        final TrafficStatistic trafficStatistic1 = TrafficStatistic.builder()
                .label(TRAFFIC_TYPE_TODAY).upload(6.86).download(52.42).total(59.28)
                .build();
        final TrafficStatistic trafficStatistic2 = TrafficStatistic.builder()
                .label(TRAFFIC_TYPE_YESTERDAY).upload(0.0).download(0.0).total(0.0)
                .build();
        final TrafficStatistic trafficStatistic3 = TrafficStatistic.builder()
                .label(TRAFFIC_TYPE_THIS_WEEK).upload(6.86).uploadAverage(0.98).download(52.42).downloadAverage(7.49).total(59.28).totalAverage(8.47)
                .build();
        final TrafficStatistic trafficStatistic4 = TrafficStatistic.builder()
                .label(TRAFFIC_TYPE_THIS_MONTH).upload(6.86).uploadAverage(0.23).download(52.42).downloadAverage(1.75).total(59.28).totalAverage(1.98)
                .build();
        final TrafficStatistic trafficStatistic5 = TrafficStatistic.builder()
                .label(TRAFFIC_TYPE_LAST_MONTH).upload(0.00).uploadAverage(0.00).download(0.00).downloadAverage(0.00).total(0.00).totalAverage(0.00)
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

    @Test
    void measureTraffic_no_sources() {
        when(trafficSourceRepositoryMock.findAllByActiveTrue()).thenReturn(List.of());

        cut.measureTraffic();

        verify(trafficSourceRepositoryMock).findAllByActiveTrue();
    }

    @Test
    void measureTraffic() {
        when(trafficSourceRepositoryMock.findAllByActiveTrue()).thenReturn(List.of(TRAFFIC_SOURCE));
        when(trafficTypeRepositoryMock.findAll()).thenReturn(trafficTypes());
        when(netgearClientMock.getTrafficMeter(TRAFFIC_SOURCE)).thenReturn(getHtml());

        cut.measureTraffic();

        verify(trafficSourceRepositoryMock).findAllByActiveTrue();
        verify(trafficTypeRepositoryMock).findAll();
        verify(netgearClientMock).getTrafficMeter(TRAFFIC_SOURCE);
        verify(trafficMeasureRepositoryMock).saveAll(trafficMeasuresCaptor.capture());

        assertThat(trafficMeasuresCaptor.getAllValues()).hasSize(1);
        final List<TrafficMeasure> trafficMeasures = trafficMeasuresCaptor.getValue();
        assertThat(trafficMeasures).hasSize(5);
        assertThat(trafficMeasures.stream().map(TrafficMeasure::getTrafficType).toList())
                .containsExactlyInAnyOrderElementsOf(trafficTypes());
        assertThat(trafficMeasures.stream().map(tm -> tm.getTrafficType().getName()).toList())
                .containsExactly(TRAFFIC_TYPE_TODAY, TRAFFIC_TYPE_YESTERDAY, TRAFFIC_TYPE_THIS_WEEK,
                        TRAFFIC_TYPE_THIS_MONTH, TRAFFIC_TYPE_LAST_MONTH);

        final Optional<TrafficMeasure> optionalTrafficMeasure = trafficMeasures.stream()
                .filter(tm -> tm.getTrafficType().getName().equals(TRAFFIC_TYPE_THIS_WEEK)).findFirst();
        assertThat(optionalTrafficMeasure).isPresent();
        final TrafficMeasure trafficMeasure = optionalTrafficMeasure.get();
        assertThat(trafficMeasure.getId()).isNull();
        assertThat(trafficMeasure.getTimestamp()).isNotNull();
        assertThat(trafficMeasure.getTrafficType()).isNotNull();
        assertThat(trafficMeasure.getTrafficSource()).isEqualTo(TRAFFIC_SOURCE);
        assertThat(trafficMeasure.getUpload()).isEqualTo(6.86);
        assertThat(trafficMeasure.getUploadAverage()).isEqualTo(0.98);
        assertThat(trafficMeasure.getDownload()).isEqualTo(52.42);
        assertThat(trafficMeasure.getDownloadAverage()).isEqualTo(7.49);
        assertThat(trafficMeasure.getTotal()).isEqualTo(59.28);
        assertThat(trafficMeasure.getTotalAverage()).isEqualTo(8.47);
    }

    private static Stream<Arguments> valuesAndAverages() {
        return Stream.of(Arguments.of("6.86 / 0.98", 6.86, 0.98),
                Arguments.of("0.00 / 0.00", 0.0, 0.0));
    }

    @SneakyThrows
    private String getHtml() {
        try (final InputStream is = getClass().getResourceAsStream("/traffic_meter_1.htm")) {
            assertThat(is).isNotNull();
            return new String(is.readAllBytes());
        }
    }

    private List<TrafficType> trafficTypes() {
        return List.of(TrafficType.builder().id(1L).name(TRAFFIC_TYPE_TODAY).build(),
                TrafficType.builder().id(2L).name(TRAFFIC_TYPE_YESTERDAY).build(),
                TrafficType.builder().id(3L).name(TRAFFIC_TYPE_THIS_WEEK).build(),
                TrafficType.builder().id(4L).name(TRAFFIC_TYPE_LAST_MONTH).build(),
                TrafficType.builder().id(5L).name(TRAFFIC_TYPE_THIS_MONTH).build());
    }

}
