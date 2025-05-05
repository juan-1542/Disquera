package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Contrato;
import co.edu.ucentral.disquera.Persistencia.Repositorio.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/contratos")
public class AdminContratoController {

    @Autowired
    private ContratoRepository contratoRepository;

    @GetMapping
    public String listarContratos(Model model) {
        List<Contrato> contratos = contratoRepository.findAll();
        model.addAttribute("contratos", contratos);
        return "gestionarContratosAdmin";
    }

    @PostMapping("/aprobar")
    public String aprobarContrato(@RequestParam Long id) {
        Contrato contrato = contratoRepository.findById(id).orElse(null);
        if (contrato != null && "Pendiente".equals(contrato.getEstado())) {
            contrato.setEstado("Activo");
            contrato.setFechaInicio(LocalDate.now());
            contrato.setFechaVencimiento(LocalDate.now().plusMonths(contrato.getDuracionMeses()));
            contratoRepository.save(contrato);
        }
        return "redirect:/admin/contratos";
    }

    @PostMapping("/rechazar")
    public String rechazarContrato(@RequestParam Long id) {
        Contrato contrato = contratoRepository.findById(id).orElse(null);
        if (contrato != null && "Pendiente".equals(contrato.getEstado())) {
            contrato.setEstado("Rechazado");
            contratoRepository.save(contrato);
        }
        return "redirect:/admin/contratos";
    }
}
