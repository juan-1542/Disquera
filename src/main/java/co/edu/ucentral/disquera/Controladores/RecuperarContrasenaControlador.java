package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import co.edu.ucentral.disquera.Servicios.EmailServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

@Controller
public class RecuperarContrasenaControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private EmailServicio emailServicio;

    @GetMapping("/recuperarcontrasena")
    public String mostrarFormularioRecuperacion() {
        return "recuperarcontrasena";
    }

    // ✅ Nombre corregido: sin ñ
    @PostMapping("/recuperarcontrasena")
    public String recuperarContrasena(@RequestParam("correo") String correo, Model model) {
        Usuario usuario = usuarioServicio.buscarPorCorreo(correo);

        if (usuario == null) {
            model.addAttribute("error", "Correo electrónico no encontrado.");
            return "recuperarcontrasena";
        }

        String codigoRecuperacion = generarCodigoRecuperacion();
        usuario.setCodigoRecuperacion(codigoRecuperacion);
        usuario.setExpiracionCodigo(LocalDateTime.now().plusMinutes(15));

        usuarioServicio.actualizarUsuario(usuario);

        emailServicio.enviarCorreo(correo, "Código de recuperación de contraseña",
                "Tu código de recuperación es: " + codigoRecuperacion);

        model.addAttribute("correo", correo); // Por si necesitas reenviarlo
        return "recuperarcodigo";
    }

    private String generarCodigoRecuperacion() {
        Random rand = new Random();
        int codigo = 100000 + rand.nextInt(900000);
        return String.valueOf(codigo);
    }

    @PostMapping("/verificarcodigo")
    public String verificarCodigo(@RequestParam("codigo") String codigo, Model model) {
        Usuario usuario = usuarioServicio.buscarPorCodigo(codigo);

        if (usuario == null || usuario.getExpiracionCodigo().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Código inválido o expirado.");
            return "recuperarcodigo";
        }

        // Aquí enviamos solo el nombre de usuario para el input oculto
        model.addAttribute("usuario", usuario.getUsuario());
        return "restablecercontrasena";
    }

    @PostMapping("/restablecercontrasena")
    public String restablecerContrasena(@RequestParam("usuario") String nombreUsuario,
                                        @RequestParam("nuevaContrasena") String nuevaContrasena,
                                        Model model) {
        Usuario usuarioExistente = usuarioServicio.buscarPorUsuario(nombreUsuario);

        if (usuarioExistente != null) {
            String contrasenaEncriptada = usuarioServicio.getPasswordEncoder().encode(nuevaContrasena);
            usuarioExistente.setContrasena(contrasenaEncriptada);
            usuarioServicio.actualizarUsuario(usuarioExistente);

            model.addAttribute("mensaje", "Contraseña restablecida con éxito.");
            model.addAttribute("contrasenaRestablecida", true);

            return "restablecercontrasena";
        } else {
            model.addAttribute("error", "Usuario no encontrado.");
            return "restablecercontrasena";
        }
    }
}
