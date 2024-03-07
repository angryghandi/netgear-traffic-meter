package com.angryghandi.network.traffic.repository;

import com.angryghandi.network.traffic.entity.TrafficSource;
import com.angryghandi.network.traffic.service.TrafficService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {"/traffic_sources.sql"})
class TrafficSourceRepositoryTest {

    @MockBean
    private TrafficService trafficService;

    @Autowired
    private TrafficSourceRepository cut;

    @Test
    void findAllByActiveTrue() {
        final List<TrafficSource> trafficSources = cut.findAllByActiveTrue();

        assertThat(trafficSources).hasSize(1).containsExactly(TrafficSource.builder()
                .id(1L)
                .name("Nighthawk AX5400")
                .url("http://10.0.0.1/traffic_meter.htm")
                .username("admin")
                .password("password")
                .active(true)
                .build());
    }
}
