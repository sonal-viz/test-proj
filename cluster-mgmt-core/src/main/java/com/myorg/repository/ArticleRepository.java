package com.myorg.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myorg.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
