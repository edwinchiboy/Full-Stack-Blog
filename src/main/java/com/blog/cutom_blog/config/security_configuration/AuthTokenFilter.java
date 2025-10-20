package com.blog.cutom_blog.config.security_configuration;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip JWT filter for public endpoints ONLY
        // Public GET endpoints for posts (but not admin-only endpoints like /by-status)
        boolean isPublicPostsEndpoint = "GET".equals(method) && (
            path.equals("/api/posts") ||  // GET /api/posts
            path.matches("/api/posts/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}") ||  // GET /api/posts/{uuid}
            path.startsWith("/api/posts/slug/") ||  // GET /api/posts/slug/{slug}
            path.startsWith("/api/posts/search")  // GET /api/posts/search
        );

        return path.startsWith("/v1/registration") ||
               path.startsWith("/api/auth") ||
               isPublicPostsEndpoint ||
               (path.startsWith("/api/categories") && "GET".equals(method)) ||
               (path.startsWith("/api/tags") && "GET".equals(method)) ||
               path.startsWith("/api/subscribers") ||
               path.startsWith("/api/password-reset") ||
               path.startsWith("/css") ||
               path.startsWith("/js") ||
               path.startsWith("/assets") ||
               path.startsWith("/static") ||
               path.equals("/") ||
               path.equals("/login") ||
               path.equals("/register") ||
               path.equals("/about") ||
               path.equals("/post") ||
               path.equals("/privacy") ||
               path.equals("/terms");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            logger.debug("Processing request to: {} - JWT present: {}", request.getRequestURI(), jwt != null);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("JWT validated for user: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("=== DEBUG AuthTokenFilter ===");
                System.out.println("Username: " + username);
                System.out.println("Authorities from UserDetails: " + userDetails.getAuthorities());
                System.out.println("=============================");

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set successfully for user: {}", username);
            } else {
                logger.debug("No valid JWT found for request to: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}