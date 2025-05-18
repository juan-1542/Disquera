package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ventas")
public class Venta {

    public enum Formato {
        DIGITAL,
        FISICO,
        STREAMING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ven_id")
    private Long id;

    @Column(name = "ven_unidades_vendidas", nullable = false)
    private Integer unidadesVendidas;

    @Column(name = "ven_fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    @Column(name = "ven_precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "ven_formato", nullable = false)
    @Enumerated(EnumType.STRING)
    private Formato formato;

    @Column(name = "ven_monto_regalias", nullable = false)
    private Double montoRegalias;

    @Column(name = "ven_estado_pago", nullable = false)
    private String estadoPago = "PENDIENTE"; // PENDIENTE, SOLICITADO, PAGADO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alb_id")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "can_id")
    private Cancion cancion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Usuario artista;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getUnidadesVendidas() { return unidadesVendidas; }
    public void setUnidadesVendidas(Integer unidadesVendidas) { this.unidadesVendidas = unidadesVendidas; }

    public LocalDate getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDate fechaVenta) { this.fechaVenta = fechaVenta; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Formato getFormato() { return formato; }
    public void setFormato(Formato formato) { this.formato = formato; }

    public Double getMontoRegalias() { return montoRegalias; }
    public void setMontoRegalias(Double montoRegalias) { this.montoRegalias = montoRegalias; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public Cancion getCancion() { return cancion; }
    public void setCancion(Cancion cancion) { this.cancion = cancion; }

    public Usuario getArtista() { return artista; }
    public void setArtista(Usuario artista) { this.artista = artista; }
}