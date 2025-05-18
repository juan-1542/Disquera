package co.edu.ucentral.disquera.Persistencia.Repositorio;

import co.edu.ucentral.disquera.Persistencia.Entidades.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepositorio extends JpaRepository<Venta, Long> {
    List<Venta> findByArtista_UsuarioAndEstadoPago(String usuario, String estadoPago);
    List<Venta> findByEstadoPago(String estadoPago);
    List<Venta> findByArtista_UsuarioAndEstadoPagoIn(String usuario, List<String> estados);

}