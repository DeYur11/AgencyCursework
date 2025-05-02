package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.service.servicesinprogress.CreateServicesInProgressInput;
import org.example.advertisingagency.dto.service.servicesinprogress.UpdateServicesInProgressInput;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.service.service.ProjectServiceService;
import org.example.advertisingagency.service.service.ServicesInProgressService;
import org.example.advertisingagency.service.task.TaskService;
import org.example.advertisingagency.util.BatchLoaderUtils;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ServicesInProgressController {

    private final ServicesInProgressService servicesInProgressService;
    private final ProjectServiceService projectServiceService;
    private final TaskService taskService;

    public ServicesInProgressController(ServicesInProgressService servicesInProgressService,
                                        ProjectServiceService projectServiceService,
                                        TaskService taskService) {
        this.servicesInProgressService = servicesInProgressService;
        this.projectServiceService = projectServiceService;
        this.taskService = taskService;
    }

    // --- Query для отримання одного запису
    @QueryMapping
    public ServicesInProgress serviceInProgress(@Argument Integer id) {
        return servicesInProgressService.getServicesInProgressById(id);
    }

    // --- Query для отримання списку
    @QueryMapping
    public List<ServicesInProgress> servicesInProgress() {
        return servicesInProgressService.getAllServicesInProgress();
    }

    // --- Mutation для створення
    @MutationMapping
    @Transactional
    public ServicesInProgress createServiceInProgress(@Argument CreateServicesInProgressInput input) {
        return servicesInProgressService.createServicesInProgress(input);
    }

    // --- Mutation для оновлення
    @MutationMapping
    @Transactional
    public ServicesInProgress updateServiceInProgress(@Argument Integer id, @Argument UpdateServicesInProgressInput input) {
        return servicesInProgressService.updateServicesInProgress(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteServiceInProgress(@Argument Integer id) {
        return servicesInProgressService.deleteServicesInProgress(id);
    }

    @SchemaMapping(typeName = "ServiceInProgress", field = "status")
    public ServiceInProgressStatus getStatus(ServicesInProgress serviceInProgress) {
        return serviceInProgress.getStatus();
    }

    @SchemaMapping(typeName = "ServiceInProgress", field = "projectService")
    public ProjectService getProjectService(ServicesInProgress serviceInProgress) {
        return serviceInProgress.getProjectService();
    }

    @BatchMapping(typeName = "ServiceInProgress", field = "tasks")
    public Mono<Map<ServicesInProgress, List<Task>>> getTasks(List<ServicesInProgress> servicesInProgressList) {
        List<Integer> serviceInProgressIds = servicesInProgressList.stream()
                .map(ServicesInProgress::getId)
                .toList();

        List<Task> tasks = BatchLoaderUtils.loadInBatches(
                serviceInProgressIds,
                taskService::getTasksByServiceInProgressIds
        );

        Map<Integer, List<Task>> tasksByServiceInProgressId = tasks.stream()
                .collect(Collectors.groupingBy(task -> task.getServiceInProgress().getId()));

        Map<ServicesInProgress, List<Task>> result = servicesInProgressList.stream()
                .collect(Collectors.toMap(
                        sip -> sip,
                        sip -> tasksByServiceInProgressId.getOrDefault(sip.getId(), List.of())
                ));

        return Mono.just(result);
    }

    @QueryMapping
    public List<ServicesInProgress> servicesInProgressByProjectService(@Argument Integer projectServiceId) {
        return servicesInProgressService.getServicesInProgressByProjectServiceId(projectServiceId);
    }
}
