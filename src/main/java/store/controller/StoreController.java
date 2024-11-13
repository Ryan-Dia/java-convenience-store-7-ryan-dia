package store.controller;

import java.util.List;
import java.util.NoSuchElementException;
import store.dto.ItemDto;
import store.dto.OrderDto;
import store.dto.OrderItemDto;
import store.error.PromotionConfirmationForFreeException;
import store.error.PurchaseConfirmationWithoutPromotionException;
import store.model.Answer;
import store.model.item.Inventory;
import store.model.item.Item;
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
            printItems(inventory.getItems());
            Order order = getOrder();
            printReceipt(order, isMembershipDiscountAccepted());
            if (isAdditionalPurchaseConfirmed()) {
                run();
            }
        } catch (NoSuchElementException e) {
            OutputView.printError(e.getMessage());
        }
    }

    private void printItems(List<Item> items) {
        List<ItemDto> itemsDTO = items.stream()
                .map(ItemDto::new)
                .toList();
        OutputView.printItems(itemsDTO);
    }

    private Order getOrder() {
        while (true) {
            try {
                String userOrder = InputView.readItem();
                Order order = new Order(userOrder);
                fulfillOrder(order);
                return order;
            } catch (IllegalArgumentException e) {
                OutputView.printError(e.getMessage());
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
                String userChoice = InputView.readPromotionConfirmationForFree(e.getItem().getName(), e.getShortfall());
                Answer.validate(userChoice);
                inventory.parseUserChoiceForFree(userChoice, e.getItem(), e.getOrderItem(), e.getShortfall());
                break;
            } catch (IllegalArgumentException ex) {
                OutputView.printError(ex.getMessage());
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
                OutputView.printError(ex.getMessage());
            }
        }
    }

    private String getUserChoice(PurchaseConfirmationWithoutPromotionException e) {
        String userChoice = InputView.readPurchaseConfirmationWithoutPromotion(e.getItemName(),
                Math.abs(e.getRemainingQuantity()));
        Answer.validate(userChoice);
        return userChoice;
    }

    private void printReceipt(Order order, boolean isMembership) {
        PaymentSummary paymentSummary = OrderCalculator.calculateAmounts(order.getOrderItems(), isMembership);
        List<OrderItemDto> orderItems = order.getOrderItems().stream().map(OrderItemDto::new).toList();
        OutputView.printReceipt(new OrderDto(orderItems), paymentSummary);
    }

    private boolean isMembershipDiscountAccepted() {
        while (true) {
            try {
                String userMembershipInput = InputView.readMembershipDiscountConfirmation();
                Answer.validate(userMembershipInput);
                return Answer.YES.isEqual(userMembershipInput);
            } catch (IllegalArgumentException e) {
                OutputView.printError(e.getMessage());
            }
        }
    }

    private boolean isAdditionalPurchaseConfirmed() {
        String additionalPurchaseConfirmation = getAdditionalPurchaseConfirmation();
        return Answer.YES.isEqual(additionalPurchaseConfirmation);
    }

    private String getAdditionalPurchaseConfirmation() {
        while (true) {
            try {
                String userInput = InputView.readAdditionalPurchaseConfirmation();
                Answer.validate(userInput);
                return userInput;
            } catch (IllegalArgumentException e) {
                OutputView.printError(e.getMessage());
            }
        }
    }
}
