package co.edu.ucentral.disquera.Servicios;
import co.edu.ucentral.disquera.Persistencia.Entidades.Artista;
import co.edu.ucentral.disquera.Persistencia.Repositorio.ArtistaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistaServicio {

    private final ArtistaRepositorio repositorio;

    public ArtistaServicio(ArtistaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Artista> listarTodos() {
        return repositorio.findAll();
    }

    public Artista guardar(Artista artista) {
        return repositorio.save(artista);
    }

    public Optional<Artista> obtenerPorId(Long id) {
        return repositorio.findById(id);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }
}
