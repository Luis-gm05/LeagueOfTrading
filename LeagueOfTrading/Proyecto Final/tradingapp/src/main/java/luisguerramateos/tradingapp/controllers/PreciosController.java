package luisguerramateos.tradingapp.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.constant.Currency;
import com.litesoftwares.coingecko.domain.Coins.MarketChart;
import com.litesoftwares.coingecko.exception.CoinGeckoApiException;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import jakarta.persistence.EntityNotFoundException;
import luisguerramateos.tradingapp.entities.Activo;
import luisguerramateos.tradingapp.entities.Usuario;
import luisguerramateos.tradingapp.services.ActivoService;
import luisguerramateos.tradingapp.services.OperacionService;
import luisguerramateos.tradingapp.services.UsuarioService;

@Controller
@RequestMapping("/precios")
public class PreciosController {
    private final OperacionService operacionService;
    private final ActivoService activoService;
    private final UsuarioService usuarioService;
    private final CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

    public PreciosController(ActivoService activoService, UsuarioService usuarioService, OperacionService operacionService) {
        this.operacionService = operacionService;
        this.activoService = activoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String showCryptoList(Model model) {
        List<Activo> cryptos = activoService.getAll();
        model.addAttribute("cryptos", cryptos);

        String email = getAuthenticatedUserEmail();
        if (email != null) {
            Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
            if (usuario.isPresent()) {
                model.addAttribute("usuario", usuario.get());
            } else {
                // If user not found in database, redirect to login
                return "redirect:/userlogin";
            }
        } else {
            // If not authenticated, redirect to login
            return "redirect:/userlogin";
        }

        return "precios";
    }

    @GetMapping("/{coinId}")
    public String getPriceData(@PathVariable String coinId, Model model) {
        try {
            String email = getAuthenticatedUserEmail();
            if (email == null) {
                return "redirect:/userlogin";
            }

            Activo activo = activoService.findByNombre(coinId);
            model.addAttribute("selectedCoinId", activo.getId());

            Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
            if (usuario.isPresent()) {
                model.addAttribute("usuario", usuario.get());
            } else {
                return "redirect:/userlogin";
            }

            MarketChart chart = client.getCoinMarketChartById(coinId.toLowerCase(), Currency.USD, 5);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<PriceData> priceDataList = new ArrayList<>();

            for (List<String> entry : chart.getPrices()) {
                long timestamp = Long.parseLong(entry.get(0));
                String date = dateFormat.format(new Date(timestamp));
                double price = Double.parseDouble(entry.get(1));
                priceDataList.add(new PriceData(date, price));
            }

            double latestPrice = priceDataList.isEmpty() ? 0 : priceDataList.get(priceDataList.size() - 1).getPrice();
            
            model.addAttribute("priceData", priceDataList);
            model.addAttribute("selectedCoin", coinId);
            model.addAttribute("latestPrice", latestPrice);

            return "graficomoneda";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "La criptomoneda " + coinId + " no está disponible en nuestra plataforma");
            return "precios";
        } catch (CoinGeckoApiException e) {
            model.addAttribute("error", "Se ha llegado al límite de intentos de la API, inténtalo en unos momentos");
            return "precios";
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los datos de la criptomoneda: " + e.getMessage());
            return "precios";
        }
    }

    @PostMapping("/confirmaroperacion")
        public String confirmarOperacion(
            @RequestParam("operationType") String operationType,
            @RequestParam("cantidad") BigDecimal cantidad,
            @RequestParam("coinId") String coinName,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("precio") BigDecimal precio,
            RedirectAttributes redirectAttributes) {
            
            try {
                Activo activo = activoService.findByNombre(coinName);
                Long activoId = activo.getId();

                operacionService.createOperacion(usuarioId, activoId, operationType, cantidad, precio);
                redirectAttributes.addFlashAttribute("successMessage", "Operación " + operationType + " exitosa");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Saldo insuficiente")) {
                    redirectAttributes.addFlashAttribute("saldoError", true);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                }
                return "redirect:/precios/" + coinName;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
                return "redirect:/precios";
            }

            return "redirect:/precios/" + coinName;
        }

        @PostMapping("/cerrarOperacion")
        public String cerrarOperacion(@RequestParam("idOperacion") Long idOperacion, Principal principal) {
            try {
                operacionService.cerrarOperacion(idOperacion, principal.getName());
                return "redirect:/historial?success=true";
            } catch (CoinGeckoApiException e) {
                return "redirect:/historial?error=api_limit";
            } catch (Exception e) {
                return "redirect:/historial?error=general";
            }
        }


    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}

class PriceData {
    private String date;
    private double price;

    public PriceData(String date, double price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() { return date; }
    public double getPrice() { return price; }
}
