package com.capstone.backend.repository.criteria;

import com.capstone.backend.entity.Resource;
import com.capstone.backend.entity.User;
import com.capstone.backend.entity.type.ActionType;
import com.capstone.backend.entity.type.TableType;
import com.capstone.backend.model.dto.resource.PagingResourceDTOResponse;
import com.capstone.backend.model.dto.resource.ResourceMediaDTOCriteria;
import com.capstone.backend.model.dto.resource.ResourceMediaDTOFilter;
import com.capstone.backend.model.dto.resource.ResourceViewDTOResponse;
import com.capstone.backend.model.mapper.ResourceMapper;
import com.capstone.backend.repository.UserResourceRepository;
import com.capstone.backend.utils.UserHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceCriteria {
    EntityManager em;
    UserHelper userHelper;
    UserResourceRepository userResourceRepository;

    public PagingResourceDTOResponse searchMediaResource(
            List<ResourceMediaDTOCriteria> resourceMediaDTOCriteriaList,
            ResourceMediaDTOFilter resourceMediaDTOFilter
    ) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        List<ResourceMediaDTOCriteria> listClassTags = resourceMediaDTOCriteriaList.stream()
                .filter(rm -> rm.getTableType() == TableType.class_tbl).toList();
        if (!listClassTags.isEmpty()) {
            Set<Long> listClassId = listClassTags.stream()
                    .map(ResourceMediaDTOCriteria::getDetailId)
                    .collect(Collectors.toSet());
            sql.append("( select re.* from resource_tbl re " +
                    "left join lesson_tbl le on le.id = re.lesson_id " +
                    "left join chapter_tbl cha on le.chapter_id = cha.id " +
                    "left join book_volume_tbl bv on cha.book_volume_id = bv.id " +
                    "left join subject_tbl su on bv.subject_id = su.id " +
                    "left join book_series_tbl bs on su.book_series_id = bs.id " +
                    "left join class_tbl c on bs.class_id = c.id " +
                    "where 1 = 1 and c.id in (:listClassId) ) ");
            params.put("listClassId", listClassId);
        }

        List<ResourceMediaDTOCriteria> listBookSeriesTags = resourceMediaDTOCriteriaList.stream()
                .filter(rm -> rm.getTableType() == TableType.book_series_tbl).toList();
        if (!listBookSeriesTags.isEmpty()) {
            Set<Long> listBookSeriesId = listBookSeriesTags.stream()
                    .map(ResourceMediaDTOCriteria::getDetailId)
                    .collect(Collectors.toSet());
            if (!sql.isEmpty()) sql.append(" union ");
            sql.append("( select re.* from resource_tbl re " +
                    "left join lesson_tbl le on le.id = re.lesson_id " +
                    "left join chapter_tbl cha on le.chapter_id = cha.id " +
                    "left join book_volume_tbl bv on cha.book_volume_id = bv.id " +
                    "left join subject_tbl su on bv.subject_id = su.id " +
                    "left join book_series_tbl bs on su.book_series_id = bs.id " +
                    "where 1 = 1 and bs.id in (:listBookSeriesId) )");
            params.put("listBookSeriesId", listBookSeriesId);
        }

        List<ResourceMediaDTOCriteria> listSubjectTags = resourceMediaDTOCriteriaList.stream()
                .filter(rm -> rm.getTableType() == TableType.subject_tbl).toList();
        if (!listSubjectTags.isEmpty()) {
            Set<Long> listSubjectId = listSubjectTags.stream()
                    .map(ResourceMediaDTOCriteria::getDetailId)
                    .collect(Collectors.toSet());
            if (!sql.isEmpty()) sql.append(" union ");
            sql.append("( select re.* from resource_tbl re " +
                    "left join lesson_tbl le on le.id = re.lesson_id " +
                    "left join chapter_tbl cha on le.chapter_id = cha.id " +
                    "left join book_volume_tbl bv on cha.book_volume_id = bv.id " +
                    "left join subject_tbl su on bv.subject_id = su.id " +
                    "where 1 = 1 and su.id in (:listSubjectId) )");
            params.put("listSubjectId", listSubjectId);
        }

        List<ResourceMediaDTOCriteria> listBookVolumeTags = resourceMediaDTOCriteriaList.stream()
                .filter(rm -> rm.getTableType() == TableType.book_volume_tbl).toList();
        if (!listBookVolumeTags.isEmpty()) {
            Set<Long> listBookVolumeId = listBookSeriesTags.stream()
                    .map(ResourceMediaDTOCriteria::getDetailId)
                    .collect(Collectors.toSet());
            if (!sql.isEmpty()) sql.append(" union ");
            sql.append("( select re.* from resource_tbl re " +
                    "left join lesson_tbl le on le.id = re.lesson_id " +
                    "left join chapter_tbl cha on le.chapter_id = cha.id " +
                    "left join book_volume_tbl bv on cha.book_volume_id = bv.id " +
                    "where 1 = 1 and bv.id in (:listBookVolumeId) )");
            params.put("listBookVolumeId", listBookVolumeId);
        }

        List<ResourceMediaDTOCriteria> listChapterTags = resourceMediaDTOCriteriaList.stream()
                .filter(rm -> rm.getTableType() == TableType.chapter_tbl).toList();
        if (!listChapterTags.isEmpty()) {
            Set<Long> listChapterId = listChapterTags.stream()
                    .map(ResourceMediaDTOCriteria::getDetailId)
                    .collect(Collectors.toSet());
            if (!sql.isEmpty()) sql.append(" union ");
            sql.append("( select re.* from resource_tbl re " +
                    "left join lesson_tbl le on le.id = re.lesson_id " +
                    "left join chapter_tbl cha on le.chapter_id = cha.id " +
                    "where 1 = 1 and cha.id in (:listChapterId) )");
            params.put("listChapterId", listChapterId);
        }

        List<ResourceMediaDTOCriteria> listLessonTags = resourceMediaDTOCriteriaList.stream()
                .filter(rm -> rm.getTableType() == TableType.lesson_tbl).toList();
        if (!listLessonTags.isEmpty()) {
            Set<Long> listLessonId = listLessonTags.stream()
                    .map(ResourceMediaDTOCriteria::getDetailId)
                    .collect(Collectors.toSet());
            if (!sql.isEmpty()) sql.append(" union ");
            sql.append("( select re.* from resource_tbl re " +
                    "left join lesson_tbl le on le.id = re.lesson_id " +
                    "where 1 = 1 and le.id in (:listLessonId) )");
            params.put("listLessonId", listLessonId);
        }

        List<ResourceMediaDTOCriteria> listResourceTags = resourceMediaDTOCriteriaList.stream()
                .filter(rm -> rm.getTableType() == TableType.resource_tbl).toList();
        if (!listResourceTags.isEmpty()) {
            Set<Long> listResourceId = listResourceTags.stream()
                    .map(ResourceMediaDTOCriteria::getDetailId).collect(Collectors.toSet());
            if (!sql.isEmpty()) sql.append(" union ");
            sql.append("( select re.* from resource_tbl re " +
                    "where 1 = 1 and re.id in (:listResourceId) )");
            params.put("listResourceId", listResourceId);
        }

        Long pageIndex = resourceMediaDTOFilter.getPageIndex();
        Long pageSize = resourceMediaDTOFilter.getPageSize();

        NativeQuery<Resource> nativeQuery = (NativeQuery<Resource>) em.createNativeQuery(sql.toString(), Resource.class);

        params.forEach(nativeQuery::setParameter);
        TypedQuery<Resource> resourceTypedQuery = nativeQuery.unwrap(TypedQuery.class);


        resourceTypedQuery.setFirstResult((int) ((pageIndex - 1) * pageSize));
        resourceTypedQuery.setMaxResults(Math.toIntExact(pageSize));
        List<Resource> resourceList = resourceTypedQuery.getResultList();

        resourceList = resourceList.stream()
                .filter(resource -> {
                    return resource.getName().contains(resourceMediaDTOFilter.getName())
                            && resource.getTabResourceType() == resourceMediaDTOFilter.getTabResourceType();
                })
                .toList();

        long totalResource = (long) resourceList.size();
        long totalPage = totalResource / pageSize;
        if (totalResource % pageSize != 0) {
            totalPage++;
        }

        User userLoggedIn = userHelper.getUserLogin();
        List<ResourceViewDTOResponse> resourceViewDTOResponses = resourceList.stream()
                .map(resource -> {
                    boolean isSave = userResourceRepository
                            .findUserResourceHasActionType(userLoggedIn.getId(), resource.getId(), ActionType.SAVED)
                            .isPresent();
                    return ResourceMapper.toResourceViewDTOResponse(resource, isSave);
                })
                .toList();

        return PagingResourceDTOResponse.builder()
                .totalElement(totalResource)
                .totalPage(totalPage)
                .data(resourceViewDTOResponses)
                .build();
    }
}
