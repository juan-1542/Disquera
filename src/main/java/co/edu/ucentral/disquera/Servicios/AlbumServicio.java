package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Repositorio.AlbumRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumServicio {

    private final AlbumRepositorio albumRepositorio;

    public AlbumServicio(AlbumRepositorio albumRepositorio) {
        this.albumRepositorio = albumRepositorio;
    }

    public List<Album> listarTodos() {
        return albumRepositorio.findAll();
    }

    public Optional<Album> buscarPorId(Long id) {
        return albumRepositorio.findById(id);
    }

    public Album guardar(Album album) {
        return albumRepositorio.save(album);
    }

    public void eliminar(Long id) {
        albumRepositorio.deleteById(id);
    }

    public List<Album> buscarPorUsuario(String usuario) {
        return albumRepositorio.findByUsuarioUsuario(usuario);
    }
}