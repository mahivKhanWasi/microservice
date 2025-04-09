package com.mahivkhanwasi.api_gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahivkhanwasi.api_gateway.dto.RedisUserSession;
import com.mahivkhanwasi.api_gateway.service.JwtService;
import com.mahivkhanwasi.api_gateway.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
public class AuthenticationPreFilter extends AbstractGatewayFilterFactory<AuthenticationPreFilter.Config> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private List<String> excludedUrls;

    @Autowired
    private RedisService redisService; // Redis connection

    public AuthenticationPreFilter() {
        super(Config.class);
    }

    public static class Config {
        private List<String> excludedPatterns;

        public List<String> getExcludedPatterns() {
            return excludedPatterns;
        }

        public void setExcludedPatterns(List<String> excludedPatterns) {
            this.excludedPatterns = excludedPatterns;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Check if the request should be excluded
            if (isExcluded(path)) {
                return chain.filter(exchange);
            }

            HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
            String token = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !token.startsWith("Bearer ")) {
                return handleAuthError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            token = token.substring(7);

            try {
                var key = jwtService.getSignInKey();
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();
                String fullname = (String) claims.get("fullName");
                List<String> roles = (List<String>) claims.get("authorities"); // Extract roles

                // Check if the user exists in Redis
                if (!redisService.isKeyInRedis(fullname)) {
                    return handleAuthError(exchange, "Unauthorized user", HttpStatus.UNAUTHORIZED);
                }

                // Retrieve stored token from Redis
                RedisUserSession storedSession = redisService.get(fullname, RedisUserSession.class);

                if (storedSession == null || storedSession.getToken() == null || !storedSession.getToken().equals(token)) {
                    return handleAuthError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                }

                // Mutate the request with additional headers
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("username", username)
                        .header("X-User-Roles", String.join(",", roles))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build();

                System.out.println("🚀 Forwarding request with headers:");
                System.out.println("username: " + username);
                System.out.println("X-User-Roles: " + String.join(",", roles));

                // Return the mutated request with the updated headers
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
                return handleAuthError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    // Method to check if the request path matches an excluded pattern
    private boolean isExcluded(String path) {
        Predicate<String> isExcluded = pattern -> path.matches(pattern.replace("**", ".*"));
        return excludedUrls.stream().anyMatch(isExcluded);
    }

    // Method to handle errors during authentication
    private Mono<Void> handleAuthError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        responseBody.put("message", message);
        responseBody.put("status", status.value());
        responseBody.put("errorCode", status.value());

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
