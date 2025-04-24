package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
}