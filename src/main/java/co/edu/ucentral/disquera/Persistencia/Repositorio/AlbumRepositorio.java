package co.edu.ucentral.disquera.Persistencia.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.ucentral.disquera.Persistencia.Entidades.Album;

public interface AlbumRepositorio extends JpaRepository<Album, Long> {
}
