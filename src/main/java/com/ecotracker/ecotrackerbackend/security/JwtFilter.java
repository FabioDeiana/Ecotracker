package com.ecotracker.ecotrackerbackend.security;

import com.ecotracker.ecotrackerbackend.entities.User;
import com.ecotracker.ecotrackerbackend.exceptions.UnauthorizedException;
import com.ecotracker.ecotrackerbackend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserService userService;

    // @Lazy su UserService per evitare dipendenza circolare:
    // JwtFilter → UserService → SecurityConfig → JwtFilter
    public JwtFilter(JwtUtils jwtUtils, @Lazy UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // leggo l'header Authorization dalla richiesta
        String authHeader = request.getHeader("Authorization");

        // se non c'è il token o non inizia con "Bearer " lascio passare la richiesta
        // sarà Spring Security a bloccarla se l'endpoint richiede autenticazione
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // estraggo il token togliendo "Bearer " (7 caratteri)
        String token = authHeader.substring(7);

        try {
            // estraggo l'id dal token
            String id = jwtUtils.extractIdFromToken(token);

            // carico l'utente dal DB tramite l'id
            User user = userService.getById(UUID.fromString(id));

            // dico a Spring Security chi è l'utente autenticato
            // null al posto della password perché non serve qui
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            throw new UnauthorizedException("Token non valido o scaduto");
        }

        filterChain.doFilter(request, response);
    }
}
