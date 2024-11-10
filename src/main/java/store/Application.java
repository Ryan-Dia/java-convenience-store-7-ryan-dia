package store;

import store.controller.StoreController;
import store.model.item.Inventory;

public class Application {
    public static void main(String[] args) {
        StoreController storeController = new StoreController(new Inventory());
        storeController.run();
    }
}
