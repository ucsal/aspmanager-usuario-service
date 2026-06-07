package br.com.ucsal.aspmanager.controller;

import br.com.ucsal.aspmanager.dto.request.AlterarSenhaRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateProfessorRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateUsuarioRequest;
import br.com.ucsal.aspmanager.dto.response.ErroApiResponse;
import br.com.ucsal.aspmanager.dto.response.UsuarioAuthResponse;
import br.com.ucsal.aspmanager.dto.response.UsuarioResponse;
import br.com.ucsal.aspmanager.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/v1/usuario")
@Tag(name = "Usuários", description = "Gestão de usuários do sistema e cadastro de professores")
public class UsuarioController implements IUsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint interno usado pelo ms-auth via Feign para autenticação.
     * Retorna os dados de autenticação incluindo senha criptografada.
     * Excluído do GatewayHeaderFilter (não requer X-User-Id).
     */
    @GetMapping("/email/{email}")
    @Operation(operationId = "getUsuarioByEmail", summary = "[INTERNO] Buscar usuário por email",
            description = "Endpoint interno consumido pelo ms-auth via Feign para validar credenciais de login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<UsuarioAuthResponse> buscarPorEmail(
            @Parameter(description = "Email do usuário", example = "admin@aspmanager.com")
            @PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @PatchMapping("/{id}")
    @Operation(operationId = "toggleUsuarioStatusById", summary = "Alternar status de usuário", description = "Altera o status entre ATIVO e INATIVO para o usuário informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    @Override
    public ResponseEntity<UsuarioResponse> alterarStatus(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.alterarStatusRegistro(id));
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getUsuarioById", summary = "Buscar usuário por ID", description = "ADMIN pode consultar qualquer usuário. PROFESSOR só pode consultar o próprio cadastro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este usuário", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    @Override
    public ResponseEntity<UsuarioResponse> buscar(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        validarAcessoAoUsuario(id, xUserId, xUserRole);
        return ResponseEntity.ok(usuarioService.buscar(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateUsuarioById", summary = "Atualizar usuário", description = "ADMIN pode atualizar qualquer usuário. PROFESSOR só pode atualizar o próprio cadastro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este usuário", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de regra de negócio", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    @Override
    public ResponseEntity<UsuarioResponse> atualizar(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
            @RequestBody @Valid UpdateUsuarioRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        validarAcessoAoUsuario(id, xUserId, xUserRole);
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @PatchMapping("/{id}/alterar-senha")
    @Operation(operationId = "changeUsuarioSenhaById", summary = "Alterar senha do usuário", description = "Permite alterar senha informando senha atual e nova senha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou senha atual incorreta", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para alterar senha deste usuário", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    @Override
    public ResponseEntity<Void> alterarSenha(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
            @RequestBody @Valid AlterarSenhaRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        validarAcessoAoUsuario(id, xUserId, xUserRole);
        usuarioService.alterarSenha(request, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/professores")
    @Operation(operationId = "listProfessores", summary = "Listar professores", description = "Retorna lista paginada de professores ativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    @Override
    public ResponseEntity<Page<UsuarioResponse>> buscarTodosOsProfessores(@ParameterObject Pageable filtros) {
        return ResponseEntity.ok(usuarioService.buscarTodosProfessores(filtros));
    }

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
    @Override
    public ResponseEntity<UsuarioResponse> atualizarProfessor(
            @Parameter(description = "ID do professor", example = "1") @PathVariable Long idProfessor,
            @RequestBody @Valid UpdateProfessorRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarProfessor(idProfessor, request));
    }

    @DeleteMapping("/professores/{idProfessor}")
    @Operation(operationId = "deleteProfessorById", summary = "Excluir professor", description = "Exclui o vínculo de professor por identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Professor excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    @Override
    public ResponseEntity<Void> deletarProfessor(
            @Parameter(description = "ID do professor", example = "1") @PathVariable Long idProfessor) {
        usuarioService.deletarProfessor(idProfessor);
        return ResponseEntity.noContent().build();
    }

    @Override
    public URI location(UsuarioResponse usuario, UriComponentsBuilder uriBuilder) {
        return uriBuilder.path("/api/v1/usuarios/{id}").buildAndExpand(usuario.id()).toUri();
    }

}
