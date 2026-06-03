package br.com.ucsal.aspmanager.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status de atividade de um registro")
public enum StatusRegistro {
    ATIVO,
    INATIVO
}
