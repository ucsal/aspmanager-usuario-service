package br.com.ucsal.aspmanager.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayHeaderFilter implements Filter {

    private static final List<String> EXCLUDED_PREFIXES = List.of(
            "/swagger-ui", "/v3/api-docs", "/webjars", "/actuator", "/error",
            "/api/v1/usuarios/email"  // chamada interna do ms-auth via Feign (sem X-User-Id)
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getServletPath();

        if (EXCLUDED_PREFIXES.stream().anyMatch(path::startsWith)) {
            chain.doFilter(req, res);
            return;
        }

        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            HttpServletResponse response = (HttpServletResponse) res;
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"erro\": \"Acesso direto não permitido. Utilize o API Gateway na porta 8080.\"}"
            );
            return;
        }

        chain.doFilter(req, res);
    }
}
