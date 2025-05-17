package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;

@Entity
public class Cancion {

    public enum Estado {
        PENDIENTE,
        APROBADO,
        RECHAZADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private Integer duracion;

    private String colaboradores;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    private boolean esSencillo;

    @Enumerated(EnumType.STRING)
    private Estado estado;

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
}