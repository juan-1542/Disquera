// AlbumServicio.java
package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Repositorio.AlbumRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumServicio {

    @Autowired
    private AlbumRepositorio albumRepositorio;

    public List<Album> listarTodos() {
        return albumRepositorio.findAll();
    }

    public Album guardar(Album album) {
        return albumRepositorio.save(album);
    }

    public Album buscarPorId(Long id) {
        return albumRepositorio.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        albumRepositorio.deleteById(id);
    }
}
