package com.zerobase.apigateway.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    private final TokenProvider tokenProvider;

    public AuthorizationHeaderFilter(TokenProvider tokenProvider) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requiredRole = config.getRole();
            boolean hasToken = exchange.getRequest().getHeaders().containsKey("Authorization");
            if (!hasToken) {
                return unauthorizedResponse(exchange, "Token이 존재하지 않습니다.");
            }

            String authorizationHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(config.getHeaderName())).get(0); // Authorization의 value(token) 가져옴 & [] 제외
            String errorMsg = "";
            if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(config.getGranted())) {
                String token = authorizationHeader.substring(config.getGranted().length()); // Bearer 제외
                try {
                    String userRole = tokenProvider.resolveTokenRole(token).replace("[", "").replace("]", ""); // [] 제외

                    if (tokenProvider.validateToken(token)) {
                        log.info("유효한 토큰 확인 완료");
                    } else {
                        log.error("토큰이 유효하지 않음");
                    }
                    System.out.println(userRole);
                    System.out.println(requiredRole);
                    if (requiredRole.contains(userRole)) {
                        log.info("권한 확인 완료");
                        return chain.filter(exchange); // 토큰과 권한이 모두 유효하므로 filter 계속
                    } else {
                        log.info("권한 없음");
                    }
                } catch (Exception e) {
                    log.error("Token validation error: {}", e.getMessage());
                    errorMsg = e.getMessage();
                }
            }
            return unauthorizedResponse(exchange, errorMsg); // 토큰이 유효하지 않으므로 인증 실패 응답

        };
    }

    // 인증 실패 Response, 실패 메세지 추가
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String errorMsg) {
        ObjectMapper om = new ObjectMapper();

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        try {
            DataBuffer buffer = response.bufferFactory().wrap(om.writeValueAsBytes(errorMsg));

            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Config할 inner class -> 설정파일에 있는 args
    @Getter
    @Setter
    @Builder
    public static class Config {
        private String headerName; // Authorization
        private String granted; // Bearer
        private String role; // PARTNER or CUSTOMER
    }
}