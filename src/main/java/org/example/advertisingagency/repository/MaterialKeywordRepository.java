package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Keyword;
import org.example.advertisingagency.model.MaterialKeyword;
import org.example.advertisingagency.model.MaterialKeywordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialKeywordRepository extends JpaRepository<MaterialKeyword, MaterialKeywordId> {
    List<MaterialKeyword> findByKeywordID(Keyword keyword);
}