package com.capstone.backend.repository.criteria;

import com.capstone.backend.entity.Tag;
import com.capstone.backend.model.dto.tag.PagingTagDTOResponse;
import com.capstone.backend.model.dto.tag.TagDTOFilter;
import com.capstone.backend.model.dto.tag.TagDTOResponse;
import com.capstone.backend.model.mapper.TagMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagCriteria {
    final EntityManager em;

    public PagingTagDTOResponse searchTag(TagDTOFilter tagDTOFilter) {
        StringBuilder sql = new StringBuilder("select t from Tag t where t.active = TRUE");
        Map<String, Object> params = new HashMap<>();

        sql.append(" and t.name like :name ");
        params.put("name", "%" + tagDTOFilter.getName() + "%");

        Query countQuery = em.createQuery(sql.toString().replace("select t", "select count(t.id)"));

        Long pageIndex = tagDTOFilter.getPageIndex();
        Long pageSize = tagDTOFilter.getPageSize();

        TypedQuery<Tag> tagTypedQuery = em.createQuery(sql.toString(), Tag.class);

        // Set param to query
        params.forEach((k, v) -> {
            tagTypedQuery.setParameter(k, v);
            countQuery.setParameter(k, v);
        });

        //paging
        tagTypedQuery.setFirstResult((int) ((pageIndex - 1) * pageSize));
        tagTypedQuery.setMaxResults(Math.toIntExact(pageSize));
        List<Tag> tagList = tagTypedQuery.getResultList();

        Long totalTag = (Long) countQuery.getSingleResult();
        Long totalPage = totalTag / pageSize;
        if (totalPage % pageSize != 0) {
            totalPage++;
        }

        List<TagDTOResponse> tagDTOResponseList = tagList.stream().map(TagMapper::toTagDTOResponse).toList();

        return PagingTagDTOResponse.builder()
                .totalElement(totalTag)
                .totalPage(totalPage)
                .data(tagDTOResponseList)
                .build();
    }
}
