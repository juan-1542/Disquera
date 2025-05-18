package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Contrato;
import co.edu.ucentral.disquera.Persistencia.Repositorio.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ContratoService {

    private static final Logger LOGGER = Logger.getLogger(ContratoService.class.getName());

    private final ContratoRepository contratoRepository;

    @Autowired
    public ContratoService(ContratoRepository contratoRepository) {
        this.contratoRepository = contratoRepository;
    }

    // Guardar contrato
    public void guardarContrato(Contrato contrato) {
        LOGGER.info("Guardando contrato para artista: " +
                (contrato.getArtista() != null ? contrato.getArtista().getUsuario() : "sin artista"));
        contrato.setFechaCreacion(LocalDate.now());
        if (contrato.getFechaInicio() == null) {
            contrato.setFechaInicio(LocalDate.now());
        }
        if (contrato.getDuracionMeses() != null) {
            contrato.setFechaVencimiento(contrato.getFechaInicio().plusMonths(contrato.getDuracionMeses()));
        }
        contrato.setEstado("Pendiente"); // Estado inicial
        contratoRepository.save(contrato);
    }

    // Listar todos los contratos
    public List<Contrato> listarContratos() {
        LOGGER.info("Listando todos los contratos");
        List<Contrato> contratos = contratoRepository.findAll();
        contratos.forEach(contrato -> contrato.setEstado(contrato.calcularEstado()));
        return contratos;
    }

    // Listar contratos pendientes
    public List<Contrato> listarContratosPendientes() {
        LOGGER.info("Listando contratos pendientes");
        List<Contrato> contratos = contratoRepository.findAll();
        return contratos.stream()
                .filter(c -> "Pendiente".equals(c.getEstado()))
                .peek(c -> c.setEstado(c.calcularEstado()))
                .toList();
    }

    // Actualizar estado del contrato
    public void actualizarEstado(Long id, String nuevoEstado) {
        LOGGER.info("Actualizando estado del contrato ID: " + id + " a " + nuevoEstado);
        Contrato contrato = contratoRepository.findById(id).orElse(null);
        if (contrato != null) {
            contrato.setEstado(nuevoEstado);
            if ("Activo".equals(nuevoEstado) && contrato.getFechaInicio() == null) {
                contrato.setFechaInicio(LocalDate.now());
                contrato.setFechaVencimiento(contrato.getFechaInicio().plusMonths(contrato.getDuracionMeses()));
            }
            contratoRepository.save(contrato);
        }
    }

    // Rechazar contrato con motivo
    public void rechazarContrato(Long id, String motivo) {
        LOGGER.info("Rechazando contrato ID: " + id + " con motivo: " + motivo);
        Optional<Contrato> contratoOpt = contratoRepository.findById(id);
        if (contratoOpt.isPresent()) {
            Contrato contrato = contratoOpt.get();
            contrato.setEstado("Rechazado");
            contrato.setMotivoRechazo(motivo);
            contratoRepository.save(contrato);
        }
    }

    // Eliminar contrato
    public void eliminarContrato(Long id) {
        LOGGER.info("Eliminando contrato ID: " + id);
        contratoRepository.deleteById(id);
    }

    // Buscar contrato activo por usuario
    public Contrato buscarContratoActivo(String usuUsuario) {
        LOGGER.info("Buscando contrato activo para usuario: " + usuUsuario);
        List<Contrato> contratos = new ArrayList<>(contratoRepository.findByArtistaUsuario(usuUsuario));
        LOGGER.info("Contratos encontrados: " + contratos.size());
        return contratos.stream()
                .filter(c -> {
                    String estado = c.calcularEstado();
                    LOGGER.info("Contrato ID: " + c.getId() + ", Estado: " + estado);
                    return "Activo".equals(estado);
                })
                .findFirst()
                .orElse(null);
    }
}