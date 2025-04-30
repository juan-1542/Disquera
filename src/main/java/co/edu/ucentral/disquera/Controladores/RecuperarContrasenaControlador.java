package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import co.edu.ucentral.disquera.Servicios.EmailServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Random;

@Controller
public class RecuperarContrasenaControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private EmailServicio emailServicio;

    // Paso 1: Recuperar contraseña - Enviar código al correo
    @PostMapping("/recuperarcontraseña")
    public String recuperarContrasena(@RequestParam("correo") String correo, Model model) {
        // Buscar el usuario por correo
        Usuario usuario = usuarioServicio.buscarPorCorreo(correo);

        // Si el usuario no existe, mostrar un error
        if (usuario == null) {
            model.addAttribute("error", "Correo electrónico no encontrado.");
            return "recuperarcontrasena"; // Vista de error
        }

        // Generar un código de recuperación aleatorio
        String codigoRecuperacion = generarCodigoRecuperacion();
        usuario.setCodigoRecuperacion(codigoRecuperacion);
        usuario.setExpiracionCodigo(LocalDateTime.now().plusMinutes(15)); // El código expira en 15 minutos

        // Actualizar el usuario con el código de recuperación y la fecha de expiración
        usuarioServicio.actualizarUsuario(usuario);

        // Enviar el código de recuperación por correo electrónico
        emailServicio.enviarCorreo(correo, "Código de recuperación de contraseña",
                "Tu código de recuperación es: " + codigoRecuperacion);

        // Redirigir a la vista donde el usuario ingresa el código
        return "recuperarcodigo"; // Vista para que el usuario ingrese el código
    }

    // Método para generar un código de 6 dígitos
    private String generarCodigoRecuperacion() {
        Random rand = new Random();
        int codigo = 100000 + rand.nextInt(900000); // Genera un código de 6 dígitos
        return String.valueOf(codigo);
    }

    // Paso 2: Verificar el código ingresado por el usuario
    @PostMapping("/verificarcodigo")
    public String verificarCodigo(@RequestParam("codigo") String codigo, Model model) {
        // Buscar el usuario con el código de recuperación
        Usuario usuario = usuarioServicio.buscarPorCodigo(codigo);

        // Verificar si el usuario existe y si el código no ha expirado
        if (usuario == null || usuario.getExpiracionCodigo().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Código inválido o expirado.");
            return "recuperarcodigo"; // Redirige a la vista de ingresar código
        }

        // Si el código es válido, redirige a la vista para restablecer la contraseña
        model.addAttribute("usuario", usuario);
        return "restablecercontrasena"; // Vista para restablecer la contraseña
    }

    // Paso 3: Restablecer la contraseña
    @PostMapping("/restablecercontrasena")
    public String restablecerContrasena(@RequestParam("usuario") String nombreUsuario,
                                        @RequestParam("nuevaContrasena") String nuevaContrasena, Model model) {
        // Buscar el usuario por nombre de usuario
        Usuario usuarioExistente = usuarioServicio.buscarPorUsuario(nombreUsuario);

        if (usuarioExistente != null) {
            // Encriptar la nueva contraseña
            String contrasenaEncriptada = usuarioServicio.getPasswordEncoder().encode(nuevaContrasena);
            usuarioExistente.setContrasena(contrasenaEncriptada);

            // Actualizar la contraseña en la base de datos
            usuarioServicio.actualizarUsuario(usuarioExistente);

            // Mostrar mensaje de éxito
            model.addAttribute("mensaje", "Contraseña restablecida con éxito.");
            return "login"; // Redirige al login o donde corresponda
        } else {
            model.addAttribute("error", "Usuario no encontrado.");
            return "recuperarcontrasena"; // Redirige a la vista de recuperación
        }
    }
}
