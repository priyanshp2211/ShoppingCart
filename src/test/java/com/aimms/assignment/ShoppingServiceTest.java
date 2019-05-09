package com.aimms.assignment;

import com.aimms.assignment.Util.UtilityClass;
import com.aimms.assignment.model.Category;
import com.aimms.assignment.model.Item;
import com.aimms.assignment.model.Product;
import com.aimms.assignment.model.ProductCatalogue;
import com.aimms.assignment.service.ShoppingService;
import com.opencsv.CSVReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.aimms.assignment.TestConstants.IPHONE_CHARGER;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ShoppingServiceTest {

    private ShoppingService shoppingService;

    @Before
    public void setup() throws Exception {
        String fileName = "product.csv";
        try {
            CSVReader reader = new CSVReader(new FileReader(UtilityClass.getFileFromResources(fileName)));
            reader.readNext();
            String[] nextLine;
            HashMap<Product, Integer> productStock = new HashMap<>();
            while ((nextLine = reader.readNext()) != null) {
                productStock.put(new Product(Long.parseLong(nextLine[0].trim()), nextLine[1].trim(), Category.valueOf(nextLine[2].trim()), new BigDecimal(nextLine[3].trim())), 4);
            }
            ProductCatalogue productCatalogue = new ProductCatalogue(productStock);
            shoppingService = new ShoppingService(productCatalogue);
        } catch (Exception exe) {
            exe.printStackTrace();
            throw new Exception("Setup Failed Due To File Failure");
        }
    }

    @Test
    public void testAllProductStockSetup() {
        Map productStock = shoppingService.showAllProducts();
        assertEquals(5, productStock.keySet().size());
        assertEquals(4, productStock.get(productStock.keySet().stream().findFirst().get()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testModifyTheProductStock() {
        Map productStock = shoppingService.showAllProducts();
        productStock.put(new Product(23, "FAKE PRODUCT", Category.MOBILE_PHONE, new BigDecimal(900)), 10);
    }

    @Test
    public void testAddToCart() {
        Map productStock = shoppingService.showAllProducts();
        Set<Product> products = productStock.keySet();

        Product iphoneX = getProductByName("Iphone X", products);
        shoppingService.addToCart(iphoneX, 2);

        Product iphoneCharger = getProductByName("Iphone charger", products);
        shoppingService.addToCart(iphoneCharger, 1);
        Collection<Item> cartItems = shoppingService.showAllItemsInTheCart();
        assertEquals(2, cartItems.size());
        assertEquals(true, cartItems.stream().anyMatch(p -> p.getProduct().getProductCode().equalsIgnoreCase("Iphone X")));
        assertEquals(true, cartItems.stream().anyMatch(p -> p.getProduct().getProductCode().equalsIgnoreCase("Iphone charger")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testModifyTheCart() {
        Map productStock = shoppingService.showAllProducts();
        Set<Product> products = productStock.keySet();

        Product iphoneX = getProductByName("Iphone X", products);
        shoppingService.addToCart(iphoneX, 2);

        Product iphoneCharger = getProductByName("Iphone charger", products);
        shoppingService.addToCart(iphoneCharger, 1);
        Collection<Item> cartItems = shoppingService.showAllItemsInTheCart();
        cartItems.add(new Item(new Product(23, "FAKE PRODUCT", Category.MOBILE_PHONE, new BigDecimal(900)), 1));
    }

    @Test
    public void testReductionInStockAfterAdditionToCart() {
        Map beforeCartStock = shoppingService.showAllProducts();
        assertEquals(4, beforeCartStock.get(TestConstants.IPHONE_X));
        assertEquals(4, beforeCartStock.get(IPHONE_CHARGER));
        testAddToCart();
        Map afterCartStock = shoppingService.showAllProducts();
        assertEquals(2, afterCartStock.get(TestConstants.IPHONE_X));
        assertEquals(3, afterCartStock.get(IPHONE_CHARGER));
    }

    @Test
    public void testFailureForEligibilityToGetDiscount() {
        testAddToCart();
        assertEquals(false, shoppingService.isEligibleForDiscount());
    }

    @Test
    public void testDiscountAmountForFailedEligibilityToGetDiscount() {
        testAddToCart();
        assertEquals(0, shoppingService.getTotalDiscount().intValue());
    }

    @Test
    public void testSuccessForEligibilityToDiscount() {
        testAddToCart();
        Map productStock = shoppingService.showAllProducts();
        Set<Product> products = productStock.keySet();
        Product samsungCharger = getProductByName("Samsung charger", products);
        shoppingService.addToCart(samsungCharger, 1);
        assertEquals(3, shoppingService.showAllItemsInTheCart().size());
        assertEquals(true, shoppingService.isEligibleForDiscount());
    }

    @Test
    public void testDiscountAmountForEligibleCart() {
        testSuccessForEligibilityToDiscount();
        assertEquals(176, shoppingService.getTotalDiscount().intValue());
    }

    @Test
    public void testTotalPriceWithDiscount() {
        testSuccessForEligibilityToDiscount();
        int priceWithoutDiscount = shoppingService.getTotalPriceWithoutDiscount().intValue();
        int discountAmt = shoppingService.getTotalDiscount().intValue();
        int totalPrice = shoppingService.getTotalPrice().intValue();
        int discountPercentage = 10;
        assertEquals(1760, priceWithoutDiscount);
        assertEquals(176, discountAmt);
        assertEquals(1584, totalPrice);
        assertEquals(discountPercentage, (priceWithoutDiscount - totalPrice) * 100 / priceWithoutDiscount);
    }

    @Test
    public void testUpdateExistingCartByAddingQuantity() {
        testSuccessForEligibilityToDiscount();
        shoppingService.updateCart(IPHONE_CHARGER, 1);
        assertEquals(true, shoppingService.showAllItemsInTheCart().stream().anyMatch(p -> p.getProduct().equals(IPHONE_CHARGER) && p.getQuantity() == 2));
    }

    @Test
    public void testStockAfterUpdateQuantity() {
        testSuccessForEligibilityToDiscount();
        assertEquals(3, shoppingService.showAllProducts().get(IPHONE_CHARGER));
        shoppingService.updateCart(IPHONE_CHARGER, 1);
        assertEquals(2, shoppingService.showAllProducts().get(IPHONE_CHARGER));
    }

    @Test
    public void testUpdateExistingCartByReducingQuantity() {
        testUpdateExistingCartByAddingQuantity();
        shoppingService.updateCart(IPHONE_CHARGER, -1);
        assertEquals(true, shoppingService.showAllItemsInTheCart().stream().anyMatch(p -> p.getProduct().equals(IPHONE_CHARGER) && p.getQuantity() == 1));
    }

    @Test
    public void testStockAfterReduceQuantityInCart() {
        testSuccessForEligibilityToDiscount();
        assertEquals(3, shoppingService.showAllProducts().get(IPHONE_CHARGER));
        shoppingService.updateCart(IPHONE_CHARGER, -1);
        assertEquals(4, shoppingService.showAllProducts().get(IPHONE_CHARGER));
    }

    private Product getProductByName(String name, Set<Product> products) {
        return products.stream()
                .filter(p -> p.getProductCode().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }

}