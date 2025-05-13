package luisguerramateos.tradingapp.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import luisguerramateos.tradingapp.entities.Operacion;
import luisguerramateos.tradingapp.entities.Usuario;
import luisguerramateos.tradingapp.services.OperacionService;
import luisguerramateos.tradingapp.services.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private OperacionService operacionService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/userlogin")
    public String mostrarLogin() {
        return "userlogin"; 
    }

    
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                List<Operacion> operaciones = operacionService.obtenerOperacionesPorUsuario(usuario);
                model.addAttribute("perfil", usuario);
                model.addAttribute("puntuacion", usuario.getPuntuacion());
                model.addAttribute("operaciones", operaciones);
                return "perfil";
            }
        }
        return "redirect:/userlogin";
    }

    @GetMapping("/historial")
    public String mostrarHistorial(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                List<Operacion> operaciones = operacionService.obtenerOperacionesPorUsuario(usuario);
                
                BigDecimal totalBeneficioPerdida = operaciones.stream()
                    .filter(Operacion::isCerrada)
                    .map(Operacion::getBeneficioPerdida)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                model.addAttribute("operaciones", operaciones);
                model.addAttribute("totalBeneficioPerdida", totalBeneficioPerdida);
                return "historial";
            }
        }
        return "redirect:/userlogin";
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String procesarRegistro(@RequestParam String nombre,
                                @RequestParam String email,
                                @RequestParam String password,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (usuarioService.buscarPorEmail(email).isPresent()) {
            model.addAttribute("usuario", new Usuario());
            model.addAttribute("error", "El email ya está registrado.");
            return "register";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuarioService.registrar(usuario);

        redirectAttributes.addFlashAttribute("registroExitoso", "¡Tu cuenta ha sido creada con éxito! Ahora puedes iniciar sesión.");
        return "redirect:/userlogin";
    }

    @GetMapping("/leaderboard")
    public String mostrarClasificacion(Model model){

        List<Usuario> usuarios=usuarioService.obtenerUsuariosOrdenadosPorPuntuacion();
        model.addAttribute("usuarios", usuarios);
        return "leaderboard";
    }
    @PostMapping("/perfil/eliminar")
    public String eliminarUsuario(@RequestParam Long id, Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Optional: Prevent a user from deleting others or ensure they only delete themselves
            if (principal != null && principal.getName().equals(usuario.getEmail())) {
                usuarioService.eliminarUsuario(id);

                return "redirect:/userlogin?logout";
            }
        }

        return "redirect:/perfil?error";
    }

}