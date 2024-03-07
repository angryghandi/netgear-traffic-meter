package com.angryghandi.network.traffic.entity;

import org.junit.jupiter.api.Test;

import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_SOURCE;
import static org.assertj.core.api.Assertions.assertThat;

class TrafficSourceTest {

    @Test
    void toString_hides_private_data() {
        assertThat(TRAFFIC_SOURCE)
                .hasToString("TrafficSource(id=1, name=Nighthawk AX5400, url=http://10.0.0.1/traffic_meter.htm, active=true)");
    }
}
