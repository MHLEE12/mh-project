package com.mh.project.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;
    private int totalPostSize = 0;

    public List<Integer> getPaginationBarNumbers(int currentPageNum, int totalPages) {
        int startNum = Math.max(currentPageNum - (BAR_LENGTH / 2), 0);
        int endNum = Math.min(startNum + BAR_LENGTH, totalPages);

        this.totalPostSize = totalPages;

        return IntStream.range(startNum, endNum).boxed().toList();
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    }

    public int currentPostSize() {
        return this.totalPostSize;
    }
}
