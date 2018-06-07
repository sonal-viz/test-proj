package com.myorg.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myorg.entity.ClusterInfo;

public interface ClusterRepository extends JpaRepository<ClusterInfo, String> {
}
