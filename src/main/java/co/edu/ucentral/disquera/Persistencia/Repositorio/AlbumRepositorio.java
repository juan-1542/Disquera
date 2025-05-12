package co.edu.ucentral.disquera.Persistencia.Repositorio;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepositorio extends JpaRepository<Album, Long> {
    List<Album> findByArtistaId(Long artistaId);
}