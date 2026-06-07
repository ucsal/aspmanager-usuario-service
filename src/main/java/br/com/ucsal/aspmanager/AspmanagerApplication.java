package br.com.ucsal.aspmanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(title = "ASPManager API - Usuários", version = "1.0",
	description = "Microserviço de Gestão de Usuários e Professores"),
	security = @SecurityRequirement(name = "bearerAuth"),
  	servers = {
        @Server(url = "http://localhost:8082", description = "Ambiente Local (Desenvolvimento)"),
        @Server(url = "http://localhost:8080", description = "API Gateway (Produção)")
    })
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Token access",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class AspmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspmanagerApplication.class, args);
	}

}
