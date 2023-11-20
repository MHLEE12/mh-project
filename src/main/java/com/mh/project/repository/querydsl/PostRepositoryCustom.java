package com.mh.project.repository.querydsl;

import java.util.List;

public interface PostRepositoryCustom {
    List<String> findAllDistinctHashtags();
}
