package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Repositorio.AlbumRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumServicio {

    private final AlbumRepositorio albumRepositorio;

    public AlbumServicio(AlbumRepositorio albumRepositorio) {
        this.albumRepositorio = albumRepositorio;
    }

    public List<Album> listarTodos() {
        return albumRepositorio.findAll();
    }

    public Album buscarPorId(Long id) {
        return albumRepositorio.findById(id).orElse(null);
    }

    public void guardar(Album album) {
        albumRepositorio.save(album);
    }

    public void eliminar(Long id) {
        albumRepositorio.deleteById(id);
    }

    public List<Album> listarPorArtista(Long artistaId) {
        return albumRepositorio.findByArtistaId(artistaId);
    }
}