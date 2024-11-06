package store.controller;

import store.model.Inventory;
import store.model.Items;
import store.view.OutputView;

public class StoreController {
    private final Inventory inventory;

    public StoreController(Inventory inventory) {
        this.inventory = inventory;
    }

    public void run() {
        Items items = inventory.setItems();
        OutputView.printItems(items);
    }
}
