package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;

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

    @Column(name = "can_titulo")
    private String titulo;

    @Column(name = "can_duracion")
    private Integer duracion;

    @Column(name = "can_colaboradores")
    private String colaboradores;

    @ManyToOne
    @JoinColumn(name = "alb_id")
    private Album album;

    @Column(name = "can_es_sencillo")
    private boolean esSencillo;

    @Column(name = "can_estado")
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "usu_usuario")
    private Usuario usuario;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public String getColaboradores() { return colaboradores; }
    public void setColaboradores(String colaboradores) { this.colaboradores = colaboradores; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public boolean isEsSencillo() { return esSencillo; }
    public void setEsSencillo(boolean esSencillo) { this.esSencillo = esSencillo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}