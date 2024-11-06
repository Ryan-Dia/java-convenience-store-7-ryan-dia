package store.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.promotion.Promotion;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
    }

    @Test
    void 제품들을_올바르게_초기화() throws IOException {
        // given
        List<String> expectedLines = Files.readAllLines(Paths.get("src/main/resources/products.md"));

        // when
        Items items = inventory.setItems();
        assertNotNull(items, "기본값이 있어야합니다.");

        List<Item> itemList = items.getItems();
        assertFalse(itemList.isEmpty(), "Item 리스트는 비어있습니다");

        // then
        for (int i = 0; i < itemList.size(); i++) {
            String[] expectedData = expectedLines.get(i + 1).split(",");
            Item item = itemList.get(i);

            assertEquals(expectedData[0], item.getName());
            assertEquals(Integer.parseInt(expectedData[1]), item.getPrice());
            assertEquals(Integer.parseInt(expectedData[2]), item.getQuantity());

            String expectedPromotionName = expectedData[3];
            if (expectedPromotionName.equals("null")) {
                expectedPromotionName = null;
            }
            Promotion promotion = item.getPromotion();
            if (expectedPromotionName == null) {
                assertNull(promotion);
            } else {
                assertNotNull(promotion);
                assertEquals(expectedPromotionName, promotion.getName());
            }
        }
    }
}
