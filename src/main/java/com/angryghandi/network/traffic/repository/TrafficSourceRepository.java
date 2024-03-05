package com.angryghandi.network.traffic.repository;

import com.angryghandi.network.traffic.entity.TrafficSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficSourceRepository extends JpaRepository<TrafficSource, Long> {

    List<TrafficSource> findAllByActiveTrue();

}