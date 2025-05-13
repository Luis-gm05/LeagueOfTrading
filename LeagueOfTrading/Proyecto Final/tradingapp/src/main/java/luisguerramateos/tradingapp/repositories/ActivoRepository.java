package luisguerramateos.tradingapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import luisguerramateos.tradingapp.entities.Activo;

public interface ActivoRepository extends JpaRepository<Activo, Long>{
    List<Activo> findAll();

    @Query("SELECT a FROM Activo a WHERE LOWER(a.nombre) = LOWER(:nombre)")
    Optional<Activo> findByNombre(@Param("nombre") String nombre);
}     
