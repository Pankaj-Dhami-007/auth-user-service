package com.company.auth.repository;

import com.company.auth.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, Long> {
}