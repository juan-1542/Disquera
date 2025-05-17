package co.edu.ucentral.disquera.Persistencia.Repositorio;

import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancionRepositorio extends JpaRepository<Cancion, Long> {
    List<Cancion> findByEsSencilloTrue();
}