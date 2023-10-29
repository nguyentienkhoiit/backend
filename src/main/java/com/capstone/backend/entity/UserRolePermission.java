package com.capstone.backend.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_role_permission_tbl", uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_role_permission",
                columnNames = {"system_permission_id", "role_id"})
})
public class UserRolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Boolean active;
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "system_permission_id")
    SystemPermission permission;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;
}
