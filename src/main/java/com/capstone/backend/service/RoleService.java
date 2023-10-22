package com.capstone.backend.service;

import com.capstone.backend.model.dto.role.*;

public interface RoleService {
    public PagingRoleDTOResponse viewSearchRole(RoleDTOFilter request);

    public RoleDTODetailResponse getRoleById(Long id);

    public RoleDTODetailResponse createRole(RoleDTORequest request);

    public RoleDTODetailResponse updateRole(RoleDTOUpdate request);

    public Boolean changeStatus(Boolean active, Long id);
}
