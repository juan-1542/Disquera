package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Contrato;
import co.edu.ucentral.disquera.Persistencia.Repositorio.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepositorio;

    @Autowired
    public ContratoService(ContratoRepository contratoRepositorio) {
        this.contratoRepositorio = contratoRepositorio;
    }

    // Guardar contrato
    public void guardarContrato(Contrato contrato) {
        contrato.setFechaCreacion(LocalDate.now());
        if (contrato.getFechaInicio() == null) {
            contrato.setFechaInicio(LocalDate.now());
        }
        if (contrato.getDuracionMeses() != null) {
            contrato.setFechaVencimiento(contrato.getFechaInicio().plusMonths(contrato.getDuracionMeses()));
        }
        contrato.setEstado("Pendiente"); // Estado inicial
        contratoRepositorio.save(contrato);
    }

    // Listar todos los contratos
    public List<Contrato> listarContratos() {
        List<Contrato> contratos = contratoRepositorio.findAll();
        contratos.forEach(contrato -> contrato.setEstado(contrato.calcularEstado()));
        return contratos;
    }

    // Listar contratos pendientes
    public List<Contrato> listarContratosPendientes() {
        List<Contrato> contratos = contratoRepositorio.findAll();
        return contratos.stream()
                .filter(c -> "Pendiente".equals(c.getEstado()))
                .peek(c -> c.setEstado(c.calcularEstado()))
                .toList();
    }

    // Actualizar estado del contrato
    public void actualizarEstado(Long id, String nuevoEstado) {
        Contrato contrato = contratoRepositorio.findById(id).orElse(null);
        if (contrato != null) {
            contrato.setEstado(nuevoEstado);
            if ("Activo".equals(nuevoEstado) && contrato.getFechaInicio() == null) {
                contrato.setFechaInicio(LocalDate.now());
                contrato.setFechaVencimiento(contrato.getFechaInicio().plusMonths(contrato.getDuracionMeses()));
            }
            contratoRepositorio.save(contrato);
        }
    }

    // Rechazar contrato con motivo
    public void rechazarContrato(Long id, String motivo) {
        Optional<Contrato> contratoOpt = contratoRepositorio.findById(id);
        if (contratoOpt.isPresent()) {
            Contrato contrato = contratoOpt.get();
            contrato.setEstado("Rechazado");
            contrato.setMotivoRechazo(motivo);
            contratoRepositorio.save(contrato);
        }
    }

    // Eliminar contrato
    public void eliminarContrato(Long id) {
        contratoRepositorio.deleteById(id);
    }
}
