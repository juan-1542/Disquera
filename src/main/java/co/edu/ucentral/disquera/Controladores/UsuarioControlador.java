package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("")
    public String mostrarInicio(Model model) {
        model.addAttribute("credenciales", new Usuario());
        return "iniciosesion"; // Muestra el formulario de inicio
    }

    @PostMapping("/iniciosesion")
    public String procesarLogin(@ModelAttribute("credenciales") Usuario usuario, Model model) {
        Usuario usuarioEncontrado = usuarioServicio.autenticar(
                usuario.getUsuario(), usuario.getContrasena());

        if (usuarioEncontrado != null) {
            if (usuarioEncontrado.getRol() == Usuario.Rol.ADMIN) {
                return "redirect:/admin"; // Ahora redirige a /admin
            } else if (usuarioEncontrado.getRol() == Usuario.Rol.ARTISTA) {
                return "redirect:/artista";
            } else {
                model.addAttribute("error", "Rol no reconocido");
                return "iniciosesion";
            }
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "iniciosesion";
        }
    }

    @GetMapping("/admin")
    public String mostrarPanelAdmin() {
        return "admin"; // Carga el template admin.html
    }

    @GetMapping("/artista")
    public String mostrarPanelArtista() {
        return "artista"; // Carga el template artista.html
    }
}
