package store.dto;

import java.util.List;

public record OrderDto(List<OrderItemDto> items) {
}
