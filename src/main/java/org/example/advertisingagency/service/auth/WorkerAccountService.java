package org.example.advertisingagency.service.auth;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.model.WorkerAccount;
import org.example.advertisingagency.repository.WorkerAccountRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class WorkerAccountService {

    private final WorkerAccountRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final WorkerRepository workerRepository;
    private final WorkerAccountRepository workerAccountRepository;

    public WorkerAccountService(WorkerAccountRepository repo, PasswordEncoder passwordEncoder, WorkerRepository workerRepository, WorkerAccountRepository workerAccountRepository) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.workerRepository = workerRepository;
        this.workerAccountRepository = workerAccountRepository;
    }

    public void createAccount(int workerId, String username, String rawPassword) {
        String hash = passwordEncoder.encode(rawPassword);

        WorkerAccount account = new WorkerAccount(workerRepository.findById(workerId).orElseThrow(()->{
            return new RuntimeException("Worker not found");
        }), username, hash);
        repo.save(account);
    }

    public Optional<WorkerAccount> findByUsername(String username) {
        return workerAccountRepository.findByUsernameIgnoreCase(username);
    }

    public boolean verifyPassword(WorkerAccount account, String rawPassword) {
        return passwordEncoder.matches(rawPassword, account.getPasswordHash());
    }

    public Optional<WorkerAccount> findWorkerById(Integer id) {
        return workerAccountRepository.findById(id);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}

