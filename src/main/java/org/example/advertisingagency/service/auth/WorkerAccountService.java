package org.example.advertisingagency.service.auth;

import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.model.WorkerAccount;
import org.example.advertisingagency.repository.WorkerAccountRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkerAccountService {

    private final WorkerAccountRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final WorkerRepository workerRepository;

    public WorkerAccountService(
            WorkerAccountRepository repo,
            PasswordEncoder passwordEncoder,
            WorkerRepository workerRepository
    ) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.workerRepository = workerRepository;
    }

    public WorkerAccount createAccount(int workerId, String username, String rawPassword) {
        if (repo.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("Username already taken");
        }

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        if (repo.findByWorker_Id(workerId).isPresent()) {
            throw new RuntimeException("This worker already has an account");
        }

        String hash = passwordEncoder.encode(rawPassword);
        WorkerAccount account = new WorkerAccount();
        account.setWorker(worker);
        account.setUsername(username);
        account.setPasswordHash(hash);

        return repo.save(account);
    }

    public Optional<WorkerAccount> findByUsername(String username) {
        return repo.findByUsernameIgnoreCase(username);
    }

    public Optional<WorkerAccount> findAccountById(Integer id) {
        return repo.findById(id);
    }

    public Optional<WorkerAccount> findAccountByWorkerId(Integer id) {
        return repo.findByWorker_Id(id);
    }

    public boolean verifyPassword(WorkerAccount account, String rawPassword) {
        return passwordEncoder.matches(rawPassword, account.getPasswordHash());
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public WorkerAccount updateUsername(Integer accountId, String newUsername) {
        WorkerAccount account = repo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (repo.existsByUsernameIgnoreCase(newUsername)) {
            throw new RuntimeException("Username already taken");
        }

        account.setUsername(newUsername);
        return repo.save(account);
    }

    public WorkerAccount updatePassword(Integer accountId, String newPassword) {
        WorkerAccount account = repo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        return repo.save(account);
    }

    public boolean deleteAccount(Integer accountId) {
        if (!repo.existsById(accountId)) return false;
        repo.deleteById(accountId);
        return true;
    }

    public boolean accountExistsForWorker(Integer workerId) {
        return repo.findByWorker_Id(workerId).isPresent();
    }

    public List<WorkerAccount> findAll() {
        List<WorkerAccount> list = repo.findAll();
        return repo.findAll();
    }
}


