package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lanzamientos")
@PreAuthorize("hasRole('ADMIN')")
public class LanzamientoControlador {

    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;

    public LanzamientoControlador(AlbumServicio albumServicio, CancionServicio cancionServicio) {
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("albumes", albumServicio.listarTodos());
        model.addAttribute("canciones", cancionServicio.listarTodas());
        return "gestion_lanzamientos";
    }

    @PostMapping("/album/aprobar/{id}")
    public String aprobarAlbum(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Album album = albumServicio.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con ID: " + id));
            album.setEstado(Album.Estado.APROBADO);
            // Aprobar todas las canciones del álbum
            if (album.getCanciones() != null) {
                album.getCanciones().forEach(cancion -> cancion.setEstado(Cancion.Estado.APROBADO));
            }
            albumServicio.guardar(album);
            redirectAttributes.addFlashAttribute("success", "Álbum aprobado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lanzamientos";
    }

    @PostMapping("/album/rechazar/{id}")
    public String rechazarAlbum(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Album album = albumServicio.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con ID: " + id));
            album.setEstado(Album.Estado.RECHAZADO);
            // Rechazar todas las canciones del álbum
            if (album.getCanciones() != null) {
                album.getCanciones().forEach(cancion -> cancion.setEstado(Cancion.Estado.RECHAZADO));
            }
            albumServicio.guardar(album);
            redirectAttributes.addFlashAttribute("success", "Álbum rechazado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lanzamientos";
    }

    @PostMapping("/cancion/aprobar/{id}")
    public String aprobarCancion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cancion cancion = cancionServicio.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Canción no encontrada con ID: " + id));
            cancion.setEstado(Cancion.Estado.APROBADO);
            cancionServicio.guardar(cancion);
            redirectAttributes.addFlashAttribute("success", "Canción aprobada exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lanzamientos";
    }

    @PostMapping("/cancion/rechazar/{id}")
    public String rechazarCancion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cancion cancion = cancionServicio.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Canción no encontrada con ID: " + id));
            cancion.setEstado(Cancion.Estado.RECHAZADO);
            cancionServicio.guardar(cancion);
            redirectAttributes.addFlashAttribute("success", "Canción rechazada exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lanzamientos";
    }
}