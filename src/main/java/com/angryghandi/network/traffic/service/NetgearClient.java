package com.angryghandi.network.traffic.service;

import com.angryghandi.network.traffic.entity.TrafficSource;

public interface NetgearClient {

    /**
     * Get traffic_meter.htm from Netgear Router defined by {@link TrafficSource}.
     *
     * @param trafficSource the traffic source / router
     * @return traffic_meter.htm HTML as string
     */
    String getTrafficMeter(final TrafficSource trafficSource);
}
