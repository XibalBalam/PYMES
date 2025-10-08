package gob.gt.com.lab.demo.controller;

import gob.gt.com.lab.demo.entity.*;
import gob.gt.com.lab.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
public class DataInitController {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/sample-data")
    public ResponseEntity<?> initializeSampleData() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Crear roles si no existen
            if (roleRepository.count() == 0) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole.setDescription("Administrador del sistema");
                roleRepository.save(adminRole);
                
                Role userRole = new Role();
                userRole.setName("USER");
                userRole.setDescription("Usuario estándar");
                roleRepository.save(userRole);
                
                result.put("roles_created", 2);
            }
            
            // Crear usuarios de prueba si no existen
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("$2a$10$N.wmKN0.5Z5qWzKGgGLb.eY8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8"); // password: admin
                admin.setFullName("Administrador");
                admin.setEmail("admin@empresa.com");
                admin.setIsActive(true);
                userRepository.save(admin);
                
                User user1 = new User();
                user1.setUsername("usuario1");
                user1.setPassword("$2a$10$N.wmKN0.5Z5qWzKGgGLb.eY8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8"); // password: usuario1
                user1.setFullName("Usuario Test");
                user1.setEmail("usuario1@empresa.com");
                user1.setIsActive(true);
                userRepository.save(user1);
                
                result.put("users_created", 2);
            }
            
            // Crear clientes de prueba si no existen
            if (customerRepository.count() == 0) {
                Customer customer1 = new Customer();
                customer1.setName("Empresa ABC S.A.");
                customer1.setEmail("contacto@empresaabc.com");
                customer1.setPhone("2234-5678");
                customer1.setAddress("Zona 10, Ciudad de Guatemala");
                customerRepository.save(customer1);
                
                Customer customer2 = new Customer();
                customer2.setName("Comercial XYZ");
                customer2.setEmail("ventas@comercialxyz.com");
                customer2.setPhone("2345-6789");
                customer2.setAddress("Zona 4, Mixco");
                customerRepository.save(customer2);
                
                result.put("customers_created", 2);
            }
            
            // Crear productos de prueba si no existen
            if (productRepository.count() == 0) {
                Product product1 = new Product();
                product1.setName("Laptop Dell Inspiron");
                product1.setDescription("Laptop Dell Inspiron 15 3000, Intel Core i5, 8GB RAM, 256GB SSD");
                product1.setPrice(4500.00);
                product1.setUnit("UNIDAD");
                productRepository.save(product1);
                
                Product product2 = new Product();
                product2.setName("Mouse Inalámbrico");
                product2.setDescription("Mouse inalámbrico óptico USB 2.4GHz con receptor nano");
                product2.setPrice(75.50);
                product2.setUnit("UNIDAD");
                productRepository.save(product2);
                
                Product product3 = new Product();
                product3.setName("Teclado Mecánico");
                product3.setDescription("Teclado mecánico RGB con switches Cherry MX Blue");
                product3.setPrice(350.00);
                product3.setUnit("UNIDAD");
                productRepository.save(product3);
                
                result.put("products_created", 3);
            }
            
            result.put("message", "Datos de prueba inicializados correctamente");
            result.put("status", "success");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al inicializar datos: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getDataStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("customers_count", customerRepository.count());
        status.put("products_count", productRepository.count());
        status.put("users_count", userRepository.count());
        status.put("roles_count", roleRepository.count());
        return ResponseEntity.ok(status);
    }
}