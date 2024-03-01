package com.angryghandi.network.traffic.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class TrafficStatistic {

    private String label;

    private Double upload;

    private Double uploadAverage;

    private Double download;

    private Double downloadAverage;

    private Double total;

    private Double totalAverage;

}
