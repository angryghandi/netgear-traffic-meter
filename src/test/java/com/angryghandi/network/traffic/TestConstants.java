package com.angryghandi.network.traffic;

import com.angryghandi.network.traffic.entity.TrafficSource;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {

    public static final String TRAFFIC_TYPE_TODAY = "TODAY";

    public static final String TRAFFIC_TYPE_YESTERDAY = "YESTERDAY";

    public static final String TRAFFIC_TYPE_THIS_WEEK = "THIS_WEEK";

    public static final String TRAFFIC_TYPE_THIS_MONTH = "THIS_MONTH";

    public static final String TRAFFIC_TYPE_LAST_MONTH = "LAST_MONTH";

    public static final TrafficSource TRAFFIC_SOURCE = TrafficSource.builder()
            .id(1L)
            .name("Nighthawk AX5400")
            .url("http://10.0.0.1/traffic_meter.htm")
            .username("admin")
            .password("password")
            .active(true)
            .build();

    public final static String HTML = "<html></html>";
}
