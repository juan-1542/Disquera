package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Artista;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.ArtistaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/albumes")
public class AlbumControlador {

    @Autowired
    private AlbumServicio albumServicio;

    @Autowired
    private ArtistaServicio artistaServicio;

    @GetMapping
    public String listarAlbumes(Model model) {
        model.addAttribute("listaAlbumes", albumServicio.listarTodos());
        return "lista_albumes";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("album", new Album());
        List<Artista> artistas = artistaServicio.listarTodos();
        model.addAttribute("listaArtistas", artistas);
        return "formulario_album";
    }

    @PostMapping("/guardar")
    public String guardarAlbum(@ModelAttribute("album") Album album) {
        albumServicio.guardar(album);
        return "redirect:/admin/albumes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Album album = albumServicio.buscarPorId(id);
        model.addAttribute("album", album);
        model.addAttribute("listaArtistas", artistaServicio.listarTodos());
        return "formulario_album";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAlbum(@PathVariable Long id) {
        albumServicio.eliminar(id);
        return "redirect:/admin/albumes";
    }
}
