package br.com.ucsal.aspmanager.dto.response;

import br.com.ucsal.aspmanager.model.enums.Perfil;
import br.com.ucsal.aspmanager.model.enums.StatusRegistro;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO interno usado pelo ms-auth via Feign para autenticação.
 * Expõe a senha criptografada APENAS neste endpoint interno,
 * que é protegido pelo GatewayHeaderFilter (excluído do bloqueio).
 */
@Schema(description = "Dados de autenticação do usuário (uso interno ms-auth)")
public record UsuarioAuthResponse(
        @Schema(description = "ID do usuário", example = "1")
        Long id,

        @Schema(description = "Email do usuário", example = "admin@aspmanager.com")
        String email,

        @Schema(description = "Senha criptografada (BCrypt)", example = "$2a$10$...")
        String senhaCriptografada,

        @Schema(description = "Perfil de acesso", example = "ADMIN")
        Perfil perfil,

        @Schema(description = "Status do registro", example = "ATIVO")
        StatusRegistro statusRegistro) {
}
