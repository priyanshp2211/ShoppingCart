package com.aimms.assignment;

import com.aimms.assignment.Util.UtilityClass;
import com.aimms.assignment.model.Category;
import com.aimms.assignment.model.Item;
import com.aimms.assignment.model.Product;
import com.aimms.assignment.model.ProductCatalogue;
import com.aimms.assignment.service.ShoppingService;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

public class ApplicationMain {

    private static final Logger log = LoggerFactory.getLogger(ApplicationMain.class);

    private ShoppingService shoppingService;

    private ApplicationMain() throws Exception {
        String fileName = "product.csv";
        try {
            CSVReader reader = new CSVReader(new FileReader(UtilityClass.getFileFromResources(fileName)));
            String[] headers = reader.readNext();
            String[] nextLine;
            List<Product> productList = new ArrayList<>();
            while ((nextLine = reader.readNext()) != null) {
                productList.add(new Product(Long.parseLong(nextLine[0].trim()), nextLine[1].trim(), Category.valueOf(nextLine[2].trim()), new BigDecimal(nextLine[3].trim())));
            }
            HashMap<Product, Integer> productStock = new HashMap<>();
            Random randomGenerator = new Random();
            for (Product product : productList) {
                productStock.put(product, randomGenerator.nextInt(10) + 2);
            }
            ProductCatalogue productCatalogue = new ProductCatalogue(productStock);
            shoppingService = new ShoppingService(productCatalogue);
        } catch (Exception exe) {
            throw new Exception("Setup Failed Due To File Failure");
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationMain instance = new ApplicationMain();
        ShoppingService service = instance.getShoppingService();
        //Step 1: Show All Products
        log.info("****SHOW ALL PRODUCTS ****");
        Map<Product, Integer> productStock = service.showAllProducts();
        //Negative Test - Try to modify the product list
        try {
            log.info("***Negative test to modify the product catalogue****");
            productStock.put(new Product(23, "FAKE PRODUCT", Category.MOBILE_PHONE, new BigDecimal(900)), 10);
            log.info("Result :: able to modify the product catalogue");
        } catch (Exception exe) {
            log.error("Result :: Unable to modify the product catalogue");
        }

        // Step 2: Add 2 Different Products on the Cart
        log.info("*** 2 Products added to the cart****");
        Set<Product> products = productStock.keySet();
        Product iphoneX = products.stream()
                .filter(p -> p.getProductCode().equalsIgnoreCase("Iphone X"))
                .findAny()
                .orElse(null);
        service.addToCart(iphoneX, 2);

        Product iphoneCharger = products.stream()
                .filter(p -> p.getProductCode().equalsIgnoreCase("Iphone charger"))
                .findAny()
                .orElse(null);
        service.addToCart(iphoneCharger, 1);
        service.showAllProducts();
        Collection<Item> cartItems = service.showAllItemsInTheCart();
        //Negative Test - Try to modify cart collection
        try {
            log.info("****Negative test to modify the cart****");
            cartItems.add(new Item(new Product(23, "FAKE PRODUCT", Category.MOBILE_PHONE, new BigDecimal(900)), 1));
            log.info("Result :: able to modify the cart illegally");
        } catch (Exception exe) {
            log.error("Result :: Unable to modify the cart illegally");
        }

        //Step 3: Check discount and price
        log.info("****Check discount and price****");
        service.isEligibleForDiscount();
        service.getTotalPrice();

        //Step 4: Add another product to qualify for discount && check price
        log.info("****Add another product to qualify for discount && check price****");
        Product samsungCharger = products.stream()
                .filter(p -> p.getProductCode().equalsIgnoreCase("samsung charger"))
                .findAny()
                .orElse(null);
        service.addToCart(samsungCharger, 1);
        service.showAllProducts();
        service.showAllItemsInTheCart();
        service.isEligibleForDiscount();
        service.getTotalPrice();

        //Update Cart Quantity
        log.info("****Update Cart Quantity****");
        service.updateCart(iphoneCharger, 1);
        service.showAllProducts();
        service.showAllItemsInTheCart();
        service.isEligibleForDiscount();
        service.getTotalPrice();

        //Reduce Qty in the cart
        log.info("****Reduce Qty in the cart****");
        service.updateCart(iphoneCharger, -1);
        service.showAllProducts();
        service.showAllItemsInTheCart();
    }

    private ShoppingService getShoppingService() {
        return shoppingService;
    }
}
