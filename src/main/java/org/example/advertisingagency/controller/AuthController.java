package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.auth.LoginRequest;
import org.example.advertisingagency.dto.auth.LoginResponse;
import org.example.advertisingagency.dto.auth.RegisterRequest;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.model.WorkerAccount;
import org.example.advertisingagency.service.auth.JwtTokenService;
import org.example.advertisingagency.service.auth.WorkerAccountService;
import org.example.advertisingagency.service.user.WorkerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final WorkerAccountService workerAccountService;
    private final JwtTokenService jwtTokenService;
    private final WorkerService workerService;

    public AuthController(WorkerAccountService workerAccountService, JwtTokenService jwtTokenService, WorkerService workerService) {
        this.workerAccountService = workerAccountService;
        this.jwtTokenService = jwtTokenService;
        this.workerService = workerService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<WorkerAccount> accountOpt = workerAccountService.findByUsername(request.getUsername());

        if (accountOpt.isEmpty() || !workerAccountService.verifyPassword(accountOpt.get(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtTokenService.generateToken(accountOpt.get());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        workerAccountService.createAccount(request.getWorkerId(), request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("Account created");
    }
}
