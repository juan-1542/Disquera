package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Repositorio.AlbumRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AlbumServicio {

    private static final Logger LOGGER = Logger.getLogger(AlbumServicio.class.getName());

    private final AlbumRepositorio albumRepositorio;

    public AlbumServicio(AlbumRepositorio albumRepositorio) {
        this.albumRepositorio = albumRepositorio;
    }

    public List<Album> listarTodos() {
        LOGGER.info("Listando todos los álbumes");
        return albumRepositorio.findAll();
    }

    public Optional<Album> buscarPorId(Long id) {
        LOGGER.info("Buscando álbum por ID: " + id);
        return albumRepositorio.findById(id);
    }

    public Album guardar(Album album) {
        LOGGER.info("Guardando álbum: " + album.getNombre());
        return albumRepositorio.save(album);
    }

    public void eliminar(Long id) {
        LOGGER.info("Eliminando álbum ID: " + id);
        albumRepositorio.deleteById(id);
    }

    public List<Album> buscarPorEstado(Album.Estado estado) {
        LOGGER.info("Buscando álbumes por estado: " + estado);
        return albumRepositorio.findByEstado(estado);
    }

    public List<Album> buscarPorUsuario(String usuario) {
        LOGGER.info("Buscando álbumes por usuario: " + usuario);
        return albumRepositorio.findByUsuarioUsuario(usuario);
    }
}