package org.example.advertisingagency.controller;

import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.model.auth.AuditAction;
import org.example.advertisingagency.model.auth.AuditEntity;
import org.example.advertisingagency.model.auth.AuditLog;
import org.example.advertisingagency.repository.ProjectRepository;
import org.example.advertisingagency.repository.WorkerRepository;

import org.example.advertisingagency.service.logs.AuditLogPublisher;
import org.example.advertisingagency.service.logs.AuditLogService;
import org.example.advertisingagency.service.material.MaterialService;
import org.example.advertisingagency.service.project.ProjectService;
import org.example.advertisingagency.service.task.TaskService;
import org.example.advertisingagency.service.user.WorkerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

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

    public AuditController(AuditLogService auditLogService,
                           AuditLogPublisher auditLogPublisher,
                           WorkerRepository workerRepository, ProjectRepository projectRepository, ProjectService projectService, WorkerService workerService, TaskService taskService, MaterialService materialService) {
        this.auditLogService = auditLogService;
        this.auditLogPublisher = auditLogPublisher;
        this.workerRepository = workerRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.workerService = workerService;
        this.taskService = taskService;
        this.materialService = materialService;
    }

    // 📄 Отримати логі по проєктах
    @QueryMapping
    public List<AuditLog> auditLogsByProjectIds(@Argument List<Integer> projectIds) {
        return auditLogService.getByProjectIds(projectIds);
    }

    // 📄 Отримати логі по матеріалах
    @QueryMapping
    public List<AuditLog> auditLogsByMaterialIds(@Argument List<Integer> materialIds) {
        return auditLogService.getByMaterialIds(materialIds);
    }

    // 📄 Загальні логі по завданнях
    @QueryMapping
    public List<AuditLog> auditLogsForTasks(@Argument Optional<Integer> limit) {
        return auditLogService.getTaskRelatedLogs()
                .stream()
                .limit(limit.orElse(20))
                .toList();
    }

    // 🔔 Підписка на логування матеріалів
    @SubscriptionMapping
    public Flux<AuditLog> onReviewAuditLogByMaterialIds(@Argument List<Integer> materialIds) {
        return auditLogPublisher.getFilteredStream(log ->
                log.getEntity() == AuditEntity.MATERIAL_REVIEW &&
                        log.getMaterialId() != null &&
                        materialIds.contains(log.getMaterialId())
        );
    }

    // 👤 Прив'язка об'єкта Worker по workerId
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
}
