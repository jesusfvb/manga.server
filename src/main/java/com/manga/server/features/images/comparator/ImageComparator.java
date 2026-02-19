package com.manga.server.features.images.comparator;

import java.util.Comparator;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.manga.server.features.images.models.ImgModel;

public class ImageComparator implements Comparator<ImgModel> {

    private final Sort.Direction direction;

    public ImageComparator(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort.isEmpty()) {
            this.direction = Sort.Direction.ASC;
        } else {
            Sort.Order order = sort.stream().findFirst().orElse(null);
            this.direction = order == null ? Sort.Direction.ASC : order.getDirection();
        }
    }

    @Override
    public int compare(ImgModel o1, ImgModel o2) {
        int result = compareNumber(o1, o2);
        return direction == Sort.Direction.DESC ? -result : result;
    }

    private int compareNumber(ImgModel o1, ImgModel o2) {
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

    public static ImageComparator of(Pageable pageable) {
        return new ImageComparator(pageable);
    }
}

