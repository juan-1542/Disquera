package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "canciones")
public class Cancion {

    public enum Estado {
        PENDIENTE,
        APROBADO,
        RECHAZADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "can_id")
    private Long id;

    @Column(name = "can_titulo", nullable = false)
    private String titulo;

    @Column(name = "can_duracion")
    private Integer duracion;

    @Column(name = "can_es_sencillo", nullable = false)
    private boolean esSencillo;

    @Column(name = "can_estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alb_id")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_usuario", nullable = false)
    private Usuario usuario;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cancion_colaboradores",
            joinColumns = @JoinColumn(name = "can_id"),
            inverseJoinColumns = @JoinColumn(name = "usu_usuario")
    )
    private List<Usuario> colaboradores;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public boolean isEsSencillo() { return esSencillo; }
    public void setEsSencillo(boolean esSencillo) { this.esSencillo = esSencillo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Usuario> getColaboradores() { return colaboradores; }
    public void setColaboradores(List<Usuario> colaboradores) { this.colaboradores = colaboradores; }
}