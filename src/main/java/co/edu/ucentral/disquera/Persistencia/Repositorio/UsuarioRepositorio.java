package co.edu.ucentral.disquera.Persistencia.Repositorio;

import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByUsuario(String usuario);
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByCodigoRecuperacion(String codigoRecuperacion);
    List<Usuario> findByRol(Usuario.Rol rol);
}
