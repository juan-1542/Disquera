package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Contrato;
import co.edu.ucentral.disquera.Servicios.ContratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contratos")
public class ContratoControlador {

    private final ContratoService contratoService;

    @Autowired
    public ContratoControlador(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    // Página principal de gestión de contratos (vista previa con botón "Nuevo contrato")
    @GetMapping("/gestionar")
    public String vistaGestionContratos() {
        return "gestionarContratos";
    }

    // Mostrar formulario para generar contrato
    @GetMapping("/nuevo")
    public String mostrarFormularioContrato(Model model) {
        model.addAttribute("contrato", new Contrato());
        return "generarContrato";  // Vista con formulario
    }

    // Procesar envío del formulario y mostrar mensaje de confirmación
    @PostMapping("/guardar")
    public String guardarContrato(@ModelAttribute("contrato") Contrato contrato, Model model) {
        contratoService.guardarContrato(contrato);
        model.addAttribute("mensaje", "¡Contrato generado exitosamente! La solicitud será revisada por el administrador.");
        return "confirmacionContrato";
    }
}
