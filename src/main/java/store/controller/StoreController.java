package store.controller;

import java.util.NoSuchElementException;
import store.error.PromotionConfirmationForFreeException;
import store.error.PurchaseConfirmationWithoutPromotionException;
import store.model.item.Inventory;
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
            OutputView.printItems(inventory.getItems());
            Order order = getOrder();
            printReceipt(order, isMembershipDiscountAccepted());
            if (isAdditionalPurchaseConfirmed()) {
                run();
            }
        } catch (NoSuchElementException e) {
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
                orderProcessor.processOrder(orderItem);
            } catch (PromotionConfirmationForFreeException e) {
                processPromotionConfirmationForFree(e);
            } catch (PurchaseConfirmationWithoutPromotionException e) {
                processPurchaseConfirmationWithoutPromotion(e);
            }
            orderProcessor.setPrice(orderItem);
        }
    }

    private void processPromotionConfirmationForFree(PromotionConfirmationForFreeException e) {
        while (true) {
            try {
                String userChoice = InputView.readPromotionConfirmationForFree(e.getItem().getName(),
                        e.getShortfall());
                inventory.parseUserChoiceForFree(userChoice, e.getItem(), e.getOrderItem(), e.getShortfall());
                break;
            } catch (IllegalArgumentException ex) {
                OutputView.printMessage(ex.getMessage());
            }
        }
    }

    private void processPurchaseConfirmationWithoutPromotion(PurchaseConfirmationWithoutPromotionException e) {
        while (true) {
            try {
                String userChoice = getUserChoice(e);
                inventory.parseUserChoiceWithoutPromotion(userChoice, e.getOrderItem(),
                        e.getRemainingPromotionQuantity(), e.getRemainingQuantity());
                break;
            } catch (IllegalArgumentException ex) {
                OutputView.printMessage(ex.getMessage());
            }
        }
    }

    private String getUserChoice(PurchaseConfirmationWithoutPromotionException e) {
        return InputView.readPurchaseConfirmationWithoutPromotion(e.getItemName(),
                Math.abs(e.getRemainingQuantity()));
    }

    private void printReceipt(Order order, boolean isMembership) {
        PaymentSummary paymentSummary = OrderCalculator.calculateAmounts(order.getOrderItems(), isMembership);
        OutputView.printReceipt(order, paymentSummary);
    }

    private boolean isMembershipDiscountAccepted() {
        while (true) {
            try {
                String userMembershipInput = InputView.readMembershipDiscountConfirmation();
                return userMembershipInput.equals("Y");
            } catch (IllegalArgumentException e) {
                OutputView.printMessage(e.getMessage());
            }
        }
    }

    private boolean isAdditionalPurchaseConfirmed() {
        String additionalPurchaseConfirmation = getAdditionalPurchaseConfirmation();
        return additionalPurchaseConfirmation.equals("Y");
    }

    private String getAdditionalPurchaseConfirmation() {
        while (true) {
            try {
                return InputView.readAdditionalPurchaseConfirmation();
            } catch (IllegalArgumentException e) {
                OutputView.printMessage(e.getMessage());
            }
        }
    }
}
