package com.company.auth.repository;

import com.company.auth.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionEntityRepository extends JpaRepository<RolePermissionEntity, Long> {
}