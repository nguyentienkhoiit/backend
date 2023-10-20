package com.capstone.backend.service;

import com.capstone.backend.model.dto.userresource.UserResourceRequest;
import org.springframework.core.io.Resource;

public interface UserResourceService {
    public Boolean actionResource(UserResourceRequest request);

    public Resource downloadResource(String fileName);
}
