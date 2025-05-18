package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Persistencia.Repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UsuarioServicio {

    private static final Logger LOGGER = Logger.getLogger(UsuarioServicio.class.getName());

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Registrar nuevo usuario
    public void registrarUsuario(Usuario usuario) {
        LOGGER.info("Registrando usuario: " + usuario.getUsuario());
        String contrasenaEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(contrasenaEncriptada);
        usuarioRepositorio.save(usuario);
        LOGGER.info("Usuario registrado: " + usuario.getUsuario());
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        LOGGER.info("Obteniendo todos los usuarios");
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        LOGGER.info("Usuarios totales encontrados: " + usuarios.size());
        return usuarios;
    }

    // Obtener usuarios con rol ARTISTA
    public List<Usuario> listarArtistas() {
        LOGGER.info("Listando usuarios con rol ARTISTA");
        List<Usuario> artistas = usuarioRepositorio.findByRol(Usuario.Rol.ARTISTA);
        LOGGER.info("Artistas encontrados: " + artistas.size());
        for (Usuario artista : artistas) {
            LOGGER.info("Artista: Usuario=" + artista.getUsuario() + ", Nombre=" + artista.getNombre() + ", Rol=" + artista.getRol());
        }
        return artistas;
    }

    // Borrar usuario
    public boolean borrarUsuarioPorId(String usuario) {
        try {
            LOGGER.info("Borrando usuario con usuario: " + usuario);
            usuarioRepositorio.deleteById(usuario);
            LOGGER.info("Usuario borrado: " + usuario);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error al borrar usuario: " + usuario + ": " + e.getMessage());
            return false;
        }
    }

    // Buscar usuario por nombre de usuario
    public Usuario buscarPorUsuario(String nombreUsuario) {
        LOGGER.info("Buscando usuario por nombre: " + nombreUsuario);
        Usuario usuario = usuarioRepositorio.findByUsuario(nombreUsuario).orElse(null);
        LOGGER.info("Usuario encontrado: " + (usuario != null ? usuario.getNombre() : "null"));
        return usuario;
    }

    // Autenticación
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        LOGGER.info("Autenticando usuario: " + nombreUsuario);
        Usuario usuario = buscarPorUsuario(nombreUsuario);
        if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            LOGGER.info("Autenticación exitosa para: " + nombreUsuario);
            return usuario;
        }
        LOGGER.warning("Autenticación fallida para: " + nombreUsuario);
        return null;
    }

    public Usuario buscarPorCorreo(String correo) {
        LOGGER.info("Buscando usuario por correo: " + correo);
        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
        LOGGER.info("Usuario encontrado por correo: " + (usuario != null ? usuario.getNombre() : "null"));
        return usuario;
    }

    public void actualizarUsuario(Usuario usuarioActualizado) {
        LOGGER.info("Actualizando usuario: " + usuarioActualizado.getUsuario());
        usuarioRepositorio.save(usuarioActualizado);
        LOGGER.info("Usuario actualizado: " + usuarioActualizado.getUsuario());
    }

    public Usuario buscarPorCodigo(String codigo) {
        LOGGER.info("Buscando usuario por código de recuperación: " + codigo);
        Usuario usuario = usuarioRepositorio.findByCodigoRecuperacion(codigo).orElse(null);
        LOGGER.info("Usuario encontrado por código: " + (usuario != null ? usuario.getNombre() : "null"));
        return usuario;
    }

    public List<Usuario> buscarPorRol(Usuario.Rol rol) {
        LOGGER.info("Buscando usuarios con rol: " + rol);
        List<Usuario> usuarios = usuarioRepositorio.findByRol(rol);
        LOGGER.info("Usuarios encontrados con rol " + rol + ": " + usuarios.size());
        for (Usuario usuario : usuarios) {
            LOGGER.info("Usuario: Usuario=" + usuario.getUsuario() + ", Nombre=" + usuario.getNombre() + ", Rol=" + usuario.getRol());
        }
        return usuarios;
    }

    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}