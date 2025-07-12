package com.unibox.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.unibox.service.JwtService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleBasedAccessFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final List<String> openEndpoints = List.of(
            "/api/admin/login",
            "/api/users/login",
            "/api/users/signup",
            "/api/department/login",
            "/api/public/departments",
            "/api/auth/user/login"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Normalize path (remove trailing slash if any)
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        // Allow OPTIONS requests (CORS preflight) without auth
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip filtering for non-API paths or open endpoints
        boolean isOpen = openEndpoints.stream().anyMatch(path::equals);
        if (!path.startsWith("/api") || isOpen) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔍 Log Authorization header before extracting role
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        String role = jwtService.extractRole(request);

        if (role == null) {
            System.out.println("Missing or invalid token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid token");
            return;
        }

        // Department-only check for specific endpoints
        if (path.startsWith("/api/complaints/department") && !"DEPARTMENT".equals(role)) {
            System.out.println("Access denied: DEPARTMENT only");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: DEPARTMENT only");
            return;
        }

        // Admin-only check
        if (path.startsWith("/api/departments") && !"ADMIN".equals(role)) {
            System.out.println("Access denied: ADMIN only");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: ADMIN only");
            return;
        }

        System.out.println("Access granted for role: " + role);

        filterChain.doFilter(request, response);
    }
}
