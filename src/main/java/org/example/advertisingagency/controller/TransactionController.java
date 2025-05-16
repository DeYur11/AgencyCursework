package org.example.advertisingagency.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.TransactionLog;
import org.example.advertisingagency.publisher.TransactionPublisher;
import org.example.advertisingagency.repository.WorkerRepository;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.example.advertisingagency.service.material.MaterialReviewService;
import org.example.advertisingagency.service.material.MaterialService;
import org.example.advertisingagency.service.project.ProjectService;
import org.example.advertisingagency.service.service.ServicesInProgressService;
import org.example.advertisingagency.service.task.TaskService;
import org.example.advertisingagency.service.user.WorkerService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Controller for transaction logs and subscriptions.
 * This replaces the AuditController with enhanced functionality.
 */
@Slf4j
@Controller
public class TransactionController {

    private final TransactionLogService transactionLogService;
    private final TransactionPublisher transactionPublisher;
    private final WorkerService workerService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final MaterialService materialService;
    private final ServicesInProgressService servicesInProgressService;
    private final MaterialReviewService materialReviewService;

    public TransactionController(
            TransactionLogService transactionLogService,
            TransactionPublisher transactionPublisher,
            WorkerService workerService,
            ProjectService projectService,
            TaskService taskService,
            MaterialService materialService,
            ServicesInProgressService servicesInProgressService,
            MaterialReviewService materialReviewService) {
        this.transactionLogService = transactionLogService;
        this.transactionPublisher = transactionPublisher;
        this.workerService = workerService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.materialService = materialService;
        this.servicesInProgressService = servicesInProgressService;
        this.materialReviewService = materialReviewService;
    }

    /**
     * Get transaction logs for task IDs.
     */
    @QueryMapping
    public List<TransactionLog> transactionsByTaskIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> taskIds) {
        log.info("Fetching transactions for tasks: {}", taskIds);
        return transactionLogService.findByTaskIds(entityList, taskIds);
    }

    /**
     * Get transaction logs for material IDs.
     */
    @QueryMapping
    public List<TransactionLog> transactionsByMaterialIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> materialIds) {
        log.info("Fetching transactions for materials: {}", materialIds);
        return transactionLogService.findByMaterialIds(entityList, materialIds);
    }

    /**
     * Get transaction logs for project IDs.
     */
    @QueryMapping
    public List<TransactionLog> transactionsByProjectIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> projectIds) {
        log.info("Fetching transactions for projects: {}", projectIds);
        return transactionLogService.findByProjectIds(entityList, projectIds);
    }

    /**
     * Get transaction logs for service in progress IDs.
     */
    @QueryMapping
    public List<TransactionLog> transactionsByServiceInProgressIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> serviceIds) {
        log.info("Fetching transactions for services: {}", serviceIds);
        return transactionLogService.findByServiceInProgressIds(entityList, serviceIds);
    }

    /**
     * Rollback a transaction.
     */
    @MutationMapping
    public TransactionLog rollbackTransaction(
            @Argument String transactionId,
            @Argument String username) {
        log.info("Rolling back transaction: {}", transactionId);
        transactionLogService.rollbackTransaction(transactionId);
        return null; // The actual response will be sent via subscription
    }

    /**
     * Subscribe to transaction logs for material reviews.
     */
    @SubscriptionMapping
    public Flux<TransactionLog> onTransactionByMaterialReviews(
            @Argument List<Integer> materialIds) {
        log.info("Client subscribed to material review transactions for materials: {}", materialIds);
        return transactionPublisher.getFilteredStream(log ->
                log.getEntityType() == AuditEntity.MATERIAL_REVIEW &&
                        log.getMaterialId() != null &&
                        materialIds.contains(log.getMaterialId())
        );
    }

    /**
     * Subscribe to transaction logs for tasks.
     */
    @SubscriptionMapping
    public Publisher<TransactionLog> onTransactionByTaskIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> taskIds) {
        log.info("Client subscribed to transactions for tasks: {}", taskIds);
        return transactionPublisher.getFilteredStream(log ->
                log.getTaskId() != null &&
                        taskIds.contains(log.getTaskId()) &&
                        entityList.contains(log.getEntityType()));
    }

    /**
     * Subscribe to transaction logs for materials.
     */
    @SubscriptionMapping
    public Publisher<TransactionLog> onTransactionByMaterialIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> materialIds) {
        log.info("Client subscribed to transactions for materials: {}", materialIds);
        return transactionPublisher.getFilteredStream(log ->
                log.getMaterialId() != null &&
                        materialIds.contains(log.getMaterialId()) &&
                        entityList.contains(log.getEntityType()));
    }

    /**
     * Subscribe to transaction logs for projects.
     */
    @SubscriptionMapping
    public Publisher<TransactionLog> onTransactionByProjectIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> projectIds) {
        log.info("Client subscribed to transactions for projects: {}", projectIds);
        return transactionPublisher.getFilteredStream(log ->
                log.getProjectId() != null &&
                        projectIds.contains(log.getProjectId()) &&
                        entityList.contains(log.getEntityType()));
    }

    /**
     * Subscribe to transaction logs for services in progress.
     */
    @SubscriptionMapping
    public Publisher<TransactionLog> onTransactionByServiceInProgressIds(
            @Argument List<AuditEntity> entityList,
            @Argument List<Integer> serviceIds) {
        log.info("Client subscribed to transactions for services: {}", serviceIds);
        return transactionPublisher.getFilteredStream(log ->
                log.getServiceInProgressId() != null &&
                        serviceIds.contains(log.getServiceInProgressId()) &&
                        entityList.contains(log.getEntityType()));
    }

    /**
     * Resolve worker for transaction log.
     */
    @SchemaMapping(typeName = "TransactionLog", field = "worker")
    public Worker getWorker(TransactionLog log) {
        return log.getWorkerId() != null
                ? workerService.getWorkerById(log.getWorkerId())
                : null;
    }

    /**
     * Resolve project for transaction log.
     */
    @SchemaMapping(typeName = "TransactionLog", field = "project")
    public Project getProject(TransactionLog log) {
        return log.getProjectId() != null
                ? projectService.getProjectById(log.getProjectId())
                : null;
    }

    /**
     * Resolve task for transaction log.
     */
    @SchemaMapping(typeName = "TransactionLog", field = "task")
    public Task getTask(TransactionLog log) {
        return log.getTaskId() != null
                ? taskService.getTaskById(log.getTaskId())
                : null;
    }

    /**
     * Resolve material for transaction log.
     */
    @SchemaMapping(typeName = "TransactionLog", field = "material")
    public Material getMaterial(TransactionLog log) {
        return log.getMaterialId() != null
                ? materialService.getMaterialById(log.getMaterialId())
                : null;
    }

    /**
     * Resolve service in progress for transaction log.
     */
    @SchemaMapping(typeName = "TransactionLog", field = "serviceInProgress")
    public ServicesInProgress getServiceInProgress(TransactionLog log) {
        return log.getServiceInProgressId() != null
                ? servicesInProgressService.getServicesInProgressById(log.getServiceInProgressId())
                : null;
    }

    /**
     * Resolve material review for transaction log.
     */
    @SchemaMapping(typeName = "TransactionLog", field = "review")
    public MaterialReview getReview(TransactionLog log) {
        return log.getMaterialReviewId() != null

                ? materialReviewService.getMaterialReviewById(log.getMaterialReviewId())
                : null;
    }
}