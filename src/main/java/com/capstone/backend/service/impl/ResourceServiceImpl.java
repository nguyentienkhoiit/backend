package com.capstone.backend.service.impl;

import com.capstone.backend.entity.*;
import com.capstone.backend.entity.type.*;
import com.capstone.backend.exception.ApiException;
import com.capstone.backend.model.dto.comment.CommentDetailDTOResponse;
import com.capstone.backend.model.dto.materials.DataMaterialsDTOResponse;
import com.capstone.backend.model.dto.materials.MaterialsFilterDTORequest;
import com.capstone.backend.model.dto.resource.*;
import com.capstone.backend.model.mapper.ResourceMapper;
import com.capstone.backend.model.mapper.UserMapper;
import com.capstone.backend.repository.*;
import com.capstone.backend.repository.criteria.MaterialsCriteria;
import com.capstone.backend.repository.criteria.ResourceCriteria;
import com.capstone.backend.service.CommentService;
import com.capstone.backend.service.FileService;
import com.capstone.backend.service.ResourceService;
import com.capstone.backend.utils.Constants;
import com.capstone.backend.utils.DataHelper;
import com.capstone.backend.utils.MessageException;
import com.capstone.backend.utils.UserHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceServiceImpl implements ResourceService {
    ResourceRepository resourceRepository;
    UserResourceRepository userResourceRepository;
    CommentService commentService;
    FileService fileService;
    TagRepository tagRepository;
    LessonRepository lessonRepository;
    SubjectRepository subjectRepository;
    UserHelper userHelper;
    ResourceTagRepository resourceTagRepository;
    UserResourcePermissionRepository userResourcePermissionRepository;
    ResourceCriteria resourceCriteria;
    MessageException messageException;
    MaterialsCriteria materialsCriteria;

    private void saveToResourceTag(ResourceDTORequest request, Resource resource) {
        if (request.getTagList() != null) {
            List<ResourceTag> resourceTagList = new ArrayList<>();
            DataHelper.parseStringToLongSet(request.getTagList()).forEach(tag -> {
                Tag tagObject = tagRepository.findById(tag)
                        .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_TAG_NOT_FOUND));
                ResourceTag resourceTag = ResourceTag.builder()
                        .tag(tagObject)
                        .resource(resource)
                        .detailId(resource.getId())
                        .tableType(TableType.resource_tbl)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build();
                resourceTagList.add(resourceTag);
            });
            resource.setResourceTagList(resourceTagList);
        }
    }

    @Override
    public List<ResourceDTOResponse> uploadResource(ResourceDTORequest request, MultipartFile[] files) {
        List<ResourceDTOResponse> resourceDTOResponseList = new ArrayList<>();

        //get list file processed
        List<FileDTOResponse> fileDTOResponseList = fileService.uploadMultiFile(files);
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_SUBJECT_NOT_FOUND));

        Lesson lesson = lessonRepository.findById(request.getLessonId()).orElse(null);
        fileDTOResponseList.forEach(fileDTOResponse -> {
            //check document or media
            boolean status = ResourceType.getFeeList().stream()
                    .anyMatch(rt -> fileDTOResponse.getFileExtension().equalsIgnoreCase(rt.toString()));
            //if media will be free or document will have fee
            Long pointResource = status ? Constants.POINT_RESOURCE : 0;

            TabResourceType tabResourceType = TabResourceType
                    .findByTabResourceType(fileDTOResponse.getResourceType());
            Resource resource = Resource.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .resourceType(fileDTOResponse.getResourceType())
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .approveType(ApproveType.UNACCEPTED)
                    .visualType(VisualType.valueOf(request.getVisualType()))
                    .thumbnailSrc(fileDTOResponse.getThumbnailSrc())
                    .resourceSrc(fileDTOResponse.getResourceSrc())
                    .point(pointResource)
                    .size(fileDTOResponse.getSize())
                    .lesson(lesson)
                    .subject(subject)
                    .author(userHelper.getUserLogin())
                    .tabResourceType(tabResourceType)
                    .build();
            resource = resourceRepository.save(resource);
            resourceDTOResponseList.add(ResourceMapper.toResourceDTOResponse(resource));
            //save tag into resource tag table (search)
            saveToResourceTag(request, resource);
            //save resource table to database
            resourceRepository.save(resource);
        });
        return resourceDTOResponseList;
    }

    @Override
    public ResourceDetailDTOResponse getResourceDetailById(Long resourceId) {
        //user logged in
        User userLoggedIn = userHelper.getUserLogin();
        //like or unlike
        Boolean isLike = userResourceRepository
                .findUserResourceHasActionType(userLoggedIn.getId(), resourceId, ActionType.LIKE)
                .isPresent();
        //like or unlike
        Boolean isUnLike = userResourceRepository
                .findUserResourceHasActionType(userLoggedIn.getId(), resourceId, ActionType.UNLIKE)
                .isPresent();
        //number of like
        Long numberOfLike = userResourceRepository
                .countByActionTypeWithResource(ActionType.LIKE, resourceId);
        //number of unlike
        Long numberOfUnLike = userResourceRepository
                .countByActionTypeWithResource(ActionType.UNLIKE, resourceId);
        //find a resource
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_RESOURCE_NOT_FOUND));
        ResourceDTOResponse resourceDTOResponse = ResourceMapper.toResourceDTOResponse(resource);

        //get list comment root
        List<CommentDetailDTOResponse> commentDetailDTOResponseList = commentService
                .getListCommentDetailDTOResponse(resourceId);

        //get list tag name of this resource
        List<String> listTagNames = resourceTagRepository.findAllTagName(resourceId);

        List<ResourceViewDTOResponse> listResourceRelates = new ArrayList<>();
        //get status resource belongs or not belongs to a lesson
        boolean status = ResourceType.getResourceByLesson().stream()
                .anyMatch(rt -> rt.equals(resource.getResourceType()));
        //get save whether user logged in saved
        boolean isSave = userResourceRepository
                .findUserResourceByUserIdAndResourceIdAndActionType(userLoggedIn.getId(), resourceId, ActionType.SAVED).isPresent();
        if (status) {
            listResourceRelates = resourceTagRepository
                    .findAllResourceByLessonIdSameResourceType(resource.getResourceType(), resourceId, resource.getLesson().getId())
                    .stream().map(r -> ResourceMapper.toResourceViewDTOResponse(r, isSave))
                    .toList();
        } else {
            listResourceRelates = resourceTagRepository
                    .findAllResourceByTagNameSameResourceType(resource.getResourceType(), resourceId, listTagNames)
                    .stream().map(r -> ResourceMapper.toResourceViewDTOResponse(r, isSave))
                    .toList();
        }
        User owner = resource.getAuthor();

        return ResourceDetailDTOResponse.builder()
                .isLike(isLike)
                .isUnlike(isUnLike)
                .numberOfLike(numberOfLike)
                .numberOfUnlike(numberOfUnLike)
                .resourceDTOResponse(resourceDTOResponse)
                .commentDetailDTOResponses(commentDetailDTOResponseList)
                .listTagRelate(listTagNames)
                .listResourceRelates(listResourceRelates)
                .isSave(isSave)
                .owner(UserMapper.toUserDTOResponse(owner))
                .build();
    }

    @Override
    public PagingResourceDTOResponse searchMediaResource(ResourceMediaDTOFilter resourceDTOFilter) {
        Set<ResourceTag> resourceTags = resourceTagRepository
                .findResourceTagByTagIdAndTabResourceType(resourceDTOFilter.getListTags());

        List<ResourceMediaDTOCriteria> resourceMediaDTOCriteriaList = resourceTags.stream()
                .map(ResourceMapper::toResourceMediaDTOCriteria).toList();
        System.out.println(resourceMediaDTOCriteriaList);
        return resourceCriteria.searchMediaResource(resourceMediaDTOCriteriaList, resourceDTOFilter);
    }

    @Override
    public ResourceDTOUpdateResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_RESOURCE_NOT_FOUND));
        Long numberOfLike = userResourceRepository
                .countByActionTypeWithResource(ActionType.LIKE, id);
        ResourceDTOUpdateResponse resourceDTOUpdateResponse = ResourceMapper.toResourceDTOUpdateResponse(resource);
        resourceDTOUpdateResponse.setNumberOfLike(numberOfLike);
        return resourceDTOUpdateResponse;
    }

    @Override
    public DataMaterialsDTOResponse searchMaterials(MaterialsFilterDTORequest request) {
        return materialsCriteria.searchMaterials(request);
    }
}
