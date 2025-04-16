package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String mostrarPaginaPrincipal() {
        return "index"; // templates/index.html
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("elusuario", new Usuario());
        return "registro"; // templates/registro.html
    }

    @PostMapping("/registro")
    public String guardarUsuario(@Valid @ModelAttribute("elusuario") Usuario usuario,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "registro";
        }
        usuarioServicio.registrarUsuario(usuario);
        return "redirect:/iniciosesion";
    }

    @GetMapping("/iniciosesion")
    public String mostrarFormularioLogin(Model model) {
        model.addAttribute("credenciales", new Usuario());
        return "iniciosesion"; // templates/iniciosesion.html
    }
    @PostMapping("/iniciosesion")
    public String procesarLogin(@ModelAttribute("credenciales") Usuario usuario, Model model) {
        Usuario usuarioEncontrado = usuarioServicio.autenticar(
                usuario.getUsuario(), usuario.getContrasena());

        if (usuarioEncontrado != null) {
            if (usuarioEncontrado.getRol() == Usuario.Rol.ADMIN) {
                return "admin"; // templates/admin.html
            } else if (usuarioEncontrado.getRol() == Usuario.Rol.ARTISTA) {
                return "artista"; // templates/artista.html
            } else {
                model.addAttribute("error", "Rol no reconocido");
                return "iniciosesion";
            }
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "iniciosesion";
        }
    }
}
