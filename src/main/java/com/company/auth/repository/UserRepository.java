package com.company.auth.repository;

import com.company.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameAndIsActiveTrue(String username);
    Optional<UserEntity> findByEmailAndIsActiveTrue(String email);

    Optional<Object> findByUsername(String admin);
    Optional<UserEntity> findByUsernameAndClientId(String username, Long id);
}