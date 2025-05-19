package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.logging.Logger;

@Controller
@RequestMapping("/")
public class UsuarioControlador {

    private static final Logger LOGGER = Logger.getLogger(UsuarioControlador.class.getName());

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("")
    public String mostrarInicio(Model model) {
        model.addAttribute("credenciales", new Usuario());
        return "index";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("elusuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("elusuario") Usuario usuario, Model model) {
        try {
            usuarioServicio.registrarUsuario(usuario);
            LOGGER.info("Usuario registrado: " + usuario.getUsuario());
            return "redirect:/iniciosesion";
        } catch (Exception e) {
            LOGGER.severe("Error al registrar usuario: " + e.getMessage());
            model.addAttribute("error", "No se pudo registrar el usuario.");
            return "registro";
        }
    }

    @GetMapping("/iniciosesion")
    public String mostrarPanelinicios(Model model) {
        model.addAttribute("credenciales", new Usuario());
        return "iniciosesion";
    }

    @PostMapping("/iniciosesion")
    public String procesarLogin(@ModelAttribute("credenciales") Usuario usuario, HttpSession session, Model model) {
        LOGGER.info("Procesando login para usuario: " + usuario.getUsuario());
        Usuario usuarioEncontrado = usuarioServicio.autenticar(
                usuario.getUsuario(), usuario.getContrasena());

        if (usuarioEncontrado != null) {
            session.setAttribute("username", usuarioEncontrado.getUsuario());
            LOGGER.info("Login exitoso, usuario: " + usuarioEncontrado.getUsuario());
            if (usuarioEncontrado.getRol() == Usuario.Rol.ADMIN) {
                return "redirect:/admin";
            } else if (usuarioEncontrado.getRol() == Usuario.Rol.ARTISTA) {
                return "redirect:/artista";
            } else {
                model.addAttribute("error", "Rol no reconocido");
                return "iniciosesion";
            }
        } else {
            LOGGER.warning("Login fallido para usuario: " + usuario.getUsuario());
            model.addAttribute("error", "Credenciales incorrectas");
            return "iniciosesion";
        }
    }

    @GetMapping("/admin")
    public String mostrarPanelAdmin() {
        return "admin";
    }

    @GetMapping("/artista")
    public String mostrarPanelArtista() {
        return "artista";
    }

    @GetMapping("/recuperarcontraseña")
    public String mostrarRecuperarContrasena() {
        return "recuperarcontrasena";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        LOGGER.info("Cerrando sesión para usuario: " + session.getAttribute("username"));
        session.invalidate();
        return "redirect:/iniciosesion?logout";
    }
    @GetMapping("/terminos")
    public String mostrarPanelTerminos() {
        return "terminos";
    }
}