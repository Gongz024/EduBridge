package com.it342g02.edubridge.edubridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.Badge;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

}
