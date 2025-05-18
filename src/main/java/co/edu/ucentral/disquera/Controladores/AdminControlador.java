package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Entidades.Venta;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import co.edu.ucentral.disquera.Servicios.ContratoService;
import co.edu.ucentral.disquera.Servicios.VentaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    private static final Logger LOGGER = Logger.getLogger(AdminControlador.class.getName());

    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;
    private final ContratoService contratoServicio;
    private final VentaServicio ventaServicio;

    public AdminControlador(AlbumServicio albumServicio, CancionServicio cancionServicio,
                            ContratoService contratoService, VentaServicio ventaServicio) {
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
        this.contratoServicio = contratoService;
        this.ventaServicio = ventaServicio;
    }

    @GetMapping("/ventas")
    public String listarVentas(Model modelo) {
        LocalDate hoy = LocalDate.now();

        List<Album> albumes = albumServicio.listarTodos().stream()
                .filter(album -> album.getEstado() == Album.Estado.APROBADO)
                .filter(album -> {
                    Date fechaLanzamiento = album.getFechaLanzamiento();
                    if (fechaLanzamiento == null) {
                        LOGGER.warning("Álbum " + album.getNombre() + " tiene fechaLanzamiento nula");
                        return false;
                    }
                    LocalDate fecha = fechaLanzamiento instanceof java.sql.Date
                            ? ((java.sql.Date) fechaLanzamiento).toLocalDate()
                            : fechaLanzamiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return fecha.isBefore(hoy.plusDays(1));
                })
                .collect(Collectors.toList());

        List<Cancion> sencillos = cancionServicio.listarSencillos().stream()
                .filter(cancion -> cancion.getEstado() == Cancion.Estado.APROBADO)
                .filter(cancion -> {
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
                    return fechaLanzamiento.isBefore(hoy.plusDays(1));
                })
                .collect(Collectors.toList());

        modelo.addAttribute("albumes", albumes);
        modelo.addAttribute("sencillos", sencillos);
        modelo.addAttribute("formatos", Venta.Formato.values());
        return "admin_ventas";
    }

    @PostMapping("/ventas/registrar")
    public String registrarVenta(
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam(value = "cancionId", required = false) Long cancionId,
            @RequestParam("unidadesVendidas") Integer unidadesVendidas,
            @RequestParam("usuUsuario") String usuUsuario,
            @RequestParam("formato") Venta.Formato formato,
            Model modelo) {
        LOGGER.info("Registrando venta: albumId=" + albumId + ", cancionId=" + cancionId +
                ", unidades=" + unidadesVendidas + ", usuario=" + usuUsuario + ", formato=" + formato);

        boolean exito = ventaServicio.registrarVenta(albumId, cancionId, unidadesVendidas, usuUsuario, formato);
        if (exito) {
            return "redirect:/admin/ventas?success";
        } else {
            modelo.addAttribute("error", "No se pudo registrar la venta. Verifique los datos.");
            return listarVentas(modelo);
        }
    }
}