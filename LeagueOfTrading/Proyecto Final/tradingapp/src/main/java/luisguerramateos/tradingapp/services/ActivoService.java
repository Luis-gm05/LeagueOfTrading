package luisguerramateos.tradingapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import luisguerramateos.tradingapp.entities.Activo;
import luisguerramateos.tradingapp.repositories.ActivoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ActivoService {
    @Autowired
    private ActivoRepository activoRepository;

    public List<Activo> getAll(){
        return activoRepository.findAll();
    }

    public Activo findByNombre(String coinName) {
        Optional<Activo> activo = activoRepository.findByNombre(coinName);
        if (activo.isPresent()) {
            return activo.get();
        } else {
            throw new EntityNotFoundException("Activo no encontrado: " + coinName);
        }
    }
}
