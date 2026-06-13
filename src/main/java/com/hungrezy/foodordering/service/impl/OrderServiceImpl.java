package com.hungrezy.foodordering.service.impl;

import com.hungrezy.foodordering.dto.request.PlaceOrderRequest;
import com.hungrezy.foodordering.dto.response.OrderResponse;
import com.hungrezy.foodordering.entity.MenuItem;
import com.hungrezy.foodordering.entity.Order;
import com.hungrezy.foodordering.entity.OrderItem;
import com.hungrezy.foodordering.entity.Restaurant;
import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.entity.enums.OrderStatus;
import com.hungrezy.foodordering.exception.BadRequestException;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.MenuItemRepository;
import com.hungrezy.foodordering.repository.OrderRepository;
import com.hungrezy.foodordering.repository.RestaurantRepository;
import com.hungrezy.foodordering.repository.UserRepository;
import com.hungrezy.foodordering.service.OrderService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final DtoMapper mapper;

    public OrderServiceImpl(UserRepository userRepository, RestaurantRepository restaurantRepository,
                            MenuItemRepository menuItemRepository, OrderRepository orderRepository,
                            SimpMessagingTemplate messagingTemplate, DtoMapper mapper) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(String userEmail, PlaceOrderRequest request) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(mapper.toAddress(request.getOrderAddress()));
        order.setPaymentMethod(request.getOrderPaymentMethod());
        order.setOrderedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (PlaceOrderRequest.OrderLineRequest line : request.getOrderItems()) {
            MenuItem menuItem = resolveMenuItem(restaurant.getId(), line);
            if (!restaurant.getId().equals(menuItem.getRestaurant().getId())) {
                throw new BadRequestException("Menu item does not belong to restaurant");
            }
            if (!Boolean.TRUE.equals(menuItem.getAvailable())) {
                throw new BadRequestException(menuItem.getName() + " is unavailable");
            }
            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
            OrderItem item = new OrderItem();
            item.setMenuItem(menuItem);
            item.setItemName(menuItem.getName());
            item.setQuantity(line.getQuantity());
            item.setUnitPrice(menuItem.getPrice());
            item.setSubtotal(subtotal);
            order.addItem(item);
            total = total.add(subtotal);
        }
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        OrderResponse response = mapper.toOrderResponse(saved);
        messagingTemplate.convertAndSend("/topic/orders/" + saved.getId(), response);
        return response;
    }

    @Override
    public List<OrderResponse> myOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserIdOrderByOrderedAtDesc(user.getId()).stream().map(mapper::toOrderResponse).toList();
    }

    @Override
    public OrderResponse track(UUID orderId) {
        return mapper.toOrderResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        OrderResponse response = mapper.toOrderResponse(order);
        messagingTemplate.convertAndSend("/topic/orders/" + order.getId(), response);
        return response;
    }

    private MenuItem resolveMenuItem(UUID restaurantId, PlaceOrderRequest.OrderLineRequest line) {
        UUID menuItemId = line.getMenuItemIdOrId();
        if (menuItemId != null) {
            return menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        }
        String name = line.getName();
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId).stream()
                .filter(menuItem -> menuItem.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + name));
    }
}
