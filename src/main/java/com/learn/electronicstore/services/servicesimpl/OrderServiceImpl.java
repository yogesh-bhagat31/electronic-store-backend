package com.learn.electronicstore.services.servicesimpl;

import com.learn.electronicstore.dtos.*;
import com.learn.electronicstore.entities.*;
import com.learn.electronicstore.enums.OrderStatus;
import com.learn.electronicstore.enums.PaymentStatus;
import com.learn.electronicstore.exceptions.BadApiRequestException;
import com.learn.electronicstore.exceptions.ResourceNotFoundException;
import com.learn.electronicstore.helper.Helper;
import com.learn.electronicstore.repositories.CartRepository;
import com.learn.electronicstore.repositories.CategoryRepository;
import com.learn.electronicstore.repositories.OrderRepository;
import com.learn.electronicstore.repositories.UserRepository;
import com.learn.electronicstore.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private CategoryRepository categoryRepository;
    private Helper helper;

    public OrderServiceImpl(CartRepository cartRepository, OrderRepository orderRepository, UserRepository userRepository, ModelMapper modelMapper, CategoryRepository categoryRepository, Helper helper) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
        this.helper = helper;
    }

    @Override
    public OrderDto createOrder(CreateOrderRequest orderDto) {

        User user = userRepository.findById(orderDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.size() <= 0)
            throw new BadApiRequestException("Empty cart! Please add items in cart");

        Order order = Order.builder()
                .billingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .billingAddress(orderDto.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(null)
                .paymentStatus(orderDto.getPaymentStatus() != null ? orderDto.getPaymentStatus() : PaymentStatus.PENDING)
                .orderStatus(orderDto.getOrderStatus() != null ? orderDto.getOrderStatus() : OrderStatus.PENDING)
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();

        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountPrice())
                    .order(order)
                    .build();
            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());
        cart.getCartItems().clear();
        cartRepository.save(cart);
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getOrdersOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Order> orders = orderRepository.findByUser(user);
        List<OrderDto> orderDtos = orders.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
        return orderDtos;
    }

    @Override
    public PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> page = orderRepository.findAll(pageable);
        PageableResponse<OrderDto> pageableResponse = helper.getPageableResponse(page, OrderDto.class);
        return pageableResponse;
    }

    @Override
    public OrderDto confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getOrderStatus() == OrderStatus.PENDING)
            order.setOrderStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public OrderDto updateOrderStatus(String orderId, UpdateOrderStatusRequest newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Map<OrderStatus, List<OrderStatus>> validTransitions = Map.of(
                OrderStatus.PENDING, List.of(OrderStatus.CONFIRMED, OrderStatus.CANCELED),
                OrderStatus.CONFIRMED, List.of(OrderStatus.SHIPPED, OrderStatus.CANCELED),
                OrderStatus.SHIPPED, List.of(OrderStatus.DELIVERED),
                OrderStatus.DELIVERED, List.of(),
                OrderStatus.CANCELED, List.of()
        );

        if (!validTransitions.get(order.getOrderStatus()).contains(newStatus.getNewStatus())) {
            throw new ResourceNotFoundException("Invalid status transition");
        }

        order.setOrderStatus(newStatus.getNewStatus());
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderDto.class);
    }


}
