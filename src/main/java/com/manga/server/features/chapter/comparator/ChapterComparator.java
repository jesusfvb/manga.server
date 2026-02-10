package com.manga.server.features.chapter.comparator;

import java.util.Comparator;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.manga.server.features.chapter.models.ChapterModel;

public class ChapterComparator implements Comparator<ChapterModel> {

    private final Sort.Direction direction;

    public ChapterComparator(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort.isEmpty()) {
            this.direction = Sort.Direction.ASC;
        } else {
            Sort.Order order = sort.stream().findFirst().orElse(null);
            this.direction = order == null ? Sort.Direction.ASC : order.getDirection();
        }
    }

    @Override
    public int compare(ChapterModel o1, ChapterModel o2) {
        int result = compareNumber(o1, o2);
        return direction == Sort.Direction.DESC ? -result : result;
    }

    private int compareNumber(ChapterModel o1, ChapterModel o2) {
        if (o1.getNumber() == null && o2.getNumber() == null) {
            return 0;
        }
        if (o1.getNumber() == null) {
            return 1;
        }
        if (o2.getNumber() == null) {
            return -1;
        }
        return o1.getNumber().compareTo(o2.getNumber());
    }

    public static ChapterComparator of(Pageable pageable) {
        return new ChapterComparator(pageable);
    }
}
