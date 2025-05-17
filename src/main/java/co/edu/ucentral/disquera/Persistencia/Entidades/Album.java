package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
public class Album {

    public enum Estado {
        PENDIENTE,
        APROBADO,
        RECHAZADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaLanzamiento;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private String genero;

    private String portada;

    private Integer numeroCanciones;

    private String formatoDistribucion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cancion> canciones;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Date getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(Date fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getPortada() { return portada; }
    public void setPortada(String portada) { this.portada = portada; }

    public Integer getNumeroCanciones() { return numeroCanciones; }
    public void setNumeroCanciones(Integer numeroCanciones) { this.numeroCanciones = numeroCanciones; }

    public String getFormatoDistribucion() { return formatoDistribucion; }
    public void setFormatoDistribucion(String formatoDistribucion) { this.formatoDistribucion = formatoDistribucion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Cancion> getCanciones() { return canciones; }
    public void setCanciones(List<Cancion> canciones) { this.canciones = canciones; }
}