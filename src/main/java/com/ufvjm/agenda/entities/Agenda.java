package com.ufvjm.agenda.entities;

import com.ufvjm.agenda.entities.enums.StatusAgenda;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_agenda")
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;
    private String descricao;
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    private StatusAgenda statusAgenda;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Agenda() {
    }

    public Agenda(UUID id, String nome, String descricao, LocalDateTime data, StatusAgenda statusAgenda, Usuario usuario) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.statusAgenda = statusAgenda;
        this.usuario = usuario;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public StatusAgenda getStatusAgenda() {
        return statusAgenda;
    }

    public void setStatusAgenda(StatusAgenda statusAgenda) {
        this.statusAgenda = statusAgenda;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Agenda agenda = (Agenda) o;
        return Objects.equals(id, agenda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
