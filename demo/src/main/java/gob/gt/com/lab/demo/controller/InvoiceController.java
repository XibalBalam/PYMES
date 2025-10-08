package gob.gt.com.lab.demo.controller;

import gob.gt.com.lab.demo.entity.Invoice;
import gob.gt.com.lab.demo.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    // Reporte: facturas por rango de fechas
    @GetMapping("/report/date-range")
    public List<Invoice> getInvoicesByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return invoiceService.getInvoicesByDateRange(start, end);
    }

    // Reporte: total de ventas por cliente
    @GetMapping("/report/sales-by-customer/{customerId}")
    public Double getTotalSalesByCustomer(@PathVariable Long customerId) {
        return invoiceService.getTotalSalesByCustomer(customerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody gob.gt.com.lab.demo.dto.InvoiceRequest request) {
        try {
            Invoice createdInvoice = invoiceService.createInvoiceFromRequest(request);
            gob.gt.com.lab.demo.dto.InvoiceResponse response = invoiceService.convertToResponse(createdInvoice);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno del servidor"));
        }
    }
    
    // Clase interna para respuestas de error
    public static class ErrorResponse {
        private String error;
        private String timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = java.time.LocalDateTime.now().toString();
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id, @RequestBody gob.gt.com.lab.demo.dto.InvoiceRequest request) {
        try {
            System.out.println("Iniciando actualización de factura ID: " + id);
            Invoice updated = invoiceService.updateInvoiceFromRequest(id, request);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Factura no encontrada"));
            }
            System.out.println("Factura actualizada, convirtiendo a response...");
            gob.gt.com.lab.demo.dto.InvoiceResponse response = invoiceService.convertToResponse(updated);
            System.out.println("Response creado exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        if (invoiceService.deleteInvoice(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
