package com.capstone.backend.service;

import com.capstone.backend.model.dto.materials.DataMaterialsDTOResponse;
import com.capstone.backend.model.dto.materials.MaterialsFilterDTORequest;
import com.capstone.backend.model.dto.resource.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    public List<ResourceDTOResponse> uploadResource(ResourceDTORequest request, MultipartFile[] files);

    public ResourceDetailDTOResponse getResourceDetailById(Long id);

    public PagingResourceDTOResponse searchMediaResource(ResourceMediaDTOFilter resourceDTOFilter);

    public ResourceDTOUpdateResponse getResourceById(Long id);

    public DataMaterialsDTOResponse searchMaterials(MaterialsFilterDTORequest request);

//    public ResourceDTOResponse getResourceById(Long id);
}
