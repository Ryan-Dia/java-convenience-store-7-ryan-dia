package store.controller;

import store.model.Inventory;
import store.model.Items;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {
    private final Inventory inventory;

    public StoreController(Inventory inventory) {
        this.inventory = inventory;
    }

    public void run() {
        Items items = inventory.setItems();
        OutputView.printItems(items);
        String order = getOrder();
        System.out.println(order);
    }


    private String getOrder() {
        try {
            String userOrder = InputView.readItem();
            return userOrder;
        } catch (IllegalArgumentException e) {
            OutputView.printMessage(e.getMessage());
            return getOrder();
        }


    }
}
