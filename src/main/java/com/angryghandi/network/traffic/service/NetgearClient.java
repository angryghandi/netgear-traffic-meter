package com.angryghandi.network.traffic.service;

public interface NetgearClient {

    /**
     * Get traffic_meter.htm from Netgear Router.
     *
     * @return traffic_meter.htm HTML as string
     */
    String getTrafficMeter();
}
