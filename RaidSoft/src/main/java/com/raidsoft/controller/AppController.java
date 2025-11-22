package com.raidsoft.controller;

import com.raidsoft.model.Perfil;
import com.raidsoft.model.Producto;
import com.raidsoft.model.Rol;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.ProductoRepository;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UsuarioService usuarioService; 
    
    @Autowired
    private ProductoRepository productoRepository;

    // --- LOGIN Y REDIRECCIÓN ---

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/redirectByRole")
    public String defaultAfterLogin(Authentication auth) {
        String rol = auth.getAuthorities().toString();
        if (rol.contains("ADMINISTRADOR")) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/vendedor/dashboard";
    }

    // --- DASHBOARDS ---

    @GetMapping("/admin/dashboard")
    public String adminPanel(Model model, Authentication auth) { 
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        return "admin_dashboard"; 
    }

    @GetMapping("/vendedor/dashboard")
    public String vendedorPanel(Model model, Authentication auth) { 
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        return "vendedor_dashboard"; 
    }

    // --- GESTIÓN DE PERFIL (ADAPTADA A NUEVA BD) ---

    @GetMapping("/perfil")
    public String verPerfil(Authentication auth, Model model) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        model.addAttribute("usuario", usuario);
        // Pasamos el perfil explícitamente para facilitar el acceso en la vista
        model.addAttribute("perfil", usuario.getPerfil()); 
        
        return "perfil"; 
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(Authentication auth, Model model) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        model.addAttribute("usuario", usuario);
        // Pasamos el objeto 'perfil' para que el formulario th:object="${perfil}" funcione
        model.addAttribute("perfil", usuario.getPerfil());
        
        return "edit_perfil";
    }
    
    @PostMapping("/perfil/guardar")
    public String guardarPerfil(Authentication auth,
                                @ModelAttribute Perfil perfilForm, // Recibimos el objeto Perfil del formulario
                                @RequestParam("file") MultipartFile archivo,
                                @RequestParam(required = false) String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            Usuario usuarioActual = usuarioRepository.findByUsername(username).orElseThrow();
            
            // El servicio actualiza el Perfil (datos) y el Usuario (password)
            usuarioService.actualizarPerfil(usuarioActual, perfilForm, archivo, newPassword);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
            return "redirect:/perfil";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }

    // --- GESTIÓN DE USUARIOS (ADMIN) ---

    @GetMapping("/admin/usuarios")
    public String adminUsuarios(Model model, Authentication auth) {
        // Datos del usuario logueado para el sidebar
        String username = auth.getName();
        Usuario usuarioLogueado = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuarioLogueado);

        // Listar todos los usuarios
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());
        
        return "admin_usuarios";
    }

    @GetMapping("/admin/usuarios/toggle/{id}")
    public String toggleEstadoUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        try {
            Usuario usuario = usuarioRepository.findById(id).orElse(null);
            
            // Evitar que el admin se desactive a sí mismo
            if (usuario != null && usuario.getUsername().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("error", "No puedes desactivar tu propia cuenta.");
                return "redirect:/admin/usuarios";
            }

            if (usuario != null) {
                boolean estadoActual = Boolean.TRUE.equals(usuario.getEstado());
                usuario.setEstado(!estadoActual);
                usuarioRepository.save(usuario);
                
                String accion = !estadoActual ? "activado" : "desactivado";
                redirectAttributes.addFlashAttribute("success", "Usuario " + accion + " correctamente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado.");
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        try {
            Usuario usuario = usuarioRepository.findById(id).orElse(null);

            // Seguridad: No auto-eliminarse
            if (usuario != null && usuario.getUsername().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("error", "No puedes eliminar tu propia cuenta.");
                return "redirect:/admin/usuarios";
            }

            if (usuario != null) {
                // Al tener CascadeType.ALL, borrar el usuario borra su perfil asociado
                usuarioRepository.delete(usuario);
                redirectAttributes.addFlashAttribute("success", "Usuario y perfil eliminados permanentemente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: El usuario tiene historial asociado.");
        }
        return "redirect:/admin/usuarios";
    }

    // --- GESTIÓN DE PRODUCTOS (ADMIN) ---

    @GetMapping("/admin/productos")
    public String adminProductos(Model model, Authentication auth) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "admin_productos";
    }

    @GetMapping("/admin/stock")
    public String adminStock(Model model, Authentication auth) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        // Nota: Asegúrate de tener este método en tu ProductoRepository
        // Si no, usa findAll() y filtra en la vista con th:if="${p.stock <= p.stockMinimo}"
        try {
             model.addAttribute("productosFaltantes", productoRepository.findProductosByStockCritico());
        } catch (Exception e) {
             // Fallback si el método del repo no existe aún
             model.addAttribute("productosFaltantes", productoRepository.findAll());
        }
        return "admin_stock";
    }

    @GetMapping("/admin/productos/nuevo")
    public String mostrarFormularioNuevoProducto(Model model, Authentication auth) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        
        model.addAttribute("producto", new Producto());
        model.addAttribute("accion", "Crear");
        return "form_producto";
    }

    @GetMapping("/admin/productos/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable("id") Long id, Model model, Authentication auth, RedirectAttributes redirectAttributes) {
        Producto producto = productoRepository.findById(id).orElse(null);
        
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/admin/productos";
        }
        
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        
        model.addAttribute("producto", producto);
        model.addAttribute("accion", "Editar");
        return "form_producto";
    }

    @PostMapping("/admin/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
        try {
            boolean isNew = producto.getIdProducto() == null;
            productoRepository.save(producto);
            
            String message = isNew ? "Producto creado correctamente." : "Producto actualizado correctamente.";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/admin/productos";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    @GetMapping("/admin/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    // --- REGISTRO (PÚBLICO) ---

//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
//        try {
//            if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
//                redirectAttributes.addFlashAttribute("error", "El usuario ya existe.");
//                return "redirect:/login?error";
//            }
//            
//            // 1. Encriptar contraseña y configurar estado/rol
//            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
//            usuario.setEstado(true);
//            if (usuario.getRol() == null) usuario.setRol(Rol.VENDEDOR);
//            
//            // 2. CORRECCIÓN CRÍTICA: Crear, asociar el Perfil e INICIALIZAR campos no nulos
//            Perfil nuevoPerfil = new Perfil();
//            // Evitar la excepción: Perfil requiere nombre y apellido (nullable=false)
//            nuevoPerfil.setNombre(usuario.getUsername()); // Usar username como nombre temporal
//            nuevoPerfil.setApellido("(Sin Apellido)");
//            
//            usuario.setPerfil(nuevoPerfil); // Vinculación bidireccional
//            
//            // 3. Guardar. Al tener cascade = ALL, se guarda el usuario y su perfil
//            usuarioRepository.save(usuario);
//            
//            redirectAttributes.addFlashAttribute("success", "Cuenta creada. Inicia sesión.");
//            return "redirect:/login";
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
//            return "redirect:/login?error";
//        }
//    }
}