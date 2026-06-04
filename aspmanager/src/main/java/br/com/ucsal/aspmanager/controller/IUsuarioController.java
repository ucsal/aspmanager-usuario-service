package br.com.ucsal.aspmanager.controller;

import br.com.ucsal.aspmanager.dto.request.AlterarSenhaRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateProfessorRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateUsuarioRequest;
import br.com.ucsal.aspmanager.dto.response.ErroApiResponse;
import br.com.ucsal.aspmanager.dto.response.UsuarioResponse;
import br.com.ucsal.aspmanager.model.enums.Perfil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

public interface IUsuarioController {
    @PatchMapping("/{id}")
    @Operation(operationId = "toggleUsuarioStatusById", summary = "Alternar status de usuário", description = "Altera o status entre ATIVO e INATIVO para o usuário informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    ResponseEntity<UsuarioResponse> alterarStatus(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id);

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(operationId = "getUsuarioById", summary = "Buscar usuário por ID", description = "ADMIN pode consultar qualquer usuário. PROFESSOR só pode consultar o próprio cadastro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este usuário", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    ResponseEntity<UsuarioResponse> buscar(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id);

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(operationId = "updateUsuarioById", summary = "Atualizar usuário", description = "ADMIN pode atualizar qualquer usuário. PROFESSOR só pode atualizar o próprio cadastro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este usuário", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de regra de negócio", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    ResponseEntity<UsuarioResponse> atualizar(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
            @RequestBody @Valid UpdateUsuarioRequest request);

    @PatchMapping("/{id}/alterar-senha")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(operationId = "changeUsuarioSenhaById", summary = "Alterar senha do usuário", description = "Permite alterar senha informando senha atual e nova senha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou senha atual incorreta", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para alterar senha deste usuário", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    ResponseEntity<Void> alterarSenha(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
            @RequestBody @Valid AlterarSenhaRequest request);

    @GetMapping("/professores")
    @Operation(operationId = "listProfessores", summary = "Listar professores", description = "Retorna lista paginada de professores ativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    ResponseEntity<Page<UsuarioResponse>> buscarTodosOsProfessores(@ParameterObject Pageable filtros);

    @PutMapping("/professores/{idProfessor}")
    @Operation(operationId = "updateProfessorById", summary = "Atualizar professor", description = "Atualiza dados acadêmicos do professor, como matrícula e escola vinculada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de regra de negócio", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    ResponseEntity<UsuarioResponse> atualizarProfessor(
            @Parameter(description = "ID do professor", example = "1") @PathVariable Long idProfessor,
            @RequestBody @Valid UpdateProfessorRequest request);

    @DeleteMapping("/professores/{idProfessor}")
    @Operation(operationId = "deleteProfessorById", summary = "Excluir professor", description = "Exclui o vínculo de professor por identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Professor excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    ResponseEntity<Void> deletarProfessor(
            @Parameter(description = "ID do professor", example = "1") @PathVariable Long idProfessor);

    URI location(UsuarioResponse usuario, UriComponentsBuilder uriBuilder);

    default void validarAcessoAoUsuario(Long id, UsuarioResponse usuarioAutenticado) {
        if (usuarioAutenticado == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        if (usuarioAutenticado.perfil() != Perfil.ADMIN && !Objects.equals(id, usuarioAutenticado.id())) {
            throw new AccessDeniedException("Sem permissão para acessar este usuário");
        }
    }

    default UsuarioResponse usuarioAutenticado() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UsuarioResponse usuarioResponse) {
            return usuarioResponse;
        }

        return null;
    }
}
