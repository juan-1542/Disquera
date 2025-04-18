package co.edu.ucentral.disquera.Persistencia.Repositorio;

import co.edu.ucentral.disquera.Persistencia.Entidades.Artista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistaRepositorio extends JpaRepository<Artista, Long> {
}
