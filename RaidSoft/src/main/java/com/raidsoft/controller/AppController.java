package com.raidsoft.controller;

import com.raidsoft.model.Producto; // <--- NUEVA IMPORTACIÓN NECESARIA
import com.raidsoft.repository.ProductoRepository;
import com.raidsoft.model.Rol;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.UsuarioService; // Importar el servicio nuevo
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
    private UsuarioService usuarioService; // Inyección del servicio
    
    // Inyectar el repositorio de productos
    @Autowired
    private ProductoRepository productoRepository;

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

    @GetMapping("/admin/dashboard")
    public String adminPanel(Model model, Authentication auth) { 
        // Pasamos datos del usuario al dashboard para mostrar nombre/foto
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

    // --- GESTIÓN DE PERFIL (NUEVO) ---

    // 1. Ver Perfil (Solo Lectura)
    @GetMapping("/perfil")
    public String verPerfil(Authentication auth, Model model) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "perfil"; 
    }

    // 2. Editar Perfil (Formulario)
    @GetMapping("/perfil/editar")
    public String editarPerfil(Authentication auth, Model model) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "edit_perfil";
    }
    
    // Agregar método para la vista de productos (READ - Listar)
    @GetMapping("/admin/productos")
    public String adminProductos(Model model, Authentication auth) {
        // Datos del usuario para el sidebar/header
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);

        // Lista de productos para la tabla
        model.addAttribute("listaProductos", productoRepository.findAll());
        
        return "admin_productos";
    }

    // --- REPORTE DE STOCK CRÍTICO (CORREGIDO) ---
    @GetMapping("/admin/stock")
    public String adminStock(Model model, Authentication auth) {
        // Datos del usuario para el sidebar/header
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);

        // CORRECCIÓN: Usamos el método findProductosByStockCritico() que usa la @Query
        model.addAttribute("productosFaltantes", 
                            productoRepository.findProductosByStockCritico()); 
        
        return "admin_stock"; 
    }

    // --- CRUD DE PRODUCTOS ---

    // 1. CREATE (Mostrar Formulario de Nuevo Producto)
    @GetMapping("/admin/productos/nuevo")
    public String mostrarFormularioNuevoProducto(Model model, Authentication auth) {
        // Datos del usuario (mantener el sidebar/header)
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        
        model.addAttribute("producto", new Producto());
        model.addAttribute("accion", "Crear"); // Usado en la vista (form_producto.html) para el título/botón
        return "form_producto"; 
    }

    // 2. UPDATE (Mostrar Formulario de Edición de Producto)
    @GetMapping("/admin/productos/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable("id") Long id, 
                                                Model model, 
                                                Authentication auth, 
                                                RedirectAttributes redirectAttributes) {
        
        Producto producto = productoRepository.findById(id).orElse(null);
        
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/admin/productos";
        }

        // Datos del usuario (mantener el sidebar/header)
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        
        model.addAttribute("producto", producto);
        model.addAttribute("accion", "Editar"); // Usado en la vista (form_producto.html) para el título/botón
        return "form_producto";
    }

    // 3. CREATE & UPDATE (Guardar Producto)
    @PostMapping("/admin/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, 
                                  RedirectAttributes redirectAttributes) {
        try {
            // Verifica si es una creación (idProducto == null) o una edición
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

    // 4. DELETE (Eliminar Producto)
    @GetMapping("/admin/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id, 
                                   RedirectAttributes redirectAttributes) {
        try {
            productoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    // 3. Procesar Cambios
    @PostMapping("/perfil/guardar")
    public String guardarPerfil(Authentication auth,
                                @ModelAttribute Usuario usuarioForm,
                                @RequestParam("file") MultipartFile archivo,
                                @RequestParam(required = false) String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            Usuario usuarioActual = usuarioRepository.findByUsername(username).orElseThrow();
            
            usuarioService.actualizarPerfil(usuarioActual, usuarioForm, archivo, newPassword);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
            return "redirect:/perfil";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }

    // --- REGISTRO ---
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El usuario ya existe.");
                return "redirect:/login?error";
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setEstado(true);
            if (usuario.getRol() == null) usuario.setRol(Rol.VENDEDOR);
            
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Cuenta creada. Inicia sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/login?error";
        }
    }
}