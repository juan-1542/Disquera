package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Persistencia.Repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Registrar nuevo usuario
    public void registrarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardar
        String contrasenaEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(contrasenaEncriptada);
        usuarioRepositorio.save(usuario);
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        return usuarioRepositorio.findAll();
    }

    // Borrar usuario
    public boolean borrarUsuarioPorId(String nombreUsuario) {
        try {
            usuarioRepositorio.deleteById(nombreUsuario);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Buscar usuario por nombre de usuario (username)
    public Usuario buscarPorUsuario(String nombreUsuario) {
        return usuarioRepositorio.findByUsuario(nombreUsuario).orElse(null);
    }

    // Autenticación: buscar usuario y verificar contraseña
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = buscarPorUsuario(nombreUsuario);
        if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            return usuario;
        }
        return null;
    }
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepositorio.findByCorreo(correo).orElse(null);
    }

    public void actualizarUsuario(Usuario usuarioActualizado) {
        // Aquí se actualiza el usuario en la base de datos
        usuarioRepositorio.save(usuarioActualizado);
    }
    public Usuario buscarPorCodigo(String codigo) {
        return usuarioRepositorio.findByCodigoRecuperacion(codigo).orElse(null);
    }
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
