package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import jakarta.validation.Valid;
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
import java.util.List;
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
        LOGGER.info("Listando álbumes");
        List<Album> albumes = albumServicio.listarTodos();
        LOGGER.info("Álbumes totales encontrados: " + albumes.size());
        model.addAttribute("albumes", albumes);
        return "albumes";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        Album album = new Album();
        album.setCanciones(new ArrayList<>());
        List<Cancion> sencillos = cancionServicio.listarSencillos();
        List<Usuario> artistas = usuarioServicio.listarArtistas();
        LOGGER.info("Sencillos encontrados: " + sencillos.size());
        LOGGER.info("Artistas encontrados: " + artistas.size());
        for (Cancion sencillo : sencillos) {
            LOGGER.info("Sencillo: ID=" + sencillo.getId() +
                    ", Título=" + sencillo.getTitulo() +
                    ", Estado=" + sencillo.getEstado() +
                    ", Duración=" + sencillo.getDuracion() +
                    ", Colaboradores=" + sencillo.getColaboradores() +
                    ", EsSencillo=" + sencillo.isEsSencillo());
        }
        for (Usuario artista : artistas) {
            LOGGER.info("Artista: Usuario=" + artista.getUsuario() + ", Nombre=" + artista.getNombre());
        }
        model.addAttribute("album", album);
        model.addAttribute("sencillos", sencillos);
        model.addAttribute("artistas", artistas);
        return "formulario_album";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Album album,
                          BindingResult bindingResult,
                          @RequestParam("portadaFile") MultipartFile portadaFile,
                          @RequestParam(value = "sencilloIds", required = false) List<Long> sencilloIds,
                          @RequestParam("usuarioId") String usuarioId,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            LOGGER.warning("Errores de validación en el formulario: " + bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Errores en el formulario: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/albumes/nuevo";
        }
        try {
            // Asignar usuario desde usuarioId
            LOGGER.info("Buscando usuario con ID: " + usuarioId);
            Usuario usuario = usuarioServicio.buscarPorUsuario(usuarioId);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
            }
            album.setUsuario(usuario);
            LOGGER.info("Usuario asignado al álbum: " + usuario.getNombre());

            // Establecer estado PENDIENTE para el álbum
            album.setEstado(Album.Estado.PENDIENTE);
            LOGGER.info("Estado del álbum establecido: PENDIENTE");

            // Inicializar lista de canciones si es null
            if (album.getCanciones() == null) {
                album.setCanciones(new ArrayList<>());
            }

            // Filtrar canciones válidas y asignar álbum y usuario
            List<Cancion> cancionesValidas = new ArrayList<>();
            for (Cancion cancion : album.getCanciones()) {
                if (cancion.getTitulo() != null && !cancion.getTitulo().isEmpty()) {
                    LOGGER.info("Procesando nueva canción: " + cancion.getTitulo());
                    cancion.setEstado(Cancion.Estado.PENDIENTE);
                    cancion.setEsSencillo(false);
                    cancion.setAlbum(album);
                    cancion.setUsuario(usuario); // Asignar el mismo usuario del álbum
                    cancionesValidas.add(cancion);
                    LOGGER.info("Canción válida añadida: " + cancion.getTitulo());
                }
            }
            album.setCanciones(cancionesValidas);

            // Manejar sencillos seleccionados
            if (sencilloIds != null && !sencilloIds.isEmpty()) {
                LOGGER.info("Asociando sencillos existentes: " + sencilloIds);
                List<Cancion> sencillos = cancionServicio.buscarPorIds(sencilloIds);
                for (Cancion sencillo : sencillos) {
                    LOGGER.info("Actualizando sencillo: ID=" + sencillo.getId() + ", Título=" + sencillo.getTitulo());
                    sencillo.setAlbum(album);
                    sencillo.setEsSencillo(false); // Ya no es sencillo
                    sencillo.setEstado(Cancion.Estado.PENDIENTE);
                    sencillo.setUsuario(usuario); // Asignar el mismo usuario del álbum
                    album.getCanciones().add(sencillo);
                    LOGGER.info("Sencillo asociado: " + sencillo.getTitulo());
                }
            }

            // Manejar portada
            if (!portadaFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/images/" + fileName);
                Files.write(path, portadaFile.getBytes());
                album.setPortada("/images/" + fileName);
                LOGGER.info("Portada guardada: " + fileName);
            }

            // Actualizar número de canciones
            album.setNumeroCanciones(album.getCanciones().size());
            LOGGER.info("Guardando álbum: Nombre=" + album.getNombre() +
                    ", FechaLanzamiento=" + album.getFechaLanzamiento() +
                    ", NumeroCanciones=" + album.getNumeroCanciones() +
                    ", Usuario=" + (album.getUsuario() != null ? album.getUsuario().getNombre() : "null"));



            // Guardar el álbum
            albumServicio.guardar(album);
            LOGGER.info("Álbum guardado exitosamente: ID=" + album.getId());

            redirectAttributes.addFlashAttribute("success", "Álbum enviado para aprobación.");
            return "redirect:/albumes";
        } catch (IOException e) {
            LOGGER.severe("Error al cargar la portada: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cargar la portada: " + e.getMessage());
            return "redirect:/albumes/nuevo";
        } catch (Exception e) {
            LOGGER.severe("Error al guardar el álbum: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al enviar el álbum: " + e.getMessage());
            return "redirect:/albumes/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Album album = albumServicio.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con ID: " + id));
            List<Cancion> sencillos = cancionServicio.listarSencillos();
            List<Usuario> artistas = usuarioServicio.listarArtistas();
            LOGGER.info("Editando álbum: ID=" + album.getId() +
                    ", Nombre=" + album.getNombre() +
                    ", FechaLanzamiento=" + album.getFechaLanzamiento() +
                    ", NumeroCanciones=" + album.getNumeroCanciones() +
                    ", Usuario=" + (album.getUsuario() != null ? album.getUsuario().getNombre() : "null"));
            LOGGER.info("Sencillos encontrados para edición: " + sencillos.size());
            LOGGER.info("Artistas encontrados: " + artistas.size());
            model.addAttribute("album", album);
            model.addAttribute("sencillos", sencillos);
            model.addAttribute("artistas", artistas);
            return "formulario_album";
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Error al editar álbum: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/albumes";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            albumServicio.eliminar(id);
            LOGGER.info("Álbum eliminado: ID=" + id);
            redirectAttributes.addFlashAttribute("success", "Álbum eliminado exitosamente.");
            return "redirect:/albumes";
        } catch (Exception e) {
            LOGGER.severe("Error al eliminar álbum: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el álbum: " + e.getMessage());
            return "redirect:/albumes";
        }
    }
}