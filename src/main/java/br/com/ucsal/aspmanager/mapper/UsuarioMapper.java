package br.com.ucsal.aspmanager.mapper;

import br.com.ucsal.aspmanager.dto.request.CreateUsuarioRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateProfessorRequest;
import br.com.ucsal.aspmanager.dto.request.UpdateUsuarioRequest;
import br.com.ucsal.aspmanager.dto.response.UsuarioResponse;
import br.com.ucsal.aspmanager.model.Professor;
import br.com.ucsal.aspmanager.model.Telefone;
import br.com.ucsal.aspmanager.model.TelefoneUsuario;
import br.com.ucsal.aspmanager.model.Usuario;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusRegistro", ignore = true)
    @Mapping(target = "telefones", source = "telefones", qualifiedByName = "toTelefoneEntities")
    Usuario toEntity(CreateUsuarioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telefones", ignore = true)
    @Mapping(target = "statusRegistro", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    void updateEntity(UpdateUsuarioRequest request, @MappingTarget Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    void updateProfessor(UpdateProfessorRequest request, @MappingTarget Professor professor);

    default UsuarioResponse toResponse(Usuario usuario, Professor professor) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getStatusRegistro(),
                professor == null ? null : professor.getMatricula(),
                professor == null ? null : professor.getId(),
                professor == null ? null : professor.getIdEscola(),
                toTelefoneStrings(usuario.getTelefones()));
    }

    default UsuarioResponse toResponse(Usuario usuario) {
        return toResponse(usuario, null);
    }

    default Professor toProfessor(Usuario usuario, Long idEscola, String matricula) {
        return Professor.builder()
                .usuario(usuario)
                .idEscola(idEscola)
                .matricula(matricula)
                .build();
    }


    @Named("toTelefoneEntities")
    default List<TelefoneUsuario> toTelefoneEntities(List<String> telefones) {
        if (telefones == null || telefones.isEmpty()) {
            return new ArrayList<>();
        }

        List<TelefoneUsuario> entidades = new ArrayList<>();
        for (String numero : telefones) {
            if (numero == null || numero.isBlank()) {
                continue;
            }

            TelefoneUsuario telefone = new TelefoneUsuario();
            String limpo = numero.replaceAll("[()\\s-]", "");
            if (!limpo.matches("\\d{10,11}")) {
                throw new IllegalArgumentException("Número de telefone inválido: " + numero + ". Deve conter 10 ou 11 dígitos.");
            }
            telefone.setNumero(limpo);
            entidades.add(telefone);
        }

        return entidades;
    }

    @Named("toTelefoneStrings")
    default List<String> toTelefoneStrings(List<? extends Telefone> telefones) {
        if (telefones == null || telefones.isEmpty()) {
            return Collections.emptyList();
        }

        return telefones.stream().map(Telefone::getNumero).toList();
    }

    @AfterMapping
    default void vincularTelefonesAoUsuario(@MappingTarget Usuario usuario) {
        if (usuario.getTelefones() == null) {
            return;
        }

        for (TelefoneUsuario telefone : usuario.getTelefones()) {
            telefone.setUsuario(usuario);
        }
    }
}
