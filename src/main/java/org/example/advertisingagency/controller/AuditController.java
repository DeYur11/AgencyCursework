package org.example.advertisingagency.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.repository.AuditLogRepository;
import org.example.advertisingagency.repository.MaterialReviewRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.example.advertisingagency.repository.WorkerRepository;

import org.example.advertisingagency.service.logs.AuditLogPublisher;
import org.example.advertisingagency.service.logs.AuditLogService;
import org.example.advertisingagency.service.material.MaterialReviewService;
import org.example.advertisingagency.service.material.MaterialService;
import org.example.advertisingagency.service.project.ProjectService;
import org.example.advertisingagency.service.service.ServicesInProgressService;
import org.example.advertisingagency.service.task.TaskService;
import org.example.advertisingagency.service.user.WorkerService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class AuditController {

    private final AuditLogService auditLogService;
    private final AuditLogPublisher auditLogPublisher;
    private final WorkerRepository workerRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final WorkerService workerService;
    private final TaskService taskService;
    private final MaterialService materialService;
    private final ServicesInProgressService servicesInProgressService;
    private final MaterialReviewRepository materialReviewRepository;
    private final MaterialReviewService materialReviewService;
    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogService auditLogService,
                           AuditLogPublisher auditLogPublisher,
                           WorkerRepository workerRepository, ProjectRepository projectRepository, ProjectService projectService, WorkerService workerService, TaskService taskService, MaterialService materialService, ServicesInProgressService servicesInProgressService, MaterialReviewRepository materialReviewRepository, MaterialReviewService materialReviewService, AuditLogRepository auditLogRepository) {
        this.auditLogService = auditLogService;
        this.auditLogPublisher = auditLogPublisher;
        this.workerRepository = workerRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.workerService = workerService;
        this.taskService = taskService;
        this.materialService = materialService;
        this.servicesInProgressService = servicesInProgressService;
        this.materialReviewRepository = materialReviewRepository;
        this.materialReviewService = materialReviewService;
        this.auditLogRepository = auditLogRepository;
    }

    @QueryMapping
    public List<AuditLog> auditLogsByTaskIds(@Argument List<AuditEntity> entityList,
                                             @Argument List<Integer> taskIds) {
        return auditLogRepository.findTop100ByEntityInAndTaskIdInOrderByTimestampDesc(entityList, taskIds);
    }

    @QueryMapping
    public List<AuditLog> auditLogsByMaterialIds(@Argument List<AuditEntity> entityList,
                                                 @Argument List<Integer> materialIds) {
        log.info("Client connected");
        return auditLogRepository.findTop100ByEntityInAndMaterialIdInOrderByTimestampDesc(entityList, materialIds);
    }

    @QueryMapping
    public List<AuditLog> auditLogsByProjectIds(@Argument List<AuditEntity> entityList,
                                                @Argument List<Integer> projectIds) {
        log.info("Client for projects connected");
        return auditLogRepository.findTop100ByEntityInAndProjectIdInOrderByTimestampDesc(entityList, projectIds);
    }

    @QueryMapping
    public List<AuditLog> auditLogsByServiceInProgressIds(@Argument List<AuditEntity> entityList,
                                                          @Argument List<Integer> serviceIds) {
        return auditLogRepository.findTop100ByEntityInAndServiceInProgressIdInOrderByTimestampDesc(entityList, serviceIds);
    }

    @SubscriptionMapping
    public Flux<AuditLog> onReviewAuditLogByMaterialIds(@Argument List<Integer> materialIds) {
        log.info("Client for reviews connected");
        return auditLogPublisher.getFilteredStream(log ->
                log.getEntity() == AuditEntity.MATERIAL_REVIEW &&
                        log.getMaterialId() != null &&
                        materialIds.contains(log.getMaterialId())
        );
    }

    @SubscriptionMapping
    public Publisher<AuditLog> onAuditLogByTaskIds(@Argument List<AuditEntity> entityList,
                                                   @Argument List<Integer> taskIds) {
        log.info("Client for tasks connected");
        return auditLogPublisher.getFilteredStream(log -> log.getTaskId() != null &&
                        taskIds.contains(log.getTaskId()) &&
                        entityList.contains(log.getEntity()));
    }

    @SubscriptionMapping
    public Publisher<AuditLog> onAuditLogByMaterialIds(@Argument List<AuditEntity> entityList,
                                                       @Argument List<Integer> materialIds) {
        log.info("Client for materials connected");
        return auditLogPublisher.getFilteredStream(log -> log.getMaterialId() != null &&
                        materialIds.contains(log.getMaterialId()) &&
                        entityList.contains(log.getEntity()));
    }

    @SubscriptionMapping
    public Publisher<AuditLog> onAuditLogByProjectIds(@Argument List<AuditEntity> entityList,
                                                      @Argument List<Integer> projectIds) {
        return auditLogPublisher.getFilteredStream(log -> log.getProjectId() != null &&
                        projectIds.contains(log.getProjectId()) &&
                        entityList.contains(log.getEntity()));
    }

    @SubscriptionMapping
    public Publisher<AuditLog> onAuditLogByServiceInProgressIds(@Argument List<AuditEntity> entityList,
                                                                @Argument List<Integer> serviceIds) {
        return auditLogPublisher.getFilteredStream(log -> log.getServiceInProgressId() != null &&
                        serviceIds.contains(log.getServiceInProgressId()) &&
                        entityList.contains(log.getEntity()));
    }

    @SchemaMapping(typeName = "AuditLog", field = "worker")
    public Worker getWorker(AuditLog log) {
        return log.getWorkerId() != null
                ? workerService.getWorkerById(log.getWorkerId())
                : null;
    }

    @SchemaMapping(typeName = "AuditLog", field = "project")
    public Project getProject(AuditLog log) {
        return log.getProjectId() != null
                ? projectService.getProjectById(log.getProjectId())
                : null;
    }

    @SchemaMapping(typeName = "AuditLog", field = "task")
    public Task getTask(AuditLog log) {
        return log.getTaskId() != null
                ? taskService.getTaskById(log.getTaskId())
                : null;
    }

    @SchemaMapping(typeName = "AuditLog", field = "material")
    public Material getMaterial(AuditLog log) {
        return log.getMaterialId() != null
                ? materialService.getMaterialById(log.getMaterialId())
                : null;
    }

    @SchemaMapping(typeName = "AuditLog", field = "serviceInProgress")
    public ServicesInProgress getServiceInProgress(AuditLog log) {
        return log.getMaterialId() != null
                ? servicesInProgressService.getServicesInProgressById(log.getServiceInProgressId())
                : null;
    }

    @SchemaMapping(typeName = "AuditLog", field = "review")
    public MaterialReview getReview(AuditLog log) {
        return log.getMaterialId() != null
                ? materialReviewService.getMaterialReviewById(log.getMaterialId())
                : null;
    }
}
