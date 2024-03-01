package com.angryghandi.network.traffic.repository;

import com.angryghandi.network.traffic.entity.TrafficMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficMeasureRepository extends JpaRepository<TrafficMeasure, Long> {
}
