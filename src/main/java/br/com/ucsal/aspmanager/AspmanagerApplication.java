package br.com.ucsal.aspmanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(title = "ASPManager API - Usuários", version = "1.0",
	description = "Microserviço de Gestão de Usuários e Professores"),

  	servers = {
        @Server(url = "http://localhost:8082", description = "Ambiente Local (Desenvolvimento)"),
        @Server(url = "http://localhost:8080/usuario", description = "API Gateway (Produção)")
    })
public class AspmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspmanagerApplication.class, args);
	}

}
