package com.capstone.backend.model.mapper;

import com.capstone.backend.entity.SystemPermission;
import com.capstone.backend.model.dto.systempermission.SystemPermissionDTORequest;
import com.capstone.backend.model.dto.systempermission.SystemPermissionDTOResponse;
import com.capstone.backend.model.dto.systempermission.SystemPermissionDTOUpdate;

import java.time.LocalDateTime;

public class SystemPermissionMapper {
    public static SystemPermissionDTOResponse toSystemPermissionDTOResponse(SystemPermission systemPermission, String creator) {
        Long dependencyPermissionId = null;
        if(systemPermission.getSystemPermissionRoot() != null) {
            dependencyPermissionId = systemPermission.getSystemPermissionRoot().getId();
        }
        return SystemPermissionDTOResponse.builder()
                .systemPermissionId(systemPermission.getId())
                .permissionName(systemPermission.getName())
                .active(systemPermission.getActive())
                .createdAt(systemPermission.getCreatedAt())
                .description(systemPermission.getDescription())
                .methodType(systemPermission.getMethodType())
                .path(systemPermission.getPath())
                .creator(creator)
                .dependencyPermissionId(dependencyPermissionId)
                .build();
    }

    public static SystemPermission toSystemPermission(SystemPermissionDTORequest systemPermissionDTORequest) {
        return SystemPermission.builder()
                .name(systemPermissionDTORequest.getPermissionName())
                .active(true)
                .description(systemPermissionDTORequest.getDescription())
                .createdAt(LocalDateTime.now())
                .methodType(systemPermissionDTORequest.getMethodType())
                .path(systemPermissionDTORequest.getPath())
                .systemPermissionRoot(null)
                .build();
    }

    public static SystemPermission toSystemPermission(SystemPermissionDTOUpdate systemPermissionDTOUpdate) {
        return SystemPermission.builder()
                .id(systemPermissionDTOUpdate.getPermissionId())
                .name(systemPermissionDTOUpdate.getPermissionName())
                .description(systemPermissionDTOUpdate.getDescription())
                .methodType(systemPermissionDTOUpdate.getMethodType())
                .path(systemPermissionDTOUpdate.getPath())
                .systemPermissionRoot(null)
                .build();
    }
}
