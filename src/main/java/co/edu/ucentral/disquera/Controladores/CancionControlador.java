package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/canciones")
public class CancionControlador {

    private final CancionServicio cancionServicio;
    private final AlbumServicio albumServicio;

    public CancionControlador(CancionServicio cancionServicio, AlbumServicio albumServicio) {
        this.cancionServicio = cancionServicio;
        this.albumServicio = albumServicio;
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("cancion", new Cancion());
        model.addAttribute("albumes", albumServicio.listarTodos());
        return "/formulario_cancion";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cancion cancion, RedirectAttributes redirectAttributes) {
        try {
            // Si se seleccionó un álbum, cargarlo desde la base de datos
            if (cancion.getAlbum() != null && cancion.getAlbum().getId() != null) {
                Album album = albumServicio.buscarPorId(cancion.getAlbum().getId())
                        .orElseThrow(() -> new IllegalArgumentException("El álbum seleccionado no existe."));
                cancion.setAlbum(album);
            } else {
                // Si no se seleccionó un álbum, establecerlo como null
                cancion.setAlbum(null);
            }
            cancionServicio.guardar(cancion);
            redirectAttributes.addFlashAttribute("success", "Canción guardada exitosamente.");
            return "redirect:/albumes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/canciones/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la canción: " + e.getMessage());
            return "redirect:/canciones/nuevo";
        }
    }
}