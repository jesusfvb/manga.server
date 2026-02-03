package com.manga.server.features.manga.repository.getmangas;

import com.manga.server.features.manga.controller.MangaFilter;
import com.manga.server.features.manga.controller.MangaQuery;
import com.manga.server.features.manga.model.MangaModel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GetMangaRepositoryImpl implements GetMangaRepository {

    private final MongoTemplate mongoTemplate;


    @Override
    public Page<MangaModel> findAll(MangaQuery query, Pageable pageable) {

        List<Criteria> criteriaList = new ArrayList<>();

        if( query.getIds() != null && !query.getIds().isEmpty() ) {
            criteriaList.add(Criteria.where("id").in(query.getIds()));
        }

        if(query.getSearch() != null && !query.getSearch().isEmpty()){
            criteriaList.add(Criteria.where("name").regex(".*" + query.getSearch() + ".*", "i"));
        }

        if(query.getFilter() != null){
            if (query.getFilter() == MangaFilter.LAST_UPDATED) {
                long cutoff = System.currentTimeMillis() - 24L * 60 * 60 * 1000;
                criteriaList.add(Criteria.where("lastUpdated").gte(new java.util.Date(cutoff)));
            }
        }

        Query mangaQuery = new Query();
        if (!criteriaList.isEmpty()) {
            mangaQuery.addCriteria(new Criteria().andOperator(criteriaList));
        }

        mangaQuery.with(pageable);

        List<MangaModel> mangaModelList = mongoTemplate.find(mangaQuery, MangaModel.class);
        long total =
                mongoTemplate.count(
                        Query.of(mangaQuery).limit(-1).skip(-1),
                        MangaModel.class
                );
        return new PageImpl<>(mangaModelList, pageable, total);
    }
}
