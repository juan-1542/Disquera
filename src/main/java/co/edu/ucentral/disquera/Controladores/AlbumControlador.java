package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;

import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.ArtistaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/albumes")
public class AlbumControlador {

    private final AlbumServicio albumServicio;
    private final ArtistaServicio artistaServicio;

    public AlbumControlador(AlbumServicio albumServicio, ArtistaServicio artistaServicio) {
        this.albumServicio = albumServicio;
        this.artistaServicio = artistaServicio;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("albumes", albumServicio.listarTodos());
        return "/albumes"; // Tu vista de lista de álbumes
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("album", new Album());
        model.addAttribute("artistas", artistaServicio.listarTodos());
        return "/formulario_album";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Album album) {
        albumServicio.guardar(album);
        return "redirect:/albumes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Album album = albumServicio.buscarPorId(id);
        model.addAttribute("album", album);
        model.addAttribute("artistas", artistaServicio.listarTodos());
        return "album/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        albumServicio.eliminar(id);
        return "redirect:/albumes";
    }
}