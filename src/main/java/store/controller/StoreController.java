package store.controller;

import java.util.List;
import store.error.PromotionConfirmationForFreeException;
import store.error.PurchaseConfirmationWithoutPromotionException;
import store.model.Inventory;
import store.model.Items;
import store.model.order.Order;
import store.model.order.OrderItem;
import store.model.order.OrderProcessor;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {
    private final Inventory inventory;
    private final OrderProcessor orderProcessor;

    public StoreController(Inventory inventory) {
        this.inventory = inventory;
        this.orderProcessor = new OrderProcessor(inventory);
    }

    public void run() {
        try {
            Items items = inventory.setItems();
            OutputView.printItems(items);
            getOrder();
            confirmMembershipDiscount();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void getOrder() {
        while (true) {
            try {
                String userOrder = InputView.readItem();
                Order order = new Order(userOrder);
                fulfillOrder(order.getOrderItems());
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printMessage(e.getMessage());
            }
        }
    }

    private void fulfillOrder(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            try {
                orderProcessor.processOrderForItem(orderItem.getName(), orderItem.getQuantity());
            } catch (PromotionConfirmationForFreeException e) {
                processPromotionConfirmationForFree(e);
            } catch (PurchaseConfirmationWithoutPromotionException e) {
                processPurchaseConfirmationWithoutPromotion(e);
            }
        }
    }

    private void processPromotionConfirmationForFree(PromotionConfirmationForFreeException e) {
        while (true) {
            try {
                String userChoice = InputView.readPromotionConfirmationForFree(e.getItem().getName(),
                        e.getShortfall());
                inventory.parseUserChoice(userChoice, e.getItem(), e.getOrderQuantity(), e.getShortfall());
                break;
            } catch (IllegalArgumentException ex) {
                OutputView.printMessage(ex.getMessage());
            }
        }
    }

    private void processPurchaseConfirmationWithoutPromotion(PurchaseConfirmationWithoutPromotionException e) {
        while (true) {
            try {
                String userChoice = InputView.readPurchaseConfirmationWithoutPromotion(e.getItemName(),
                        e.getWithoutPromoQuantity());
                orderProcessor.parseUserChoice(userChoice, e.getItemName(), e.getWithoutPromoQuantity(),
                        e.getRemainingPromoQuantity());
                break;
            } catch (IllegalArgumentException ex) {
                OutputView.printMessage(ex.getMessage());
            }
        }
    }

    private String confirmMembershipDiscount() {
        while (true) {
            try {
                return InputView.readMembershipDiscountConfirmation();
            } catch (IllegalArgumentException e) {
                OutputView.printMessage(e.getMessage());
            }
        }
    }
}
