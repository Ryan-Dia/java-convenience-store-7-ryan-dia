package store.controller;

import store.error.PromotionConfirmationForFreeException;
import store.error.PurchaseConfirmationWithoutPromotionException;
import store.model.Inventory;
import store.model.Items;
import store.model.order.Order;
import store.model.order.OrderCalculator;
import store.model.order.OrderItem;
import store.model.order.OrderProcessor;
import store.model.order.PaymentSummary;
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
            Order order = getOrder();
            String userMembershipInput = confirmMembershipDiscount();
            boolean isMembership = userMembershipInput.equals("Y");
            PaymentSummary paymentSummary = OrderCalculator.calculateAmounts(order.getOrderItems(), isMembership);
            OutputView.printReceipt(order, paymentSummary);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private Order getOrder() {
        while (true) {
            try {
                String userOrder = InputView.readItem();
                Order order = new Order(userOrder);
                fulfillOrder(order);
                return order;
            } catch (IllegalArgumentException e) {
                OutputView.printMessage(e.getMessage());
            }
        }
    }

    private void fulfillOrder(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            try {
                orderProcessor.processOrderForItem(orderItem);
                orderProcessor.setPrice(orderItem);
            } catch (PromotionConfirmationForFreeException e) {
                processPromotionConfirmationForFree(e);
                orderProcessor.setPrice(orderItem);
            } catch (PurchaseConfirmationWithoutPromotionException e) {
                processPurchaseConfirmationWithoutPromotion(e);
                orderProcessor.setPrice(orderItem);
            }
        }
    }

    private void processPromotionConfirmationForFree(PromotionConfirmationForFreeException e) {
        while (true) {
            try {
                String userChoice = InputView.readPromotionConfirmationForFree(e.getItem().getName(),
                        e.getShortfall());
                inventory.parseUserChoice(userChoice, e.getItem(), e.getOrderItem(), e.getShortfall());
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
                        Math.abs(e.getRemainingQuantity()));
                orderProcessor.parseUserChoice(userChoice, e.getItemName(), e.getRemainingQuantity(),
                        e.getRemainingPromotionQuantity(),
                        e.getOrderItem());
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
