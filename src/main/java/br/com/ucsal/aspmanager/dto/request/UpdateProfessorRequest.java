package br.com.ucsal.aspmanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para atualização de dados acadêmicos do professor")
public record UpdateProfessorRequest(
        @Schema(description = "Matrícula do professor", example = "202600123")
        @Size(min = 3, max = 100, message = "Matrícula deve ter entre 3 e 100 caracteres") String matricula,

        @Schema(description = "ID da escola vinculada ao professor", example = "2")
        Long idEscola
) {
}
