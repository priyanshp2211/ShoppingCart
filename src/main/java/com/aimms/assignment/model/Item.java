package com.aimms.assignment.model;

public final class Item {
    private final Product product;
    private int quantity;

    public Item(Product product, int qty) {
        this.product = product;
        this.quantity = qty;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return product.equals(item.product);
    }

    @Override
    public int hashCode() {
        return product.hashCode();
    }

    @Override
    public String toString() {
        return "Item{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}
