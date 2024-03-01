package com.angryghandi.network.traffic.service;

import com.angryghandi.network.traffic.dto.TrafficStatistic;

import java.util.List;

public interface TrafficService {

    /**
     *
     */
    void measureTraffic();

    /**
     * Extract the traffic statistics from the provided html.
     *
     * @param html html from router, page source of traffic_meter.htm
     * @return list of statistics
     */
    List<TrafficStatistic> extractStatistics(final String html);

}
