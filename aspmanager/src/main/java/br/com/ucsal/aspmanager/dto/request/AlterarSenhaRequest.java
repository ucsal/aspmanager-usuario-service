package br.com.ucsal.aspmanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para alteração de senha do usuário")
public record AlterarSenhaRequest(
        @Schema(description = "Senha atual do usuário", example = "SenhaAtual@123")
        @NotBlank(message = "Senha antiga não pode estar vazia") String senhaAntiga,

        @Schema(description = "Nova senha do usuário", example = "NovaSenha@123")
        @Size(min = 6, max = 30, message = "Senha deve ter entre 6 e 30 caracteres") String senhaNova
) {
}