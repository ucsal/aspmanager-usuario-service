package br.com.ucsal.aspmanager.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Perfil de acesso do usuário")
public enum Perfil {
    ADMIN,
    PROFESSOR
}
