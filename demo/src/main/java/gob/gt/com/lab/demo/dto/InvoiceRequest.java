package gob.gt.com.lab.demo.dto;

import java.util.List;

public class InvoiceRequest {
    private Long customerId;
    private List<InvoiceItemRequest> items;

    // Constructors
    public InvoiceRequest() {}

    public InvoiceRequest(Long customerId, List<InvoiceItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
    }

    // Getters and setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<InvoiceItemRequest> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemRequest> items) {
        this.items = items;
    }

    public static class InvoiceItemRequest {
        private Long productId;
        private Integer quantity;
        private Double unitPrice;

        // Constructors
        public InvoiceItemRequest() {}

        public InvoiceItemRequest(Long productId, Integer quantity, Double unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        // Getters and setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }
    }
}