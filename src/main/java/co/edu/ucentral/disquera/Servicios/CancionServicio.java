package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Repositorio.CancionRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CancionServicio {

    private final CancionRepositorio cancionRepositorio;

    public CancionServicio(CancionRepositorio cancionRepositorio) {
        this.cancionRepositorio = cancionRepositorio;
    }

    public List<Cancion> listarTodas() {
        return cancionRepositorio.findAll();
    }

    public Optional<Cancion> buscarPorId(Long id) {
        return cancionRepositorio.findById(id);
    }

    public Cancion guardar(Cancion cancion) {
        return cancionRepositorio.save(cancion);
    }

    public List<Cancion> listarSencillos() {
        return cancionRepositorio.findByEsSencilloTrue();
    }
}