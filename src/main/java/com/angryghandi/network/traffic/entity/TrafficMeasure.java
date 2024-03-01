package com.angryghandi.network.traffic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "traffic_measure")
public class TrafficMeasure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "type_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private TrafficType trafficType;

    @Column(name = "ts", nullable = false)
    private Date timestamp;

    @Column(name = "upload", nullable = false)
    private Double upload;

    @Column(name = "upload_average")
    private Double uploadAverage;

    @Column(name = "download", nullable = false)
    private Double download;

    @Column(name = "download_average")
    private Double downloadAverage;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "total_average")
    private Double totalAverage;

}
