package com.aimms.assignment.model;

import com.aimms.assignment.exception.InvalidInputException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;

public final class Cart {
    private HashSet<Item> itemHashSet;
    private ProductCatalogue productCatalogue;

    public Cart(ProductCatalogue productCatalogue) {
        this.itemHashSet = new LinkedHashSet<>();
        this.productCatalogue = productCatalogue;
    }

    public void addToCart(Item item) {
        int availableStock = productCatalogue.getAvailableStock(item.getProduct());
        if (availableStock >= 1) {
            //Equals Checking The ProductCode only & update the quantity if found
            if (itemHashSet.contains(item)) {
                updateCart(item);
            } else {
                productCatalogue.reduceQuantity(item.getProduct(), item.getQuantity());
                itemHashSet.add(item);
            }
        }
    }

    public Collection<Item> getAllItemInTheCart() {
        return Collections.unmodifiableSet(itemHashSet);
    }

    public void removeItem(Item item) {
        productCatalogue.addQuantity(item.getProduct(), item.getQuantity());
        itemHashSet.remove(item);
    }

    public void updateCart(Item item) {
        if (itemHashSet.contains(item)) {
            Item addedItem = itemHashSet.stream()
                    .filter(p -> p.getProduct().equals(item.getProduct()))
                    .findAny()
                    .orElse(null);
            if (addedItem != null) {
                itemHashSet.remove(addedItem);
                int totalQty = item.getQuantity() + addedItem.getQuantity();
                //Scenario of only added extra qty of same item
                if (totalQty > addedItem.getQuantity()) {
                    productCatalogue.reduceQuantity(item.getProduct(), item.getQuantity());
                    itemHashSet.add(new Item(item.getProduct(), totalQty));
                } else if (totalQty < addedItem.getQuantity()) { //Scenario of reduced qty of existing item on cart
                    productCatalogue.addQuantity(item.getProduct(), addedItem.getQuantity() - totalQty);
                    itemHashSet.add(new Item(item.getProduct(), totalQty));
                } else {
                    productCatalogue.addQuantity(item.getProduct(), addedItem.getQuantity());
                }
            }
        } else {
            throw new InvalidInputException("The item doesn't exist for update");
        }
    }

    public BigDecimal getTotalPriceWithoutDiscount() {
        BigDecimal total = new BigDecimal(0);
        for (Item item : itemHashSet) {
            BigDecimal amt = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(amt);
        }
        return total;
    }

    public BigDecimal getTotalDiscount(BigDecimal base, BigDecimal pct) {
        //Discount Condition
        if (itemHashSet.size() >= 3
                || LocalDate.now().getDayOfWeek() == DayOfWeek.FRIDAY) {
            return base.multiply(pct).divide(new BigDecimal(100));
        }
        return new BigDecimal(0);
    }

    public boolean isEligibleForDiscount() {
        boolean eligibility = false;
        if (itemHashSet.size() >= 3 || LocalDate.now().getDayOfWeek() == DayOfWeek.FRIDAY) {
            eligibility = true;
        }
        return eligibility;
    }

    public BigDecimal getTotalPrice(BigDecimal discountAmount) {
        return getTotalPriceWithoutDiscount().subtract(discountAmount);
    }
}
