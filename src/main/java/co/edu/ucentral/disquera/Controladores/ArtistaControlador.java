package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.ArtistaServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller
@RequestMapping("/artista")
public class ArtistaControlador {

    private static final Logger LOGGER = Logger.getLogger(ArtistaControlador.class.getName());

    private final ArtistaServicio artistaServicio;
    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;

    public ArtistaControlador(ArtistaServicio artistaServicio, AlbumServicio albumServicio, CancionServicio cancionServicio) {
        this.artistaServicio = artistaServicio;
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
    }

    @GetMapping("/panel")
    public String mostrarPanel(@RequestParam(value = "usuario", defaultValue = "artista1") String usuario, Model modelo) {
        LOGGER.info("Mostrando panel para artista: " + usuario);
        modelo.addAttribute("username", usuario);
        return "artista";
    }

    @GetMapping("/verlanzamientos")
    public String verLanzamientos(@RequestParam(value = "usuario", defaultValue = "artista1") String usuario, Model modelo) {
        LOGGER.info("Obteniendo lanzamientos para artista: " + usuario);

        // Obtener álbumes del artista
        var albumes = albumServicio.buscarPorUsuario(usuario);
        LOGGER.info("Álbumes encontrados: " + albumes.size());
        modelo.addAttribute("albumes", albumes);

        // Obtener canciones del artista
        var canciones = cancionServicio.buscarPorUsuario(usuario);
        LOGGER.info("Canciones encontradas: " + canciones.size());
        modelo.addAttribute("canciones", canciones);

        modelo.addAttribute("username", usuario);
        return "verlanzamientos";
    }
}