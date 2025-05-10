package org.example.advertisingagency.controller;

import lombok.RequiredArgsConstructor;
import org.example.advertisingagency.dto.auth.WorkerAccountDTO;
import org.example.advertisingagency.dto.auth.WorkerAccountInput;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.model.WorkerAccount;
import org.example.advertisingagency.service.auth.WorkerAccountService;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

@Component
@RequiredArgsConstructor
@Controller
public class WorkerAccountController {

    private final WorkerAccountService accountService;

    @QueryMapping("workerAccountByWorkerId")
    public WorkerAccountDTO workerAccount(@Argument Integer workerId) {
        return accountService.findAccountByWorkerId(workerId)
                .map(this::toDTO)
                .orElse(null);
    }

    @QueryMapping
    public WorkerAccount workerAccountById(@Argument int accountId) {
        return accountService.findAccountById(accountId)
                .orElse(null);
    }

    @QueryMapping
    public List<WorkerAccount> allWorkerAccounts() {
        return accountService.findAll();
    }

    @MutationMapping
    public WorkerAccountDTO createWorkerAccount(@Argument WorkerAccountInput input) {
        return toDTO(accountService.createAccount(input.workerId(), input.username(), input.password()));
    }

    @MutationMapping
    public WorkerAccountDTO updateWorkerAccountUsername(@Argument Integer accountId, @Argument String newUsername) {
        return toDTO(accountService.updateUsername(accountId, newUsername));
    }

    @MutationMapping
    public Boolean deleteWorkerAccount(@Argument Integer accountId) {
        return accountService.deleteAccount(accountId);
    }

    private WorkerAccountDTO toDTO(WorkerAccount account) {
        return new WorkerAccountDTO(
                account.getId(),
                account.getWorker().getId(),
                account.getUsername(),
                account.getPasswordHash()
        );
    }

    @SchemaMapping(typeName = "WorkerAccount", field = "worker")
    public Worker getWorker(WorkerAccount account) {
        return account.getWorker();
    }
}

