package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.MaterialKeyword;
import org.example.advertisingagency.model.MaterialKeywordId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialKeywordRepository extends JpaRepository<MaterialKeyword, MaterialKeywordId> {
}