package com.company.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "permission_name"})
        }
)
public class PermissionEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    @Column(length = 200)
    private String description;
}
