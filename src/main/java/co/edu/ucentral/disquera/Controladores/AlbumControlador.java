package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

@Controller
@RequestMapping("/albumes")
public class AlbumControlador {

    private static final Logger LOGGER = Logger.getLogger(AlbumControlador.class.getName());

    private final AlbumServicio albumServicio;
    private final UsuarioServicio usuarioServicio;
    private final CancionServicio cancionServicio;

    public AlbumControlador(AlbumServicio albumServicio, UsuarioServicio usuarioServicio, CancionServicio cancionServicio) {
        this.albumServicio = albumServicio;
        this.usuarioServicio = usuarioServicio;
        this.cancionServicio = cancionServicio;
    }

    @GetMapping
    public String listar(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            LOGGER.info("Usuario autenticado: " + authentication.getName() + ", Authorities: " + authentication.getAuthorities());
            String username = authentication.getName();
            Usuario usuario = usuarioServicio.buscarPorUsuario(username);
            if (usuario != null && usuario.getRol() == Usuario.Rol.ARTISTA) {
                model.addAttribute("albumes", albumServicio.buscarPorUsuario(username));
            } else {
                model.addAttribute("albumes", albumServicio.listarTodos());
            }
        } else {
            LOGGER.warning("No hay usuario autenticado, mostrando todos los álbumes");
            model.addAttribute("albumes", albumServicio.listarTodos());
        }
        return "albumes";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        Album album = new Album();
        album.setCanciones(new ArrayList<>());
        model.addAttribute("album", album);
        model.addAttribute("sencillos", cancionServicio.listarSencillos());
        return "formulario_album";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Album album,
                          BindingResult bindingResult,
                          @RequestParam("portadaFile") MultipartFile portadaFile,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Errores en el formulario: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/albumes/nuevo";
        }
        try {
            // Validar que el usuario autenticado sea un artista
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                LOGGER.severe("No hay usuario autenticado en /albumes/guardar");
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para enviar álbumes.");
                return "redirect:/login";
            }
            String username = authentication.getName();
            Usuario usuario = usuarioServicio.buscarPorUsuario(username);
            if (usuario == null || usuario.getRol() != Usuario.Rol.ARTISTA) {
                redirectAttributes.addFlashAttribute("error", "Usuario no autorizado para enviar álbumes.");
                return "redirect:/albumes/nuevo";
            }
            // Asignar el usuario autenticado al álbum
            album.setUsuario(usuario);
            // Establecer estado como PENDIENTE
            album.setEstado(Album.Estado.PENDIENTE);
            // Asignar estado PENDIENTE a todas las canciones
            if (album.getCanciones() != null) {
                album.getCanciones().forEach(cancion -> cancion.setEstado(Cancion.Estado.PENDIENTE));
            }
            // Manejar la carga de la portada
            if (!portadaFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/images/" + fileName);
                Files.write(path, portadaFile.getBytes());
                album.setPortada("/images/" + fileName);
            }
            // Actualizar número de canciones
            album.setNumeroCanciones(album.getCanciones() != null ? album.getCanciones().size() : 0);
            albumServicio.guardar(album);
            redirectAttributes.addFlashAttribute("success", "Álbum enviado para aprobación.");
            return "redirect:/albumes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar el álbum: " + e.getMessage());
            return "redirect:/albumes/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Album album = albumServicio.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con ID: " + id));
            model.addAttribute("album", album);
            model.addAttribute("sencillos", cancionServicio.listarSencillos());
            return "formulario_album";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/albumes";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            albumServicio.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Álbum eliminado exitosamente.");
            return "redirect:/albumes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el álbum: " + e.getMessage());
            return "redirect:/albumes";
        }
    }
}