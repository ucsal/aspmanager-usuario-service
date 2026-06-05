package br.com.ucsal.aspmanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Payload para atualização de usuário")
public record UpdateUsuarioRequest(
        @Schema(description = "Nome completo", example = "Ana Paula Souza")
        @Size(min = 3, max = 254, message = "Nome completo deve ter entre 3 e 254 caracteres") String nomeCompleto,

        @Schema(description = "Email", example = "ana.souza@ucsal.br")
        @Email(message = "Email inválido") String email,

        @Schema(description = "Telefones de contato. [] remove todos; null não altera.", example = "[\"71999998888\"]")
        List<String> telefones // telefones = [] → apaga todos e telefones = null → não mexe nos telefones
) {
}
