package kitchenpos.order.application;

import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.dto.OrderLineItemRequest;
import kitchenpos.order.domain.Order;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.order.dto.OrderResponse;
import kitchenpos.order.exception.EmptyOrderException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;

    public OrderService(final OrderRepository orderRepository, final OrderValidator orderValidator) {
        this.orderRepository = orderRepository;
        this.orderValidator = orderValidator;
    }

    @Transactional
    public OrderResponse create(final OrderRequest request) {
        final Order order = Order.from(request.getOrderTableId());
        addOrderLineItems(order, request.getOrderLineItems());

        final Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    private void addOrderLineItems(final Order order, final List<OrderLineItemRequest> orderLineItemRequests) {
        final List<OrderLineItem> orderLineItems = orderLineItemRequests.stream()
                .map(this::getOrderItem)
                .collect(Collectors.toList());
        order.addOrderLineItems(orderLineItems);
    }

    private OrderLineItem getOrderItem(final OrderLineItemRequest request) {
        orderValidator.validatorMenu(request);
        return request.toOrderLineItem();
    }

    public List<OrderResponse> list() {
        final List<Order> orders = orderRepository.findAll();
        return OrderResponse.from(orders);
    }

    @Transactional
    public OrderResponse changeOrderStatus(final Long orderId, final OrderRequest orderRequest) {
        final Order order = findById(orderId);
        order.updateStatus(orderRequest.getOrderStatus());
        return OrderResponse.from(order);
    }

    private Order findById(final Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(EmptyOrderException::new);
    }

    public List<Order> findByOrderTableIdIn(final List<Long> orderTableIds) {
        return orderRepository.findByOrderTableIdIn(orderTableIds);
    }

    public Order findByOrderTableId(final Long orderTableId) {
        return orderRepository.findByOrderTableId(orderTableId);
    }
}