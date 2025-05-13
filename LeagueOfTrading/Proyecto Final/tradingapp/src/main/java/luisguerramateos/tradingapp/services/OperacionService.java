package luisguerramateos.tradingapp.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.constant.Currency;
import com.litesoftwares.coingecko.domain.Coins.MarketChart;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import luisguerramateos.tradingapp.entities.Activo;
import luisguerramateos.tradingapp.entities.Operacion;
import luisguerramateos.tradingapp.entities.Operacion.TipoOperacion;
import luisguerramateos.tradingapp.entities.Usuario;
import luisguerramateos.tradingapp.repositories.ActivoRepository;
import luisguerramateos.tradingapp.repositories.OperacionRepository;
import luisguerramateos.tradingapp.repositories.UsuarioRepository;

@Service
public class OperacionService {

    @Autowired
    private final OperacionRepository operacionRepository;
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final ActivoRepository activoRepository;
    private final CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

    public OperacionService(OperacionRepository operacionRepository,
                            UsuarioRepository usuarioRepository,
                            ActivoRepository activoRepository) {
        this.operacionRepository = operacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.activoRepository = activoRepository;
    }

    @Transactional
public void createOperacion(Long usuarioId, Long activoId, String tipoOperacion, BigDecimal cantidad, BigDecimal precio) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    Activo activo = activoRepository.findById(activoId)
            .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado"));

    TipoOperacion tipoOp = TipoOperacion.valueOf(tipoOperacion);
    BigDecimal valorTotal = cantidad.multiply(precio);

    // Verificar saldo suficiente para compra
    if (tipoOp == TipoOperacion.compra && usuario.getSaldo().compareTo(valorTotal) < 0) {
        throw new IllegalArgumentException("Saldo insuficiente para realizar la compra");
    }

    Operacion operacion = new Operacion(
        usuario,
        activo,
        tipoOp,
        cantidad,
        precio,
        valorTotal,
        false,
        BigDecimal.ZERO
    );

    usuarioRepository.save(usuario);
    operacionRepository.save(operacion);
}


    @Transactional
public void cerrarOperacion(Long operacionId, String email) {
    System.out.println("Starting cerrarOperacion with operacionId=" + operacionId);
    
    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    Operacion operacion = operacionRepository.findById(operacionId)
        .orElseThrow(() -> new IllegalArgumentException("Operación no encontrada"));

    if (operacion.isCerrada()) {
        throw new IllegalStateException("Operación ya cerrada");
    }
    
    if (!operacion.getUsuario().getId().equals(usuario.getId())) {
        throw new IllegalStateException("Operación no pertenece al usuario");
    }

    Activo activo = operacion.getActivo();
    if (activo == null || activo.getNombre() == null) {
        throw new IllegalStateException("Activo inválido en la operación");
    }
    
    MarketChart chart = client.getCoinMarketChartById(activo.getNombre().toLowerCase(), Currency.USD, 1);
    if (chart == null) {
        throw new RuntimeException("No se pudo obtener datos de mercado");
    }

    List<?> rawPrices = chart.getPrices();
    if (rawPrices == null || rawPrices.isEmpty()) {
        throw new RuntimeException("No hay datos de precios disponibles");
    }
    
    Object latestPriceObj = rawPrices.get(rawPrices.size() - 1);
    BigDecimal currentPrice = null;
    
    try {
        if (latestPriceObj instanceof List<?>) {
            List<?> latestPriceData = (List<?>) latestPriceObj;
            if (latestPriceData.size() > 1) {
                Object priceObj = latestPriceData.get(1);
                
                if (priceObj instanceof String) {
                    currentPrice = new BigDecimal((String) priceObj);
                } else if (priceObj instanceof Number) {
                    currentPrice = BigDecimal.valueOf(((Number) priceObj).doubleValue());
                } else {
                    throw new RuntimeException("Unexpected price data type: " + 
                        (priceObj != null ? priceObj.getClass().getName() : "null"));
                }
            } else {
                throw new RuntimeException("Price data entry has insufficient elements");
            }
        } else if (latestPriceObj instanceof Map) {
            Map<?, ?> priceMap = (Map<?, ?>) latestPriceObj;
            if (priceMap.containsKey("price")) {
                Object price = priceMap.get("price");
                if (price instanceof Number) {
                    currentPrice = BigDecimal.valueOf(((Number) price).doubleValue());
                } else if (price instanceof String) {
                    currentPrice = new BigDecimal((String) price);
                }
            }
        }
        
        if (currentPrice == null) {
            throw new RuntimeException("Unexpected price data structure: " + 
                (latestPriceObj != null ? latestPriceObj.getClass().getName() : "null"));
        }
    } catch (Exception e) {
        throw new RuntimeException("Error extracting current price: " + e.getMessage(), e);
    }
    
    System.out.println("Current price: " + currentPrice);
    
    BigDecimal beneficioPerdida;
    if (operacion.getTipoOperacion() == TipoOperacion.compra) {
        beneficioPerdida = currentPrice.subtract(operacion.getPrecio()).multiply(operacion.getCantidad());
    } else {
        beneficioPerdida = operacion.getPrecio().subtract(currentPrice).multiply(operacion.getCantidad());
    }

    operacion.setCerrada(true);
    operacion.setBeneficioPerdida(beneficioPerdida);
    
    usuario.setSaldo(usuario.getSaldo().add(beneficioPerdida));
    System.out.println("New balance: " + usuario.getSaldo());


    int currentScore = 0;
    try {
        currentScore = Integer.parseInt(usuario.getPuntuacion());
        System.out.println("Current score: " + currentScore);
    } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
    }
    
    BigDecimal valorInvertido = operacion.getValorTotal();
    if (valorInvertido == null || valorInvertido.compareTo(BigDecimal.ZERO) == 0) {
        usuario.setPuntuacion(String.valueOf(currentScore));
    } else {
        try {
            BigDecimal porcentajeCambio = beneficioPerdida.multiply(BigDecimal.valueOf(100));
            porcentajeCambio = porcentajeCambio.divide(valorInvertido, new MathContext(4, RoundingMode.HALF_DOWN))
            .multiply(BigDecimal.valueOf(20));

            int puntosCambio = porcentajeCambio.setScale(0, RoundingMode.DOWN).intValue();
            System.out.println("Percentage change: " + porcentajeCambio + "%, points change: " + puntosCambio);
            
            int nuevaPuntuacion;
            if (puntosCambio > 0) {
                nuevaPuntuacion = currentScore + puntosCambio;
            } else if (puntosCambio < 0) {
                int perdidaAbsoluta = Math.abs(puntosCambio);
                nuevaPuntuacion = Math.max(0, currentScore - perdidaAbsoluta);
            } else {
                nuevaPuntuacion = currentScore;
            }
            
            System.out.println("Updating score from " + currentScore + " to " + nuevaPuntuacion);
            usuario.setPuntuacion(String.valueOf(nuevaPuntuacion));
        } catch (Exception e) {
            System.err.println("Error calculating score: " + e.getMessage());
            usuario.setPuntuacion(String.valueOf(currentScore));
        }
    }

    try {
        usuarioRepository.save(usuario);
        operacionRepository.save(operacion);
        System.out.println("Operation completed successfully");
    } catch (Exception e) {
        System.err.println("Error saving changes: " + e.getMessage());
        throw new RuntimeException("Error al guardar los cambios", e);
    }
}

    public List<Operacion> obtenerOperacionesPorUsuario(Usuario usuario) {
        return operacionRepository.findByUsuario(usuario);
    }

    public Operacion obtenerOperacionPorId(Long id) {
        return operacionRepository.findById(id).orElse(null);
    }

    public void guardarOperacion(Operacion operacion) {
        operacionRepository.save(operacion);
    }
}
