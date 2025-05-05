package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Contrato;
import co.edu.ucentral.disquera.Servicios.ContratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/contratos")
public class ContratoControlador {

    private final ContratoService contratoService;

    @Autowired
    public ContratoControlador(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    // Página principal de gestión de contratos para artistas
    @GetMapping("/gestionar")
    public String vistaGestionContratos(Model model) {
        List<Contrato> contratos = contratoService.listarContratos();
        model.addAttribute("contratos", contratos);

        long activos = contratos.stream().filter(c -> "Activo".equals(c.getEstado())).count();
        long pendientes = contratos.stream().filter(c -> "Pendiente".equals(c.getEstado())).count();
        long vencidos = contratos.stream().filter(c -> "Vencido".equals(c.getEstado())).count();

        model.addAttribute("activos", activos);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("vencidos", vencidos);

        return "gestionarContratos";
    }

    // Mostrar formulario para generar contrato
    @GetMapping("/nuevo")
    public String mostrarFormularioContrato(Model model) {
        model.addAttribute("contrato", new Contrato());
        return "generarContrato";
    }

    // Procesar envío del formulario y mostrar mensaje de confirmación
    @PostMapping("/guardar")
    public String guardarContrato(@ModelAttribute("contrato") Contrato contrato, Model model) {
        contratoService.guardarContrato(contrato);
        model.addAttribute("mensaje", "¡Contrato generado exitosamente! La solicitud será revisada por el administrador.");
        return "confirmacionContrato";
    }

    // Mostrar formulario para editar contrato
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Contrato contrato = contratoService.listarContratos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (contrato != null) {
            model.addAttribute("contrato", contrato);
            return "editarContrato";
        }
        return "redirect:/contratos/gestionar";
    }

    // Procesar edición del contrato
    @PostMapping("/actualizar")
    public String actualizarContrato(@ModelAttribute("contrato") Contrato contrato, Model model) {
        contratoService.guardarContrato(contrato);
        model.addAttribute("mensaje", "¡Contrato actualizado exitosamente!");
        return "redirect:/contratos/gestionar";
    }

    // Eliminar contrato
    @GetMapping("/eliminar/{id}")
    public String eliminarContrato(@PathVariable Long id) {
        contratoService.eliminarContrato(id);
        return "redirect:/contratos/gestionar";
    }

    // Página de administración para revisar contratos pendientes
    @GetMapping("/admin/contratos")
    public String vistaAdminContratos(Model model) {
        List<Contrato> contratosPendientes = contratoService.listarContratosPendientes();
        model.addAttribute("contratosPendientes", contratosPendientes);
        return "adminContratos";
    }

    // Mostrar detalles del contrato
    @GetMapping("/admin/detalle/{id}")
    public String mostrarDetalleContrato(@PathVariable Long id, Model model) {
        Contrato contrato = contratoService.listarContratos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (contrato != null) {
            model.addAttribute("contrato", contrato);
            return "detalleContrato";
        }
        return "redirect:/contratos/admin/contratos";
    }

    // Aceptar contrato
    @GetMapping("/admin/aceptar/{id}")
    public String aceptarContrato(@PathVariable Long id) {
        contratoService.actualizarEstado(id, "Activo");
        return "redirect:/contratos/admin/contratos";
    }

    // Rechazar contrato
    @GetMapping("/admin/rechazar/{id}")
    public String rechazarContrato(@PathVariable Long id) {
        contratoService.actualizarEstado(id, "Rechazado");
        return "redirect:/contratos/admin/contratos";
    }
}