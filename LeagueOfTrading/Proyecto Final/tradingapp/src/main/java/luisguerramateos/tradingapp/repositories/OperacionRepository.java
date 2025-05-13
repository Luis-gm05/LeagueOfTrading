package luisguerramateos.tradingapp.repositories;

import luisguerramateos.tradingapp.entities.Operacion;
import luisguerramateos.tradingapp.entities.Usuario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long> {

    List<Operacion> findByUsuario(Usuario usuario);
    
}
