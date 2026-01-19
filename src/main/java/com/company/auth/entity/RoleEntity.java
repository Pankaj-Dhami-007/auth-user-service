package com.company.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "role_name"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(length = 200)
    private String description;

    // inverse side
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();
}

