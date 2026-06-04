package br.com.ucsal.aspmanager.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "Modelo padrão de erro retornado pela API")
public record ErroApiResponse (

        @Schema(description = "Data e hora da ocorrência do erro", example = "2026-04-23T10:15:30")
        LocalDateTime timestamp,

        @Schema(description = "Código HTTP da resposta", example = "400")
        Integer codigo,

        @Schema(description = "Nome do status HTTP", example = "BAD_REQUEST")
        String status,

        @ArraySchema(schema = @Schema(description = "Lista de mensagens de erro", example = "email: Email inválido"))
        List<String> erros

) {
}



