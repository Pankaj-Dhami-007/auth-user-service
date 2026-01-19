package com.company.auth.repository;

import com.company.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByClientIdAndRoleName(Long clientId, String roleName);
    List<RoleEntity> findByClientIdAndRoleNameIn(Long clientId, Set<String> roleNames);

}