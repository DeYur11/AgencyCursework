package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ServicesInProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicesInProgressRepository extends JpaRepository<ServicesInProgress, Integer> {
    List<ServicesInProgress> findAllByProjectServiceID_Id(Integer projectServiceId);
    List<ServicesInProgress> findAllByStatusID_Id(Integer statusId);
    List<ServicesInProgress> findAllByProjectServiceID_IdIn(List<Integer> projectServiceIds);
}