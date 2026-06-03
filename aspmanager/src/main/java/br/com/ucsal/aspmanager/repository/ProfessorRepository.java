package br.com.ucsal.aspmanager.repository;


import br.com.ucsal.aspmanager.model.Professor;
import br.com.ucsal.aspmanager.model.Usuario;
import br.com.ucsal.aspmanager.model.enums.StatusRegistro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUsuario_Id(Long usuarioId);

    Page<Professor> findByUsuario_StatusRegistro(StatusRegistro statusRegistro, Pageable pageable);

    boolean existsByEscola_Id(Long escolaId);

    void deleteByUsuario(Usuario usuario);

    Optional<Professor> findByMatricula(String matricula);
}