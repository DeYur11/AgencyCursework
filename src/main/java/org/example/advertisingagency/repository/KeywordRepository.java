package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Integer> {
}