package com.capstone.backend.repository;

import com.capstone.backend.entity.UserRolePermission;
import com.capstone.backend.entity.type.MethodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRolePermissionRepository extends JpaRepository<UserRolePermission, Long> {

    @Query("select urp from UserRolePermission urp where urp.active = true and urp.permission.active = true and urp.role.name = :roleName " +
            "and urp.permission.methodType = :methodType and urp.permission.path = :url")
    public UserRolePermission needCheckPermission(String url, MethodType methodType, String roleName);
}
