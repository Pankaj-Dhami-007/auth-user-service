package com.company.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"code"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Company unique code (used internally & in JWT)
     * Example: ABC_TECH, XYZ_TRADERS
     */
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    /**
     * Display name of company
     */
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /**
     * Optional description
     */
    @Column(name = "description", length = 250)
    private String description;

    /**
     * Company active / inactive
     * If false → no user can login
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
