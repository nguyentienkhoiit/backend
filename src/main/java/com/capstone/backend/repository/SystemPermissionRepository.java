package com.capstone.backend.repository;

import com.capstone.backend.entity.SystemPermission;
import com.capstone.backend.entity.type.MethodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SystemPermissionRepository extends JpaRepository<SystemPermission, Long> {
}
