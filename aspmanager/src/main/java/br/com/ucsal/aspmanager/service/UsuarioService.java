package br.com.ucsal.aspmanager.service;

import br.com.ucsal.aspmanager.dto.request.AlterarSenhaRequest;
import br.com.ucsal.aspmanager.dto.request.CreateUsuarioRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateProfessorRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateUsuarioRequest;
import br.com.ucsal.aspmanager.dto.response.UsuarioResponse;
import br.com.ucsal.aspmanager.mapper.UsuarioMapper;
import br.com.ucsal.aspmanager.model.Professor;
import br.com.ucsal.aspmanager.model.Usuario;
import br.com.ucsal.aspmanager.model.enums.Perfil;
import br.com.ucsal.aspmanager.model.enums.StatusRegistro;
import br.com.ucsal.aspmanager.repository.ProfessorRepository;
import br.com.ucsal.aspmanager.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioService{

    private final UsuarioRepository usuarios;

    private final ProfessorRepository professores;
    private final PasswordEncoder codificadorDeSenha;
    private final EscolaRepository escolas;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder codificadorDeSenha,  ProfessorRepository professores, EscolaRepository escolas, UsuarioMapper usuarioMapper) {
        this.usuarios = usuarios;
        this.codificadorDeSenha = codificadorDeSenha;
        this.professores = professores;
        this.escolas = escolas;
        this.usuarioMapper = usuarioMapper;
    }


    @Transactional
    public UsuarioResponse criar(CreateUsuarioRequest createUsuarioRequest) {
        Usuario usuario = usuarioMapper.toEntity(createUsuarioRequest);
        usuario.setSenha(codificadorDeSenha.encode(createUsuarioRequest.senha()));

        boolean isProfessor = createUsuarioRequest.perfil().equals(Perfil.PROFESSOR);

        if (isProfessor && (createUsuarioRequest.matricula() == null || createUsuarioRequest.idEscola() == null)) {
            throw new IllegalArgumentException("Matrícula e Escola não devem ser nulas para professores");
        }

        if (isProfessor) {
            usuarios.save(usuario);
            Escola escola = escolas.findById(createUsuarioRequest.idEscola()).orElseThrow(() -> new EntityNotFoundException("Escola não encontrada"));
            Professor professor = professores.save(usuarioMapper.toProfessor(usuario, escola, createUsuarioRequest.matricula()));
            return usuarioMapper.toResponse(usuario, professor);
        }

        usuarios.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public Page<UsuarioResponse> buscarTodos(Pageable filtros) {
        return usuarios.findByStatusRegistro(StatusRegistro.ATIVO, filtros)
                .map(usuarioMapper::toResponse);
    }

    public UsuarioResponse buscar(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        Professor professor = professores.findByUsuario_Id(usuario.getId()).orElse(null);
        return usuarioMapper.toResponse(usuario, professor);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UpdateUsuarioRequest updateUsuarioRequest) {
        Usuario update = buscarUsuarioPorId(id);
        Usuario usuarioExistente = usuarios.findUsuarioByEmail(updateUsuarioRequest.email()).orElse(null);

        if (usuarioExistente != null && !update.getEmail().equals(usuarioExistente.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com esse email!");
        }

        usuarioMapper.updateEntity(updateUsuarioRequest, update);

        atualizarTelefones(update, updateUsuarioRequest.telefones());

        return usuarioMapper.toResponse(usuarios.save(update));
    }

    @Transactional
    public void deletar(Long id) {
        Usuario delete = buscarUsuarioPorId(id);

        professores.deleteByUsuario(delete);
        usuarios.delete(delete);
    }

    @Transactional
    public UsuarioResponse alterarStatusRegistro(Long id) {
        Usuario update = buscarUsuarioPorId(id);

        if (update.isEnabled()) {
            update.setStatusRegistro(StatusRegistro.INATIVO);
        } else {
            update.setStatusRegistro(StatusRegistro.ATIVO);
        }

        return usuarioMapper.toResponse(usuarios.save(update));
    }

    @Transactional
    public void alterarSenha(AlterarSenhaRequest request, Long usuarioId) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);

        if (!codificadorDeSenha.matches(request.senhaAntiga(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta!");
        }

        usuario.setSenha(codificadorDeSenha.encode(request.senhaNova()));

        usuarios.save(usuario);
    }

    public Page<UsuarioResponse> buscarTodosProfessores(Pageable filtros) {
        return professores.findByUsuario_StatusRegistro(StatusRegistro.ATIVO, filtros)
                .map(professor -> usuarioMapper.toResponse(professor.getUsuario(), professor));
    }

    @Transactional
    public UsuarioResponse atualizarProfessor(Long id, UpdateProfessorRequest updateProfessorRequest) {
        Professor update = professores.findById(id).orElseThrow(() -> new EntityNotFoundException("Professor não encontrado!"));
        Professor professorExistente = professores.findByMatricula(updateProfessorRequest.matricula()).orElse(null);

        if (professorExistente != null && !update.getMatricula().equals(professorExistente.getMatricula())) {
            throw new IllegalArgumentException("Já existe um professor com essa matrícula!");
        }

        usuarioMapper.updateProfessor(updateProfessorRequest, update);

        return usuarioMapper.toResponse(buscarUsuarioPorId(update.getUsuario().getId()), update);
    }

    @Transactional
    public void deletarProfessor(Long id) {
        if (professores.existsById(id)) {
            professores.deleteById(id);
        }
    }


    private Usuario buscarUsuarioPorId(Long id) {
        return usuarios.findById(id).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    @Transactional
    protected void atualizarTelefones(Usuario usuario, List<String> telefones) {
        if (telefones == null) {
            return;
        }

        if (usuario.getTelefones() == null) {
            usuario.setTelefones(new ArrayList<>());
        }

        usuario.getTelefones().clear();

        usuarioMapper.toTelefoneEntities(telefones).forEach(telefone -> {
            telefone.setUsuario(usuario);
            usuario.getTelefones().add(telefone);
        });
    }


}