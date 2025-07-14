package com.epam.gymapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epam.gymapp.util.TransactionContext;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String transactionId = request.getHeader("X-Transaction-ID"); 
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString(); 
        }
        TransactionContext.setTransactionId(transactionId); 
        response.setHeader("X-Transaction-ID", transactionId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            TransactionContext.clearTransactionId(); 
        }
    }
}