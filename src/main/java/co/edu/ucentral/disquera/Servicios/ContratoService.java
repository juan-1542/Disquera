package co.edu.ucentral.disquera.Servicios;

import co.edu.ucentral.disquera.Persistencia.Entidades.Contrato;
import co.edu.ucentral.disquera.Persistencia.Repositorio.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepositorio;

    @Autowired
    public ContratoService(ContratoRepository contratoRepositorio) {
        this.contratoRepositorio = contratoRepositorio;
    }

    // Guardar contrato
    public void guardarContrato(Contrato contrato) {
        contratoRepositorio.save(contrato);  // Guardar el contrato en la base de datos
    }
}
