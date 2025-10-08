package gob.gt.com.lab.demo.service;

import gob.gt.com.lab.demo.entity.Product;
import gob.gt.com.lab.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        // Generar SKU automáticamente si no se proporciona
        if (product.getSku() == null || product.getSku().isEmpty()) {
            product.setSku(generateSku(product.getName()));
        }
        return productRepository.save(product);
    }

    private String generateSku(String name) {
        // Generar SKU basado en el nombre + timestamp
        String baseSku = name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (baseSku.length() > 6) {
            baseSku = baseSku.substring(0, 6);
        }
        return baseSku + System.currentTimeMillis() % 10000;
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setUnit(productDetails.getUnit());
            // Actualizar SKU solo si se proporciona uno nuevo
            if (productDetails.getSku() != null && !productDetails.getSku().isEmpty()) {
                product.setSku(productDetails.getSku());
            }
            return productRepository.save(product);
        }).orElse(null);
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return true;
        }).orElse(false);
    }
}
