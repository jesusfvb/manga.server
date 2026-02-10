package com.manga.server.features.chapter.repository.getchapters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.manga.server.features.chapter.controller.querty.ChapterQuery;
import com.manga.server.features.chapter.models.ChapterModel;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetChapterRepositoryImpl implements GetChapterRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ChapterModel> findAll(String mangaId, ChapterQuery query, Pageable pageable) {
        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("mangaId").is(mangaId));
        addIdsFilter(query, criteria);

        Query mongoQuery = new Query();
        if (!criteria.isEmpty()) {
            mongoQuery.addCriteria(
                    new Criteria().andOperator(criteria.toArray(new Criteria[0]))
            );
        }

        mongoQuery.with(pageable);

        List<ChapterModel> result =
                mongoTemplate.find(mongoQuery, ChapterModel.class);

        long total =
                mongoTemplate.count(
                        Query.of(mongoQuery).limit(-1).skip(-1),
                        ChapterModel.class
                );

        return new PageImpl<>(result, pageable, total);
    }

    private void addIdsFilter(ChapterQuery query, List<Criteria> criteria) {
        if (query != null && query.getIds() != null && !query.getIds().isEmpty()) {
            criteria.add(
                    Criteria.where("id").in(query.getIds())
            );
        }
    }
}
