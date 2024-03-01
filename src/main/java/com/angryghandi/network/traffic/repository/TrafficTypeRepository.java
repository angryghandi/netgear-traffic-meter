package com.angryghandi.network.traffic.repository;

import com.angryghandi.network.traffic.entity.TrafficType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficTypeRepository extends JpaRepository<TrafficType, Long> {
}
