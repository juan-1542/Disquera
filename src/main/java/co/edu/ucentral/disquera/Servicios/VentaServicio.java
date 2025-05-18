package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.*;
import co.edu.ucentral.disquera.Persistencia.Repositorio.VentaRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.logging.Logger;

@Service
public class VentaServicio {

    private static final Logger LOGGER = Logger.getLogger(VentaServicio.class.getName());

    private final VentaRepositorio ventaRepositorio;
    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;
    private final ContratoService contratoService;

    public VentaServicio(VentaRepositorio ventaRepositorio, AlbumServicio albumServicio,
                         CancionServicio cancionServicio, ContratoService contratoService) {
        this.ventaRepositorio = ventaRepositorio;
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
        this.contratoService = contratoService;
    }

    public boolean registrarVenta(Long albumId, Long cancionId, Integer unidadesVendidas, String usuUsuario) {
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

        Venta venta = new Venta();
        venta.setUnidadesVendidas(unidadesVendidas);
        venta.setFechaVenta(hoy);
        venta.setArtista(new Usuario());
        venta.getArtista().setUsuario(usuUsuario);

        if (albumId != null) {
            Album album = albumServicio.buscarPorId(albumId)
                    .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado: " + albumId));
            
            if (album.getEstado() != Album.Estado.APROBADO) {
                LOGGER.warning("El álbum no está aprobado: " + album.getNombre());
                return false;
            }

            LocalDate fechaLanzamiento = album.getFechaLanzamiento()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (fechaLanzamiento.isAfter(hoy)) {
                LOGGER.warning("La fecha de lanzamiento del álbum aún no ha pasado: " + album.getNombre());
                return false;
            }

            venta.setAlbum(album);
            venta.setPrecioUnitario(10.0); // Precio fijo para álbumes
        } else {
            Cancion cancion = cancionServicio.buscarPorId(cancionId)
                    .orElseThrow(() -> new IllegalArgumentException("Canción no encontrada: " + cancionId));
            
            if (!cancion.isEsSencillo()) {
                LOGGER.warning("La canción no es un sencillo: " + cancion.getTitulo());
                return false;
            }

            if (cancion.getEstado() != Cancion.Estado.APROBADO) {
                LOGGER.warning("La canción no está aprobada: " + cancion.getTitulo());
                return false;
            }

            // Asumimos que la canción tiene una fecha de lanzamiento asociada al álbum o es la misma
            LocalDate fechaLanzamiento = cancion.getAlbum() != null
                    ? cancion.getAlbum().getFechaLanzamiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    : hoy; // Si es sencillo sin álbum, usamos hoy como fallback
            if (fechaLanzamiento.isAfter(hoy)) {
                LOGGER.warning("La fecha de lanzamiento de la canción aún no ha pasado: " + cancion.getTitulo());
                return false;
            }

            venta.setCancion(cancion);
            venta.setPrecioUnitario(2.0); // Precio fijo para sencillos
        }

        ventaRepositorio.save(venta);
        LOGGER.info("Venta registrada exitosamente para usuario: " + usuUsuario);
        return true;
    }
}