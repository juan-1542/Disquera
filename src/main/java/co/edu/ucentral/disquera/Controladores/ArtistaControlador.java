package co.edu.ucentral.disquera.Controladores;



import co.edu.ucentral.disquera.Persistencia.Entidades.Artista;
import co.edu.ucentral.disquera.Servicios.ArtistaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/artistas")
public class ArtistaControlador {

    private final ArtistaServicio servicio;

    public ArtistaControlador(ArtistaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String listarArtistas(Model modelo) {
        modelo.addAttribute("listaArtistas", servicio.listarTodos());
        return "lista_artistas";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model modelo) {
        modelo.addAttribute("artista", new Artista());
        return "formulario_artista";
    }

    @PostMapping("/guardar")
    public String guardarArtista(@ModelAttribute("artista") Artista artista) {
        servicio.guardar(artista);
        return "redirect:/admin/artistas";
    }

    @GetMapping("/editar/{id}")
    public String editarArtista(@PathVariable Long id, Model modelo) {
        servicio.obtenerPorId(id).ifPresent(artista -> modelo.addAttribute("artista", artista));
        return "formulario_artista";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarArtista(@PathVariable Long id) {
        servicio.eliminar(id);
        return "redirect:/admin/artistas";
    }
}
