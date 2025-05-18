package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.*;
import co.edu.ucentral.disquera.Persistencia.Repositorio.VentaRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class VentaServicio {

    private static final Logger LOGGER = Logger.getLogger(VentaServicio.class.getName());

    private final VentaRepositorio ventaRepositorio;
    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;
    private final ContratoService contratoService;
    private final UsuarioServicio usuarioServicio;

    public VentaServicio(VentaRepositorio ventaRepositorio, AlbumServicio albumServicio,
                         CancionServicio cancionServicio, ContratoService contratoService,
                         UsuarioServicio usuarioServicio) {
        this.ventaRepositorio = ventaRepositorio;
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
        this.contratoService = contratoService;
        this.usuarioServicio = usuarioServicio;
    }

    public boolean registrarVenta(Long albumId, Long cancionId, Integer unidadesVendidas, String usuUsuario, Venta.Formato formato) {
        if (albumId != null && cancionId != null) {
            LOGGER.warning("No se puede registrar venta para álbum y canción simultáneamente");
            return false;
        }

        if (albumId == null && cancionId == null) {
            LOGGER.warning("Debe especificar un álbum o una canción");
            return false;
        }

        LocalDate hoy = LocalDate.now();
        Contrato contrato = contratoService.buscarContratoActivo(usuUsuario);
        if (contrato == null) {
            LOGGER.warning("No se encontró un contrato activo para el usuario: " + usuUsuario);
            return false;
        }

        Usuario artista = usuarioServicio.buscarPorUsuario(usuUsuario);
        if (artista == null) {
            LOGGER.warning("Usuario no encontrado: " + usuUsuario);
            return false;
        }

        Venta venta = new Venta();
        venta.setUnidadesVendidas(unidadesVendidas);
        venta.setFechaVenta(hoy);
        venta.setFormato(formato);
        venta.setArtista(artista);
        venta.setEstadoPago("PENDIENTE");

        double precioUnitario;
        if (albumId != null) {
            Album album = albumServicio.buscarPorId(albumId).orElse(null);
            if (album == null) {
                LOGGER.warning("Álbum no encontrado: " + albumId);
                return false;
            }

            if (album.getEstado() != Album.Estado.APROBADO) {
                LOGGER.warning("El álbum no está aprobado: " + album.getNombre());
                return false;
            }

            Date fechaLanzamiento = album.getFechaLanzamiento();
            if (fechaLanzamiento == null) {
                LOGGER.warning("El álbum " + album.getNombre() + " tiene fechaLanzamiento nula");
                return false;
            }

            LocalDate fechaLanzamientoLocal = fechaLanzamiento instanceof java.sql.Date
                    ? ((java.sql.Date) fechaLanzamiento).toLocalDate()
                    : fechaLanzamiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (fechaLanzamientoLocal.isAfter(hoy)) {
                LOGGER.warning("La fecha de lanzamiento del álbum aún no ha pasado: " + album.getNombre());
                return false;
            }

            venta.setAlbum(album);
            precioUnitario = switch (formato) {
                case FISICO -> 10.0;
                case DIGITAL -> 8.0;
                case STREAMING -> 0.01;
            };
            venta.setPrecioUnitario(precioUnitario);
        } else {
            Cancion cancion = cancionServicio.buscarPorId(cancionId).orElse(null);
            if (cancion == null) {
                LOGGER.warning("Canción no encontrada: " + cancionId);
                return false;
            }

            if (!cancion.isEsSencillo()) {
                LOGGER.warning("La canción no es un sencillo: " + cancion.getTitulo());
                return false;
            }

            if (cancion.getEstado() != Cancion.Estado.APROBADO) {
                LOGGER.warning("La canción no está aprobada: " + cancion.getTitulo());
                return false;
            }

            LocalDate fechaLanzamiento;
            if (cancion.getAlbum() != null) {
                Date fecha = cancion.getAlbum().getFechaLanzamiento();
                if (fecha == null) {
                    LOGGER.warning("Canción " + cancion.getTitulo() + " tiene álbum con fechaLanzamiento nula");
                    return false;
                }
                fechaLanzamiento = fecha instanceof java.sql.Date
                        ? ((java.sql.Date) fecha).toLocalDate()
                        : fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                fechaLanzamiento = hoy;
            }

            if (fechaLanzamiento.isAfter(hoy)) {
                LOGGER.warning("La fecha de lanzamiento de la canción aún no ha pasado: " + cancion.getTitulo());
                return false;
            }

            venta.setCancion(cancion);
            precioUnitario = switch (formato) {
                case FISICO -> 2.0;
                case DIGITAL -> 1.0;
                case STREAMING -> 0.005;
            };
            venta.setPrecioUnitario(precioUnitario);
        }

        double ingresosTotales = unidadesVendidas * precioUnitario;
        double porcentajeRegalias = contrato.getPorcentajeGanancia() / 100.0;
        double montoRegalias;

        if (cancionId != null) {
            Cancion cancion = cancionServicio.buscarPorId(cancionId).orElse(null);
            if (cancion != null && cancion.getColaboradores() != null && !cancion.getColaboradores().isEmpty()) {
                int totalArtistas = cancion.getColaboradores().size() + 1;
                montoRegalias = (ingresosTotales * porcentajeRegalias) / totalArtistas;
            } else {
                montoRegalias = ingresosTotales * porcentajeRegalias;
            }
        } else {
            montoRegalias = ingresosTotales * porcentajeRegalias;
        }

        venta.setMontoRegalias(montoRegalias);
        try {
            ventaRepositorio.save(venta);
            LOGGER.info("Venta registrada exitosamente para usuario: " + usuUsuario + ", monto regalías: " + montoRegalias);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error al registrar venta: " + e.getMessage());
            return false;
        }
    }

    public List<Venta> consultarRegaliasPorUsuario(String usuUsuario) {
        LOGGER.info("Consultando regalías para usuario: " + usuUsuario);
        return ventaRepositorio.findByArtista_UsuarioAndEstadoPago(usuUsuario, "PENDIENTE");
    }

    public boolean solicitarPagoRegalias(String usuUsuario) {
        LOGGER.info("Solicitando pago de regalías para usuario: " + usuUsuario);
        List<Venta> ventasPendientes = ventaRepositorio.findByArtista_UsuarioAndEstadoPago(usuUsuario, "PENDIENTE");
        if (ventasPendientes.isEmpty()) {
            LOGGER.warning("No hay regalías pendientes para el usuario: " + usuUsuario);
            return false;
        }

        for (Venta venta : ventasPendientes) {
            venta.setEstadoPago("SOLICITADO");
            ventaRepositorio.save(venta);
        }
        LOGGER.info("Pago de regalías solicitado para usuario: " + usuUsuario);
        return true;
    }

    public double obtenerTotalRegaliasPendientes(String usuUsuario) {
        List<Venta> ventas = consultarRegaliasPorUsuario(usuUsuario);
        return ventas.stream()
                .mapToDouble(Venta::getMontoRegalias)
                .sum();
    }

    public List<Venta> consultarVentasPorEstado(String estadoPago) {
        LOGGER.info("Consultando ventas con estado: " + estadoPago);
        return ventaRepositorio.findByEstadoPago(estadoPago);
    }

    public List<Venta> consultarVentasPorUsuarioYEstados(String usuUsuario, List<String> estados) {
        LOGGER.info("Consultando ventas para usuario: " + usuUsuario + ", estados: " + estados);
        return ventaRepositorio.findByArtista_UsuarioAndEstadoPagoIn(usuUsuario, estados);
    }

    public boolean aprobarPago(Long ventaId) {
        LOGGER.info("Aprobando pago para venta ID: " + ventaId);
        Venta venta = ventaRepositorio.findById(ventaId).orElse(null);
        if (venta == null) {
            LOGGER.warning("Venta no encontrada: " + ventaId);
            return false;
        }
        if (!"SOLICITADO".equals(venta.getEstadoPago())) {
            LOGGER.warning("La venta no está en estado SOLICITADO: " + ventaId);
            return false;
        }
        venta.setEstadoPago("PAGADO");
        ventaRepositorio.save(venta);
        LOGGER.info("Pago aprobado para venta ID: " + ventaId);
        return true;
    }
}