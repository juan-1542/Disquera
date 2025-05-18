package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin/lanzamiento")
public class LanzamientoControlador {

    private static final Logger LOGGER = Logger.getLogger(LanzamientoControlador.class.getName());

    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;

    public LanzamientoControlador(AlbumServicio albumServicio, CancionServicio cancionServicio) {
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPendientes(Model model) {
        LOGGER.info("Accediendo a /admin/lanzamiento como ADMIN");
        List<Album> albumesPendientes = albumServicio.buscarPorEstado(Album.Estado.PENDIENTE);
        List<Cancion> cancionesPendientes = cancionServicio.buscarPorEstado(Cancion.Estado.PENDIENTE);

        LOGGER.info("Álbumes pendientes encontrados: " + albumesPendientes.size());
        for (Album album : albumesPendientes) {
            LOGGER.info("Álbum: ID=" + album.getId() +
                    ", Nombre=" + album.getNombre() +
                    ", Estado=" + album.getEstado() +
                    ", FechaLanzamiento=" + album.getFechaLanzamiento() +
                    ", NumeroCanciones=" + album.getNumeroCanciones() +
                    ", Usuario=" + (album.getUsuario() != null ? album.getUsuario().getNombre() : "null"));
        }

        LOGGER.info("Canciones pendientes encontradas: " + cancionesPendientes.size());
        for (Cancion cancion : cancionesPendientes) {
            LOGGER.info("Canción: ID=" + cancion.getId() +
                    ", Título=" + cancion.getTitulo() +
                    ", Estado=" + cancion.getEstado() +
                    ", Duración=" + cancion.getDuracion() +
                    ", Colaboradores=" + cancion.getColaboradores() +
                    ", EsSencillo=" + cancion.isEsSencillo() +
                    ", Álbum=" + (cancion.getAlbum() != null ? cancion.getAlbum().getNombre() : "null"));
        }

        model.addAttribute("albumesPendientes", albumesPendientes);
        model.addAttribute("cancionesPendientes", cancionesPendientes);
        return "gestion_lanzamientos";
    }

    @PostMapping("/aprobar/{tipo}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String aprobar(@PathVariable String tipo, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if ("album".equalsIgnoreCase(tipo)) {
                Album album = albumServicio.buscarPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con ID: " + id));
                album.setEstado(Album.Estado.APROBADO);
                albumServicio.guardar(album);
                redirectAttributes.addFlashAttribute("success", "Álbum aprobado exitosamente.");
            } else if ("cancion".equalsIgnoreCase(tipo)) {
                Cancion cancion = cancionServicio.buscarPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Canción no encontrada con ID: " + id));
                cancion.setEstado(Cancion.Estado.APROBADO);
                cancionServicio.guardar(cancion);
                redirectAttributes.addFlashAttribute("success", "Canción aprobada exitosamente.");
            } else {
                throw new IllegalArgumentException("Tipo inválido: " + tipo);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Error al aprobar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/lanzamiento";
    }

    @PostMapping("/rechazar/{tipo}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String rechazar(@PathVariable String tipo, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if ("album".equalsIgnoreCase(tipo)) {
                Album album = albumServicio.buscarPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con ID: " + id));
                album.setEstado(Album.Estado.RECHAZADO);
                albumServicio.guardar(album);
                redirectAttributes.addFlashAttribute("success", "Álbum rechazado exitosamente.");
            } else if ("cancion".equalsIgnoreCase(tipo)) {
                Cancion cancion = cancionServicio.buscarPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Canción no encontrada con ID: " + id));
                cancion.setEstado(Cancion.Estado.RECHAZADO);
                cancionServicio.guardar(cancion);
                redirectAttributes.addFlashAttribute("success", "Canción rechazada exitosamente.");
            } else {
                throw new IllegalArgumentException("Tipo inválido: " + tipo);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Error al rechazar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/lanzamiento";
    }
}