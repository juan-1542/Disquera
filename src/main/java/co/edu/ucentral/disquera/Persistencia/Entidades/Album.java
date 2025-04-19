package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;

@Entity
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String genero;
    private int anio;

    @ManyToOne
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }
}
