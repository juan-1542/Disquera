package co.edu.ucentral.disquera.Servicios;


import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Repositorio.CancionRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class CancionServicio {

    private static final Logger LOGGER = Logger.getLogger(CancionServicio.class.getName());

    private final CancionRepositorio cancionRepositorio;

    public CancionServicio(CancionRepositorio cancionRepositorio) {
        this.cancionRepositorio = cancionRepositorio;
    }

    public List<Cancion> listarTodas() {
        LOGGER.info("Listando todas las canciones");
        return cancionRepositorio.findAll();
    }

    public Optional<Cancion> buscarPorId(Long id) {
        LOGGER.info("Buscando canción por ID: " + id);
        return cancionRepositorio.findById(id);
    }

    public Cancion guardar(Cancion cancion) {
        LOGGER.info("Guardando canción: " + cancion.getTitulo());
        return cancionRepositorio.save(cancion);
    }

    public List<Cancion> buscarPorEstado(Cancion.Estado estado) {
        LOGGER.info("Buscando canciones por estado: " + estado);
        return cancionRepositorio.findByEstado(estado);
    }

    public List<Cancion> buscarPorIds(List<Long> ids) {
        LOGGER.info("Buscando canciones por IDs: " + ids);
        return cancionRepositorio.findAllById(ids);
    }

    public List<Cancion> listarSencillos() {
        LOGGER.info("Listando canciones marcadas como sencillos");
        return cancionRepositorio.findByEsSencilloTrue();
    }

    public List<Cancion> buscarPorUsuario(String usuario) {
        LOGGER.info("Buscando canciones por usuario: " + usuario);
        return cancionRepositorio.findByUsuarioUsuario(usuario);
    }
}