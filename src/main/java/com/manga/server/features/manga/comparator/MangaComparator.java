package com.manga.server.features.manga.comparator;

import com.manga.server.features.manga.enums.MangaSortField;
import com.manga.server.features.manga.model.MangaModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Comparator;

/**
 * Comparador personalizado para ordenar entidades MangaModel
 * basado en los parámetros de ordenamiento especificados en un Pageable.
 */
public class MangaComparator implements Comparator<MangaModel> {

    private final MangaSortField sortField;
    private final Sort.Direction direction;

    public MangaComparator(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort.isEmpty()) {
            this.sortField = MangaSortField.TITLE;
            this.direction = Sort.Direction.ASC;
        } else {
            Sort.Order order = sort.stream().findFirst().orElse(null);
            if (order == null) {
                this.sortField = MangaSortField.TITLE;
                this.direction = Sort.Direction.ASC;
            } else {
                this.sortField = MangaSortField.fromFieldName(order.getProperty().toUpperCase());
                this.direction = order.getDirection();
            }
        }
    }


    @Override
    public int compare(MangaModel o1, MangaModel o2) {
        int result = switch (sortField) {
            case TITLE -> compareTitle(o1, o2);
            case LAST_UPDATED -> compareLastUpdated(o1, o2);
        };

        return direction == Sort.Direction.DESC ? -result : result;
    }


    private int compareTitle(MangaModel o1, MangaModel o2) {
        if (o1.getName() == null && o2.getName() == null) {
            return 0;
        }
        if (o1.getName() == null) {
            return 1;
        }
        if (o2.getName() == null) {
            return -1;
        }
        return o1.getName().compareToIgnoreCase(o2.getName());
    }


    private int compareLastUpdated(MangaModel o1, MangaModel o2) {
        if (o1.getLastUpdated() == null && o2.getLastUpdated() == null) {
            return 0;
        }
        if (o1.getLastUpdated() == null) {
            return 1;
        }
        if (o2.getLastUpdated() == null) {
            return -1;
        }
        return o1.getLastUpdated().compareTo(o2.getLastUpdated());
    }


    // ...existing code...

    public static MangaComparator of(Pageable pageable) {
        return new MangaComparator(pageable);
    }
}
