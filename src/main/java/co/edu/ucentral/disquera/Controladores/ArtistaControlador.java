package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Venta;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.ArtistaServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import co.edu.ucentral.disquera.Servicios.VentaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/artista")
public class ArtistaControlador {

    private static final Logger LOGGER = Logger.getLogger(ArtistaControlador.class.getName());

    private final ArtistaServicio artistaServicio;
    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;
    private final VentaServicio ventaServicio;

    @Autowired
    public ArtistaControlador(ArtistaServicio artistaServicio, AlbumServicio albumServicio,
                              CancionServicio cancionServicio, VentaServicio ventaServicio) {
        this.artistaServicio = artistaServicio;
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
        this.ventaServicio = ventaServicio;
    }

    @GetMapping("/panel")
    public String mostrarPanel(Model modelo, HttpSession session) {
        String usuario = getSessionUser(session);
        if (usuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Mostrando panel para artista: " + usuario);
        modelo.addAttribute("username", usuario);
        return "artista";
    }

    @GetMapping("/verlanzamientos")
    public String verLanzamientos(Model modelo, HttpSession session) {
        String usuario = getSessionUser(session);
        if (usuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Obteniendo lanzamientos para artista: " + usuario);

        var albumes = albumServicio.buscarPorUsuario(usuario);
        LOGGER.info("Álbumes encontrados: " + albumes.size());
        modelo.addAttribute("albumes", albumes);

        var canciones = cancionServicio.buscarPorUsuario(usuario);
        LOGGER.info("Canciones encontradas: " + canciones.size());
        modelo.addAttribute("canciones", canciones);

        modelo.addAttribute("username", usuario);
        return "verlanzamientos";
    }

    @GetMapping("/regalias")
    public String mostrarRegalias(Model model, HttpSession session) {
        String usuUsuario = getSessionUser(session);
        if (usuUsuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Consultando regalías para artista: " + usuUsuario);

        List<Venta> ventas = ventaServicio.consultarRegaliasPorUsuario(usuUsuario);
        double totalRegalias = ventaServicio.obtenerTotalRegaliasPendientes(usuUsuario);

        model.addAttribute("ventas", ventas);
        model.addAttribute("totalRegalias", totalRegalias);
        model.addAttribute("username", usuUsuario);
        return "artista_regalias";
    }

    @PostMapping("/regalias/solicitar")
    public String solicitarPagoRegalias(Model model, HttpSession session) {
        String usuUsuario = getSessionUser(session);
        if (usuUsuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Procesando solicitud de pago para artista: " + usuUsuario);

        boolean exito = ventaServicio.solicitarPagoRegalias(usuUsuario);
        if (exito) {
            model.addAttribute("mensaje", "Solicitud de pago enviada exitosamente.");
        } else {
            model.addAttribute("error", "No hay regalías pendientes para solicitar.");
        }

        return mostrarRegalias(model, session);
    }

    private String getSessionUser(HttpSession session) {
        String usuario = (String) session.getAttribute("username");
        LOGGER.info("Usuario en sesión: " + usuario);
        return usuario;
    }
}