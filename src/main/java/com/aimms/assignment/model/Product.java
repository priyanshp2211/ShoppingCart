package com.aimms.assignment.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Product {

    private final long productId;

    private final String productCode;

    private final Category category;

    private final BigDecimal pricePerItem;

    public Product(long productId, String productCode, Category category, BigDecimal price) {
        this.productId = productId;
        this.productCode = productCode;
        this.category = category;
        this.pricePerItem = price;
    }

    public long getProductId() {
        return productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return pricePerItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId &&
                productCode.equals(product.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productCode);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productCode='" + productCode + '\'' +
                ", category=" + category +
                ", pricePerItem=" + pricePerItem +
                '}';
    }
}
