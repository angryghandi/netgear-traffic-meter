package com.angryghandi.network.traffic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "traffic_source")
@ToString(onlyExplicitlyIncluded = true)
public class TrafficSource {

    @Id
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(unique = true, nullable = false)
    String name;

    @ToString.Include
    @Column(unique = true, nullable = false)
    String url;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String password;

    @ToString.Include
    @Column(nullable = false)
    Boolean active;
}
