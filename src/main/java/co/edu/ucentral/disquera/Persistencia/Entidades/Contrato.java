package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreReal;
    private String nombreArtistico;
    private String telefono;
    private Integer duracionMeses;
    private String generoMusical;
    private Double porcentajeGanancia;
    private LocalDate fechaCreacion;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private String estado;// Valores posibles: "Pendiente", "Activo", "Rechazado", "Vencido"
    private String motivoRechazo;

    @ManyToOne
    @JoinColumn(name = "artista_id")
    private Usuario artista;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public void setNombreReal(String nombreReal) {
        this.nombreReal = nombreReal;
    }

    public String getNombreArtistico() {
        return nombreArtistico;
    }

    public void setNombreArtistico(String nombreArtistico) {
        this.nombreArtistico = nombreArtistico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public void setGeneroMusical(String generoMusical) {
        this.generoMusical = generoMusical;
    }

    public Double getPorcentajeGanancia() {
        return porcentajeGanancia;
    }

    public void setPorcentajeGanancia(Double porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getArtista() {
        return artista;
    }

    public void setArtista(Usuario artista) {
        this.artista = artista;
    }

    public String calcularEstado() {
        LocalDate hoy = LocalDate.now();
        if ("Rechazado".equals(estado) || "Pendiente".equals(estado)) {
            return estado;
        }
        if (fechaInicio == null || fechaVencimiento == null) {
            return "Pendiente";
        }
        if (hoy.isBefore(fechaInicio)) {
            return "Pendiente";
        } else if (hoy.isAfter(fechaVencimiento)) {
            return "Vencido";
        } else {
            return "Activo";
        }
    }
    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    }
