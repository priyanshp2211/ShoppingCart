package com.aimms.assignment.model;

import com.aimms.assignment.exception.InvalidInputException;

import java.util.Collections;
import java.util.Map;

public final class ProductCatalogue {

    private Map<Product, Integer> productStockMap;

    public ProductCatalogue(Map<Product, Integer> productStockMap) {
        this.productStockMap = productStockMap;
    }

    public void addProductToCatalogue(Product item, Integer stock) {
        productStockMap.put(item, stock);
    }

    public Map<Product, Integer> getAllProducts() {
        return Collections.unmodifiableMap(productStockMap);
    }

    public void removeProduct(Product product) {
        productStockMap.remove(product);
    }

    public void addQuantity(Product product, int qty) {
        int oldStockQuantity = productStockMap.get(product);
        productStockMap.put(product, oldStockQuantity + qty);
    }

    public void reduceQuantity(Product product, int qty) {
        int oldStockQuantity = productStockMap.get(product);
        int afterReduceQty = oldStockQuantity - qty;
        if (afterReduceQty < 0) {
            throw new InvalidInputException("Product stock is unavailable");
        }
        productStockMap.put(product, afterReduceQty);
    }

    public int getAvailableStock(Product product) {
        return productStockMap.get(product);
    }

}
