package com.manga.server.features.manga.repository.getmangas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.manga.server.features.manga.controller.query.MangaQuery;
import com.manga.server.features.manga.enums.MangaFilter;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.shared.regex.RegexUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetMangaRepositoryImpl implements GetMangaRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<MangaModel> findAll(MangaQuery query, Pageable pageable) {

        List<Criteria> criteria = new ArrayList<>();

        addIdFilter(query, criteria);
        addSearchFilter(query, criteria);
        addLastUpdatedFilter(query, criteria);

        Query mongoQuery = new Query();
        if (!criteria.isEmpty()) {
            mongoQuery.addCriteria(
                    new Criteria().andOperator(criteria.toArray(new Criteria[0]))
            );
        }

        mongoQuery.with(pageable);

        List<MangaModel> result =
                mongoTemplate.find(mongoQuery, MangaModel.class);

        long total =
                mongoTemplate.count(
                        Query.of(mongoQuery).limit(-1).skip(-1),
                        MangaModel.class
                );

        return new PageImpl<>(result, pageable, total);
    }

    private void addIdFilter(MangaQuery query, List<Criteria> criteria) {
        if (query.getIds() != null && !query.getIds().isEmpty()) {
            criteria.add(
                    Criteria.where("id").in(query.getIds())
            );
        }
    }

    private void addSearchFilter(MangaQuery query, List<Criteria> criteria) {
        if (query.getSearch() != null && !query.getSearch().isBlank()) {
            String regex = RegexUtils.accentInsensitive(query.getSearch());
            criteria.add(
                    Criteria.where("name").regex(regex, "i")
            );
        }
    }

    private void addLastUpdatedFilter(MangaQuery query, List<Criteria> criteria) {
        if (query.getFilter() == MangaFilter.LAST_UPDATED) {
            long cutoff = System.currentTimeMillis() - 86_400_000;
            criteria.add(
                    Criteria.where("lastUpdated")
                            .gte(new Date(cutoff))
            );
        }
    }
}
