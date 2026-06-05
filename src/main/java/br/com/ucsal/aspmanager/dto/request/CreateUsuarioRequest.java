package br.com.ucsal.aspmanager.dto.request;


import br.com.ucsal.aspmanager.model.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Payload para criação de usuário")
public record CreateUsuarioRequest(
        @Schema(description = "Nome completo do usuário", example = "Ana Paula Souza")
        @NotBlank(message = "Nome completo é obrigatório") @Size(min = 3, max = 254, message = "Nome completo deve ter entre 3 e 254 caracteres") String nomeCompleto,

        @Schema(description = "Email do usuário", example = "ana.souza@ucsal.br")
        @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email,

        @Schema(description = "Senha do usuário", example = "Senha@123")
        @NotBlank(message = "Senha é obrigatória") @Size(min = 6, max = 30, message = "Senha deve ter entre 6 e 30 caracteres") String senha,

        @Schema(description = "Perfil de acesso", example = "PROFESSOR")
        @NotNull(message = "Perfil é obrigatório") Perfil perfil,

        @Schema(description = "ID da escola vinculada (obrigatório para perfil PROFESSOR)", example = "2")
        Long idEscola,

        @Schema(description = "Matrícula do professor (obrigatório para perfil PROFESSOR)", example = "202600123")
        String matricula,

        @Schema(description = "Telefones de contato", example = "[\"71999998888\", \"7132017000\"]")
        List<String> telefones
) {
}
