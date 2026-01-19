package com.company.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users_auth",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "username"}),
                @UniqueConstraint(columnNames = {"client_id", "email"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Company / Tenant ID
     */
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false, length = 50)
    private String username;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Many Users <-> Many Roles
    //Owning Side
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
}
