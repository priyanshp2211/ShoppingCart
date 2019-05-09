package com.aimms.assignment.service;

import com.aimms.assignment.model.Cart;
import com.aimms.assignment.model.Item;
import com.aimms.assignment.model.Product;
import com.aimms.assignment.model.ProductCatalogue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

//This is a service class that could be exposed through rest/soap

public class ShoppingService {

    private static final int DISCOUNT_PERCENTAGE = 10;
    private static final Logger log = LoggerFactory.getLogger(ShoppingService.class);

    private ProductCatalogue productCatalogue;
    private Cart cart;

    public ShoppingService(ProductCatalogue productCatalogue) {
        this.productCatalogue = productCatalogue;
        this.cart = new Cart(productCatalogue);
    }

    public Map showAllProducts() {
        log.info("Get All Products In The Catalogue");
        Map productStock = this.productCatalogue.getAllProducts();
        productStock.keySet().forEach(p -> log.info(p + " :: available stock :: " + productStock.get(p)));
        return productStock;
    }

    public void addToCart(Product product, int quantity) {
        log.info("The Product :: " + product + ":: added to cart for quantity :: " + quantity);
        this.cart.addToCart(new Item(product, quantity));
    }

    public void updateCart(Product product, int quantity) {
        log.info("The Product :: " + product + ":: updated to cart for quantity :: " + quantity);
        this.cart.updateCart(new Item(product, quantity));
    }

    public Collection<Item> showAllItemsInTheCart() {
        log.info("Get All Items In The Cart");
        Collection<Item> allItems = this.cart.getAllItemInTheCart();
        allItems.forEach(p -> log.info(String.valueOf(p)));
        return allItems;
    }

    public Boolean isEligibleForDiscount() {
        Boolean isEligible = this.cart.isEligibleForDiscount();
        log.info("Is Eligible For Discount :: " + (isEligible ? "Yes" : "No"));
        return isEligible;
    }

    public BigDecimal getTotalPriceWithoutDiscount() {
        return this.cart.getTotalPriceWithoutDiscount();
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalAmt = this.cart.getTotalPrice(getTotalDiscount());
        log.info("The total price of cart is :: " + totalAmt);
        return totalAmt;
    }

    public BigDecimal getTotalDiscount() {
        BigDecimal discountAmt = this.cart.getTotalDiscount(getTotalPriceWithoutDiscount(), BigDecimal.valueOf(DISCOUNT_PERCENTAGE));
        log.info("Discount Amount :: " + discountAmt);
        return discountAmt;
    }
}
