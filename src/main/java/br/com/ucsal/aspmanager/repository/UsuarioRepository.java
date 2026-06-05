package br.com.ucsal.aspmanager.repository;

import br.com.ucsal.aspmanager.model.Usuario;
import br.com.ucsal.aspmanager.model.enums.StatusRegistro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findUsuarioByEmail(String email);

    Page<Usuario> findByStatusRegistro(StatusRegistro statusRegistro, Pageable pageable);

}