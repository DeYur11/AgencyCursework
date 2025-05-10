package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialReviewRepository extends JpaRepository<MaterialReview, Integer> {
  List<MaterialReview> findAllByMaterial_Id(int id);
  List<MaterialReview> findAllByReviewer_Id(int id);
  List<MaterialReview> findAllByMaterialSummary_Id(Integer materialSummaryId);
  List<MaterialReview> findByMaterialIdIn(List<Integer> materialIds);
}