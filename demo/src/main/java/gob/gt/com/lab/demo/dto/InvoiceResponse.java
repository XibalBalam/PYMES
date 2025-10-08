package gob.gt.com.lab.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class InvoiceResponse {
    private Long id;
    private CustomerSummary customer;
    private LocalDate date;
    private List<InvoiceLineResponse> lines;
    private Double total;

    // Constructors
    public InvoiceResponse() {}

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSummary getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSummary customer) {
        this.customer = customer;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<InvoiceLineResponse> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLineResponse> lines) {
        this.lines = lines;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public static class CustomerSummary {
        private Long id;
        private String name;
        private String email;

        public CustomerSummary() {}

        public CustomerSummary(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class InvoiceLineResponse {
        private Long id;
        private ProductSummary product;
        private Integer quantity;
        private Double price;
        private Double subtotal;

        public InvoiceLineResponse() {}

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public ProductSummary getProduct() {
            return product;
        }

        public void setProduct(ProductSummary product) {
            this.product = product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Double subtotal) {
            this.subtotal = subtotal;
        }

        public static class ProductSummary {
            private Long id;
            private String sku;
            private String name;
            private Double price;

            public ProductSummary() {}

            public ProductSummary(Long id, String sku, String name, Double price) {
                this.id = id;
                this.sku = sku;
                this.name = name;
                this.price = price;
            }

            // Getters and setters
            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public String getSku() {
                return sku;
            }

            public void setSku(String sku) {
                this.sku = sku;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Double getPrice() {
                return price;
            }

            public void setPrice(Double price) {
                this.price = price;
            }
        }
    }
}