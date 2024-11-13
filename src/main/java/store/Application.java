package store;

import store.controller.StoreController;
import store.model.item.Inventory;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        try {
            StoreController storeController = new StoreController(new Inventory());
            storeController.run();
        } catch (IllegalArgumentException e) {
            OutputView.printError(e.getMessage());
        }

    }
}
