
    package gob.gt.com.lab.demo.service;

import gob.gt.com.lab.demo.entity.Invoice;
import gob.gt.com.lab.demo.entity.InvoiceLine;
import gob.gt.com.lab.demo.repository.InvoiceRepository;
import gob.gt.com.lab.demo.repository.InvoiceLineRepository;
import gob.gt.com.lab.demo.repository.CustomerRepository;
import gob.gt.com.lab.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceLineRepository invoiceLineRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<Invoice> getInvoicesByDateRange(java.time.LocalDate start, java.time.LocalDate end) {
        return invoiceRepository.findAll().stream()
            .filter(inv -> inv.getDate() != null &&
                !inv.getDate().isBefore(start) && !inv.getDate().isAfter(end))
            .toList();
    }

    public Double getTotalSalesByCustomer(Long customerId) {
        return invoiceRepository.findAll().stream()
            .filter(inv -> inv.getCustomer() != null && inv.getCustomer().getId().equals(customerId))
            .mapToDouble(inv -> inv.getTotal() != null ? inv.getTotal() : 0.0)
            .sum();
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Invoice createInvoice(Invoice invoice) {
        // Validar cliente
        if (invoice.getCustomer() == null || invoice.getCustomer().getId() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (!customerRepository.existsById(invoice.getCustomer().getId())) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        // Validar líneas
        if (invoice.getLines() == null || invoice.getLines().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos una línea de producto");
        }
        double total = 0.0;
        for (InvoiceLine line : invoice.getLines()) {
            if (line.getProduct() == null || line.getProduct().getId() == null) {
                throw new IllegalArgumentException("Cada línea debe tener un producto válido");
            }
            if (!productRepository.existsById(line.getProduct().getId())) {
                throw new IllegalArgumentException("El producto con ID " + line.getProduct().getId() + " no existe");
            }
            if (line.getQuantity() == null || line.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (line.getPrice() == null || line.getPrice() <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a cero");
            }
            line.setInvoice(invoice);
            line.setSubtotal(line.getPrice() * line.getQuantity());
            total += line.getSubtotal();
        }
        invoice.setTotal(total);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        for (InvoiceLine line : invoice.getLines()) {
            invoiceLineRepository.save(line);
        }
        return savedInvoice;
    }

    public Invoice createInvoiceFromRequest(gob.gt.com.lab.demo.dto.InvoiceRequest request) {
        // Validar customer ID
        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        
        // Validar items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto");
        }

        // Crear la factura
        Invoice invoice = new Invoice();
        invoice.setCustomer(customerRepository.findById(request.getCustomerId()).orElse(null));
        invoice.setDate(java.time.LocalDate.now());

        // Crear las líneas
        java.util.List<InvoiceLine> lines = new java.util.ArrayList<>();
        double total = 0.0;

        for (gob.gt.com.lab.demo.dto.InvoiceRequest.InvoiceItemRequest item : request.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("El ID del producto es obligatorio");
            }
            if (!productRepository.existsById(item.getProductId())) {
                throw new IllegalArgumentException("El producto con ID " + item.getProductId() + " no existe");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice() <= 0) {
                throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
            }

            InvoiceLine line = new InvoiceLine();
            line.setProduct(productRepository.findById(item.getProductId()).orElse(null));
            line.setQuantity(item.getQuantity());
            line.setPrice(item.getUnitPrice());
            line.setSubtotal(item.getQuantity() * item.getUnitPrice());
            line.setInvoice(invoice);

            lines.add(line);
            total += line.getSubtotal();
        }

        invoice.setLines(lines);
        invoice.setTotal(total);

        return invoiceRepository.save(invoice);
    }

    public gob.gt.com.lab.demo.dto.InvoiceResponse convertToResponse(Invoice invoice) {
        System.out.println("Convirtiendo invoice a response, ID: " + invoice.getId());
        gob.gt.com.lab.demo.dto.InvoiceResponse response = new gob.gt.com.lab.demo.dto.InvoiceResponse();
        response.setId(invoice.getId());
        response.setDate(invoice.getDate());
        response.setTotal(invoice.getTotal());
        
        // Customer summary
        if (invoice.getCustomer() != null) {
            gob.gt.com.lab.demo.dto.InvoiceResponse.CustomerSummary customerSummary = 
                new gob.gt.com.lab.demo.dto.InvoiceResponse.CustomerSummary(
                    invoice.getCustomer().getId(),
                    invoice.getCustomer().getName(),
                    invoice.getCustomer().getEmail()
                );
            response.setCustomer(customerSummary);
        }
        
        // Lines
        if (invoice.getLines() != null && !invoice.getLines().isEmpty()) {
            java.util.List<gob.gt.com.lab.demo.dto.InvoiceResponse.InvoiceLineResponse> lineResponses = 
                new java.util.ArrayList<>();
            
            try {
                for (InvoiceLine line : invoice.getLines()) {
                gob.gt.com.lab.demo.dto.InvoiceResponse.InvoiceLineResponse lineResponse = 
                    new gob.gt.com.lab.demo.dto.InvoiceResponse.InvoiceLineResponse();
                lineResponse.setId(line.getId());
                lineResponse.setQuantity(line.getQuantity());
                lineResponse.setPrice(line.getPrice());
                lineResponse.setSubtotal(line.getSubtotal());
                
                // Product summary
                if (line.getProduct() != null) {
                    gob.gt.com.lab.demo.dto.InvoiceResponse.InvoiceLineResponse.ProductSummary productSummary = 
                        new gob.gt.com.lab.demo.dto.InvoiceResponse.InvoiceLineResponse.ProductSummary(
                            line.getProduct().getId(),
                            line.getProduct().getSku(),
                            line.getProduct().getName(),
                            line.getProduct().getPrice()
                        );
                    lineResponse.setProduct(productSummary);
                }
                
                    lineResponses.add(lineResponse);
                }
            } catch (Exception e) {
                System.err.println("Error al procesar líneas: " + e.getMessage());
                throw new RuntimeException("Error al convertir líneas de factura", e);
            }
            response.setLines(lineResponses);
        }
        
        System.out.println("Conversión completada exitosamente");
        return response;
    }

    @org.springframework.transaction.annotation.Transactional
    public Invoice updateInvoiceFromRequest(Long id, gob.gt.com.lab.demo.dto.InvoiceRequest request) {
        // Verificar que la factura existe
        if (!invoiceRepository.existsById(id)) {
            return null;
        }

        // Validar customer ID
        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        
        // Validar items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto");
        }

        return invoiceRepository.findById(id).map(invoice -> {
            try {
                // Actualizar customer
                invoice.setCustomer(customerRepository.findById(request.getCustomerId()).orElse(null));
                
                // Limpiar líneas existentes usando clear() para mantener la referencia de la colección
                if (invoice.getLines() != null) {
                    invoice.getLines().clear();
                } else {
                    invoice.setLines(new java.util.ArrayList<>());
                }
                
                // Forzar flush para asegurar que las eliminaciones se procesen
                invoiceRepository.flush();
            double total = 0.0;

            for (gob.gt.com.lab.demo.dto.InvoiceRequest.InvoiceItemRequest item : request.getItems()) {
                if (item.getProductId() == null) {
                    throw new IllegalArgumentException("El ID del producto es obligatorio");
                }
                if (!productRepository.existsById(item.getProductId())) {
                    throw new IllegalArgumentException("El producto con ID " + item.getProductId() + " no existe");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
                }
                if (item.getUnitPrice() == null || item.getUnitPrice() <= 0) {
                    throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
                }

                InvoiceLine line = new InvoiceLine();
                line.setProduct(productRepository.findById(item.getProductId()).orElse(null));
                line.setQuantity(item.getQuantity());
                line.setPrice(item.getUnitPrice());
                line.setSubtotal(item.getQuantity() * item.getUnitPrice());
                line.setInvoice(invoice);

                // Agregar directamente a la lista existente en lugar de crear una nueva
                invoice.getLines().add(line);
                total += line.getSubtotal();
            }
                invoice.setTotal(total);

                Invoice savedInvoice = invoiceRepository.save(invoice);
                
                // Refrescar la entidad para asegurar que las relaciones estén cargadas
                return invoiceRepository.findById(savedInvoice.getId()).orElse(savedInvoice);
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar la factura: " + e.getMessage(), e);
            }
        }).orElse(null);
    }

    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        return invoiceRepository.findById(id).map(invoice -> {
            invoice.setCustomer(invoiceDetails.getCustomer());
            invoice.setDate(invoiceDetails.getDate());
            // Actualizar líneas y total
            invoice.getLines().clear();
            double total = 0.0;
            if (invoiceDetails.getLines() != null) {
                for (InvoiceLine line : invoiceDetails.getLines()) {
                    line.setInvoice(invoice);
                    line.setSubtotal(line.getPrice() * line.getQuantity());
                    total += line.getSubtotal();
                    invoice.getLines().add(line);
                }
            }
            invoice.setTotal(total);
            return invoiceRepository.save(invoice);
        }).orElse(null);
    }

    public boolean deleteInvoice(Long id) {
        return invoiceRepository.findById(id).map(invoice -> {
            invoiceRepository.delete(invoice);
            return true;
        }).orElse(false);
    }
}
