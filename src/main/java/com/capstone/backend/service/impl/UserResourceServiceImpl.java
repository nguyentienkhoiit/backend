package com.capstone.backend.service.impl;

import com.capstone.backend.entity.Resource;
import com.capstone.backend.entity.User;
import com.capstone.backend.entity.UserResource;
import com.capstone.backend.entity.type.ActionType;
import com.capstone.backend.exception.ApiException;
import com.capstone.backend.model.dto.userresource.UserResourceRequest;
import com.capstone.backend.repository.ResourceRepository;
import com.capstone.backend.repository.UserRepository;
import com.capstone.backend.repository.UserResourceRepository;
import com.capstone.backend.service.FileService;
import com.capstone.backend.service.UserResourceService;
import com.capstone.backend.utils.MessageException;
import com.capstone.backend.utils.UserHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserResourceServiceImpl implements UserResourceService {
    FileService fileService;
    ResourceRepository resourceRepository;
    UserResourceRepository userResourceRepository;
    UserHelper userHelper;
    UserRepository userRepository;
    MessageException messageException;

    private void findAndDeleteUserResourceExist(User userLoggedIn, Long resourceId, ActionType actionType) {
        userResourceRepository.findUserResourceHasActionType(
                userLoggedIn.getId(),
                resourceId,
                actionType
        ).ifPresent(
                userResourceExist -> userResourceRepository
                        .deleteUserResourceHasActionType(
                                userLoggedIn.getId(),
                                resourceId,
                                actionType
                        )
        );
    }

    @Override
    public Boolean actionResource(UserResourceRequest request) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_RESOURCE_NOT_FOUND));
        UserResource userResource = UserResource.builder()
                .user(userHelper.getUserLogin())
                .actionType(ActionType.valueOf(request.getActionType()))
                .createdAt(LocalDateTime.now())
                .resource(resource)
                .active(true)
                .build();

        User userLoggedIn = userHelper.getUserLogin();
        if (request.getActionType().equalsIgnoreCase(ActionType.LIKE.toString())) {
            findAndDeleteUserResourceExist(userLoggedIn, request.getResourceId(), ActionType.UNLIKE);
            userResource = userResourceRepository.save(userResource);
        } else if (request.getActionType().equalsIgnoreCase(ActionType.UNLIKE.toString())) {
            findAndDeleteUserResourceExist(userLoggedIn, request.getResourceId(), ActionType.LIKE);
            userResource = userResourceRepository.save(userResource);
        } else if (request.getActionType().equalsIgnoreCase(ActionType.SAVED.toString())) {
            findAndDeleteUserResourceExist(userLoggedIn, request.getResourceId(), ActionType.UNSAVED);
            userResource = userResourceRepository.save(userResource);
        } else if (request.getActionType().equalsIgnoreCase(ActionType.UNSAVED.toString())) {
            findAndDeleteUserResourceExist(userLoggedIn, request.getResourceId(), ActionType.SAVED);
        } else if (request.getActionType().equalsIgnoreCase(ActionType.DOWNLOAD.toString())) {
            userResource = userResourceRepository.save(userResource);
        }
        return true;
    }

    @Override
    public org.springframework.core.io.Resource downloadResource(String fileName) {
        Resource document = resourceRepository.findByName(fileName)
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_RESOURCE_NOT_FOUND));

        User userLoggedIn = userHelper.getUserLogin();
        org.springframework.core.io.Resource resource;
        long pointRemain = userLoggedIn.getTotalPoint() - document.getPoint();
        if (pointRemain > 0) {
            resource = fileService.downloadFile(fileName);
            userLoggedIn.setTotalPoint(pointRemain);
            userRepository.save(userLoggedIn);
        } else throw ApiException.forBiddenException(messageException.MSG_FILE_DOWNLOAD_ERROR);

        boolean action = actionResource(UserResourceRequest.builder()
                .actionType(String.valueOf(ActionType.DOWNLOAD))
                .resourceId(document.getId())
                .build());
        if (!action) throw ApiException.badRequestException(messageException.MSG_INTERNAL_SERVER_ERROR);
        return resource;
    }
}
