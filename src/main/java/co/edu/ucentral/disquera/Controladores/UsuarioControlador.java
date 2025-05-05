package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

@Controller
@RequestMapping("/")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("")
    public String mostrarInicio(Model model) {
        model.addAttribute("credenciales", new Usuario());
        return "index"; // Asegúrate de que la vista de inicio se llama 'iniciosesion'
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("elusuario", new Usuario());
        return "registro"; // Esta vista debe existir en tu proyecto
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("elusuario") Usuario usuario, Model model) {
        try {

            usuarioServicio.registrarUsuario(usuario);
            return "redirect:/iniciosesion"; // Redirige correctamente a la página de inicio de sesión
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo registrar el usuario.");
            return "registro"; // Si hay error, regresa a la vista de registro
        }
    }

    @GetMapping("/iniciosesion")
    public String mostrarPanelinicios(Model model) {
        model.addAttribute("credenciales", new Usuario()); // Asegura que el formulario de inicio tenga un objeto Usuario vacío
        return "iniciosesion"; // Carga el template de inicio de sesión
    }

    @PostMapping("/iniciosesion")
    public String procesarLogin(@ModelAttribute("credenciales") Usuario usuario, Model model) {
        Usuario usuarioEncontrado = usuarioServicio.autenticar(
                usuario.getUsuario(), usuario.getContrasena());

        if (usuarioEncontrado != null) {
            if (usuarioEncontrado.getRol() == Usuario.Rol.ADMIN) {
                return "redirect:/admin"; // Redirige al panel de admin si el rol es ADMIN
            } else if (usuarioEncontrado.getRol() == Usuario.Rol.ARTISTA) {
                return "redirect:/artista"; // Redirige al panel de artista si el rol es ARTISTA
            } else {
                model.addAttribute("error", "Rol no reconocido");
                return "iniciosesion"; // Si el rol no es reconocido, vuelve al formulario de login
            }
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "iniciosesion"; // Si las credenciales son incorrectas, vuelve al formulario de login
        }
    }

    @GetMapping("/admin")
    public String mostrarPanelAdmin() {
        return "admin"; // Carga el template para el panel de administración
    }

    @GetMapping("/artista")
    public String mostrarPanelArtista() {
        return "artista"; // Carga el template para el panel del artista
    }
    @GetMapping("/recuperarcontraseña")
    public String mostrarRecuperarContrasena() {
        return "recuperarcontrasena"; // Vista para el formulario de recuperación de contraseña
    }

}
