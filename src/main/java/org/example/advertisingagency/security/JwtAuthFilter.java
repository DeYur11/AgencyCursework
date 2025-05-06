package org.example.advertisingagency.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.advertisingagency.model.WorkerAccount;
import org.example.advertisingagency.service.auth.JwtTokenService;
import org.example.advertisingagency.service.auth.WorkerAccountService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final WorkerAccountService workerAccountService;

    public JwtAuthFilter(JwtTokenService jwtTokenService, WorkerAccountService workerAccountService) {
        this.jwtTokenService = jwtTokenService;
        this.workerAccountService = workerAccountService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7); // remove "Bearer "
        if (!jwtTokenService.validateToken(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        Integer workerId = jwtTokenService.extractWorkerId(jwtToken);
        Optional<WorkerAccount> accountOpt = workerAccountService.findWorkerById(workerId);

        if (accountOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
            WorkerAccount account = accountOpt.get();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    account, null, List.of()  // You can add roles here if needed
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
