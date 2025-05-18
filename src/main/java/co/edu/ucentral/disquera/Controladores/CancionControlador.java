package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@Controller
@RequestMapping("/canciones")
public class CancionControlador {

    private static final Logger LOGGER = Logger.getLogger(CancionControlador.class.getName());

    private final CancionServicio cancionServicio;
    private final AlbumServicio albumServicio;
    private final UsuarioServicio usuarioServicio;

    public CancionControlador(CancionServicio cancionServicio, AlbumServicio albumServicio, UsuarioServicio usuarioServicio) {
        this.cancionServicio = cancionServicio;
        this.albumServicio = albumServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        LOGGER.info("Cargando formulario para nueva canción");
        model.addAttribute("cancion", new Cancion());
        model.addAttribute("albumes", albumServicio.listarTodos());
        model.addAttribute("artistas", usuarioServicio.listarArtistas());
        LOGGER.info("Álbumes cargados: " + albumServicio.listarTodos().size());
        LOGGER.info("Artistas cargados: " + usuarioServicio.listarArtistas().size());
        return "formulario_cancion";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cancion cancion,
                          @ModelAttribute("usuarioId") String usuarioId,
                          RedirectAttributes redirectAttributes) {
        try {
            LOGGER.info("Guardando canción: Título=" + cancion.getTitulo() + ", usuarioId=" + usuarioId);

            // Asignar usuario desde usuarioId
            if (usuarioId == null || usuarioId.isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar un artista.");
            }
            Usuario usuario = usuarioServicio.buscarPorUsuario(usuarioId);
            if (usuario == null) {
                throw new IllegalArgumentException("El artista seleccionado no existe.");
            }
            cancion.setUsuario(usuario);
            LOGGER.info("Usuario asignado: " + usuario.getNombre());

            // Si se seleccionó un álbum, cargarlo desde la base de datos
            if (cancion.getAlbum() != null && cancion.getAlbum().getId() != null) {
                Album album = albumServicio.buscarPorId(cancion.getAlbum().getId())
                        .orElseThrow(() -> new IllegalArgumentException("El álbum seleccionado no existe."));
                cancion.setAlbum(album);
                LOGGER.info("Álbum asignado: " + album.getNombre());
            } else {
                cancion.setAlbum(null);
                LOGGER.info("No se asignó álbum (sencillo o sin álbum)");
            }

            // Establecer estado PENDIENTE
            cancion.setEstado(Cancion.Estado.PENDIENTE);
            LOGGER.info("Estado establecido: PENDIENTE");

            // Guardar la canción
            Cancion savedCancion = cancionServicio.guardar(cancion);
            LOGGER.info("Canción guardada exitosamente: ID=" + savedCancion.getId());
            redirectAttributes.addFlashAttribute("success", "Canción guardada exitosamente.");
            return "redirect:/albumes";
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Error al guardar la canción: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/canciones/nuevo";
        } catch (Exception e) {
            LOGGER.severe("Error inesperado al guardar la canción: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al guardar la canción: " + e.getMessage());
            return "redirect:/canciones/nuevo";
        }
    }
}