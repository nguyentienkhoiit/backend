package com.capstone.backend.service.impl;

import com.capstone.backend.entity.*;
import com.capstone.backend.entity.type.*;
import com.capstone.backend.exception.ApiException;
import com.capstone.backend.model.dto.comment.CommentDetailDTOResponse;
import com.capstone.backend.model.dto.materials.DataMaterialsDTOResponse;
import com.capstone.backend.model.dto.materials.MaterialsFilterDTORequest;
import com.capstone.backend.model.dto.resource.*;
import com.capstone.backend.model.dto.tag.TagSuggestDTORequest;
import com.capstone.backend.model.dto.tag.TagSuggestDTOResponse;
import com.capstone.backend.model.dto.userresource.UserResourceRequest;
import com.capstone.backend.model.mapper.ResourceMapper;
import com.capstone.backend.model.mapper.TagMapper;
import com.capstone.backend.model.mapper.UserMapper;
import com.capstone.backend.model.mapper.UserResourceMapper;
import com.capstone.backend.repository.*;
import com.capstone.backend.repository.criteria.MaterialsCriteria;
import com.capstone.backend.repository.criteria.ResourceCriteria;
import com.capstone.backend.service.CommentService;
import com.capstone.backend.service.FileService;
import com.capstone.backend.service.ResourceService;
import com.capstone.backend.service.UserResourceService;
import com.capstone.backend.utils.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static com.capstone.backend.utils.Constants.CREATOR_RESOURCE_PERMISSION;
import static com.capstone.backend.utils.Constants.CREATOR_RESOURCE_PERMISSION_MESSAGE;

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
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    CheckPermissionResource checkPermissionResource;
    UserResourceService userResourceService;

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
        List<FileDTOResponse> fileDTOResponseList = fileService.uploadMultiFile(files, request.getLessonId());
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_SUBJECT_NOT_FOUND));

        Lesson lesson;
        if (request.getLessonId() != null) {
            lesson = lessonRepository
                    .findById(request.getLessonId())
                    .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_LESSON_NOT_FOUND));
        } else {
            lesson = null;
        }
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
            //save user permission resource
            UserResourcePermission permission = UserResourcePermission.builder()
                    .user(userHelper.getUserLogin())
                    .resource(resource)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .permission(CREATOR_RESOURCE_PERMISSION)
                    .build();
            userResourcePermissionRepository.save(permission);
        });
        return resourceDTOResponseList;
    }

    @Override
    public ResourceDetailDTOResponse getResourceDetailById(Long resourceId) {
        //user logged in
        User userLoggedIn = userHelper.getUserLogin();

        //find a resource
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_RESOURCE_NOT_FOUND));
        ResourceDTOResponse resourceDTOResponse = ResourceMapper.toResourceDTOResponse(resource);

        //check permission resource
        boolean isPermission = checkPermissionResource
                .needCheckPermissionResource(userLoggedIn, resource, PermissionResourceType.V);
        if (!isPermission)
            throw ApiException.forBiddenException(messageException.MSG_NO_PERMISSION);


        //like or unlike
        Boolean isLike = userLoggedIn != null && userResourceRepository
                .findUserResourceHasActionType(userLoggedIn.getId(), resourceId, ActionType.LIKE)
                .isPresent();
        //like or unlike
        Boolean isUnLike = userLoggedIn != null && userResourceRepository
                .findUserResourceHasActionType(userLoggedIn.getId(), resourceId, ActionType.UNLIKE)
                .isPresent();
        //number of like
        Long numberOfLike = userResourceRepository
                .countByActionTypeWithResource(ActionType.LIKE, resourceId);
        //number of unlike
        Long numberOfUnLike = userResourceRepository
                .countByActionTypeWithResource(ActionType.UNLIKE, resourceId);

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
        boolean isSave = userLoggedIn != null && userResourceRepository
                .findUserResourceHasActionType(userLoggedIn.getId(), resourceId, ActionType.SAVED).isPresent();
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

        boolean action = userResourceService.actionResource(UserResourceRequest.builder()
                .actionType(String.valueOf(ActionType.DOWNLOAD))
                .resourceId(document.getId())
                .build());
        if (!action) throw ApiException.badRequestException(messageException.MSG_INTERNAL_SERVER_ERROR);
        return resource;
    }

    @Override
    public Boolean shareResource(ResourceSharedDTORequest request) {
        var userLoggedIn = userHelper.getUserLogin();
        var ownerResource = userResourcePermissionRepository.getUserOwnerResource(request.getResourceId());
        var resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_RESOURCE_NOT_FOUND));

        //owner of resource can view
        if (!Objects.equals(userLoggedIn.getId(), ownerResource.getId())) {
            throw ApiException.internalServerException(messageException.MSG_NO_PERMISSION);
        }

        //check permission resource
        boolean isPermission = checkPermissionResource
                .needCheckPermissionResource(userLoggedIn, resource, PermissionResourceType.C);
        if (!isPermission)
            throw ApiException.forBiddenException(messageException.MSG_NO_PERMISSION);

        resource.setVisualType(request.getVisualType());
        resource = resourceRepository.save(resource);

        var userResourcePermissions = new ArrayList<>(userResourcePermissionRepository
                .findByResource(resource.getId(), userLoggedIn.getId()).stream()
                .toList());

        var listUserIdShared = userResourcePermissions.stream().map(upr -> upr.getUser().getId()).toList();
        var listUserIdShareNew = request.getUserShareIds();


        var listAdded = listUserIdShareNew.stream().filter(e -> !listUserIdShared.contains(e)).toList();
        var listDeleted = listUserIdShared.stream().filter(e -> !listUserIdShareNew.contains(e)).toList();

        //list user permission resource added
        Resource finalResource = resource;
        if (!listAdded.isEmpty()) {
            var userResourcePermissionAdds = listAdded.stream()
                    .map(e -> {
                        User user = userRepository.findById(e)
                                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_USER_NOT_FOUND));
                        return UserResourceMapper.toUserResourcePermission(user, finalResource);
                    })
                    .toList();
            userResourcePermissionRepository.saveAll(userResourcePermissionAdds);
        }

        //list user permission resource deleted
        if (!listDeleted.isEmpty()) {
            listDeleted.stream()
                    .map(e -> {
                        User user = userRepository.findById(e)
                                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_USER_NOT_FOUND));
                        return userResourcePermissionRepository.findByUserAndResource(user, finalResource);
                    })
                    .forEach(e -> {
                        assert e.orElse(null) != null;
                        userResourcePermissionRepository.delete(e.orElse(null));
                    });
        }

        return true;
    }

    @Override
    public ResourceSharedDTOResponse viewResourceShareById(Long resourceId) {
        var userLoggedIn = userHelper.getUserLogin();
        var ownerResource = userResourcePermissionRepository.getUserOwnerResource(resourceId);
        var resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_RESOURCE_NOT_FOUND));

        //owner of resource can view
        if (!Objects.equals(userLoggedIn.getId(), ownerResource.getId())) {
            throw ApiException.internalServerException(messageException.MSG_NO_PERMISSION);
        }

        //check permission resource
        boolean isPermission = checkPermissionResource
                .needCheckPermissionResource(userLoggedIn, resource, PermissionResourceType.C);
        if (!isPermission)
            throw ApiException.forBiddenException(messageException.MSG_NO_PERMISSION);

        var userResourcePermissions = new ArrayList<>(userResourcePermissionRepository
                .findByResource(resource.getId(), ownerResource.getId()).stream()
                .sorted(Comparator.comparing(up -> up.getUser().getUsername()))
                .toList());

        //push owner the first
        var userResourcePermissionOwner = userResourcePermissionRepository.findByUserAndResource(userLoggedIn, resource)
                .orElseThrow(() -> ApiException.notFoundException(messageException.MSG_PERMISSION_RESOURCE_NOT_FOUND));
        userResourcePermissionOwner.getUser().setUsername(ownerResource.getUsername() + "(you)");
        userResourcePermissions.add(0, userResourcePermissionOwner);

        var userSharedDTOResponses = userResourcePermissions.stream()
                .map(user -> ResourceMapper.toUserSharedDTOResponse(user.getUser(), Constants.SHARED_RESOURCE_PERMISSION_MESSAGE))
                .peek(user -> {
                    if (Objects.equals(user.getUserShareId(), ownerResource.getId())) {
                        user.setPermission(CREATOR_RESOURCE_PERMISSION_MESSAGE);
                    }
                })
                .toList();

        return ResourceSharedDTOResponse.builder()
                .resourceId(resource.getId())
                .resourceName(resource.getName())
                .userSharedDTOResponses(userSharedDTOResponses)
                .visualType(resource.getVisualType())
                .build();
    }

    @Override
    public List<UserSharedDTOResponse> suggestionUserShare(String text) {
        User userLoggedIn = userHelper.getUserLogin();
        List<User> users = userRoleRepository.findTeacherByUsernameOrEmailContaining(text, userLoggedIn.getId());
        return users.stream()
                .map(user -> ResourceMapper.toUserSharedDTOResponse(user, Constants.SHARED_RESOURCE_PERMISSION_MESSAGE))
                .toList();
    }

    @Override
    public List<TagSuggestDTOResponse> getListTagsSuggest(TagSuggestDTORequest request) {
        return tagRepository
                .findAllByNameContainsAndActive(request.getTagSuggest()).stream()
                .map(TagMapper::toTagSuggestDTOResponse)
                .toList();
    }
}
