package br.com.ucsal.aspmanager.model;

import br.com.ucsal.aspmanager.model.enums.Perfil;
import br.com.ucsal.aspmanager.model.enums.StatusRegistro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;
    @Column(name = "status_registro", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusRegistro statusRegistro = StatusRegistro.ATIVO;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "usuario", fetch = FetchType.LAZY)
    @Builder.Default
    private List<TelefoneUsuario> telefones = new ArrayList<>();
}
