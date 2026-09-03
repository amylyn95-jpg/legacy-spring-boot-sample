package com.techbookstore.app.controller;

import com.techbookstore.app.dto.OrderDto;
import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.Order;
import com.techbookstore.app.entity.OrderItem;
import com.techbookstore.app.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for order management operations.
 */
@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    
    /**
     * Constructor injection for dependencies.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Creates a new order.
     * 
     * @param request the order creation request
     * @return the created order
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("Creating new order for customer: {}", request.getCustomerId());
        
        Order order = new Order(request.getType(), request.getPaymentMethod());
        order.setCustomerId(request.getCustomerId());
        order.setNotes(request.getNotes());
        
        // Convert request items to OrderItems
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            Book book = new Book();
            book.setId(itemRequest.getBookId());
            orderItem.setBook(book);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(new OrderDto(savedOrder));
    }
    
    /**
     * Retrieves orders with optional filtering and pagination.
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDir sort direction (asc/desc)
     * @param status filter by order status
     * @param type filter by order type
     * @param customerId filter by customer ID
     * @param startDate filter by start date
     * @param endDate filter by end date
     * @param keyword search keyword
     * @return page of orders
     */
    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword) {
        
        logger.debug("Fetching orders - page: {}, size: {}, keyword: {}", page, size, keyword);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = orderService.searchOrders(keyword.trim(), pageable);
        } else {
            Order.OrderStatus orderStatus = status != null ? Order.OrderStatus.valueOf(status) : null;
            Order.OrderType orderType = type != null ? Order.OrderType.valueOf(type) : null;
            orders = orderService.getOrdersWithFilters(orderStatus, orderType, customerId, startDate, endDate, pageable);
        }
        
        Page<OrderDto> orderDtos = orders.map(OrderDto::new);
        return ResponseEntity.ok(orderDtos);
    }
    
    /**
     * Retrieves a specific order by ID.
     * 
     * @param id the order ID
     * @return the order if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable @Positive Long id) {
        logger.debug("Fetching order with ID: {}", id);
        
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(new OrderDto(order.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Retrieves a specific order by order number.
     * 
     * @param orderNumber the order number
     * @return the order if found
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderDto> getOrderByNumber(@PathVariable @NotBlank String orderNumber) {
        logger.debug("Fetching order with number: {}", orderNumber);
        
        Optional<Order> order = orderService.getOrderByNumber(orderNumber);
        if (order.isPresent()) {
            return ResponseEntity.ok(new OrderDto(order.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Retrieves all orders for a specific customer.
     * 
     * @param customerId the customer ID
     * @return list of orders for the customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(@PathVariable @Positive Long customerId) {
        logger.debug("Fetching orders for customer: {}", customerId);
        
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }
    
    /**
     * Confirms an order and reserves inventory.
     * 
     * @param id the order ID
     * @return the confirmed order
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderDto> confirmOrder(@PathVariable @Positive Long id) {
        logger.info("Confirming order: {}", id);
        
        Order order = orderService.confirmOrder(id);
        return ResponseEntity.ok(new OrderDto(order));
    }
    
    /**
     * Marks an order as being picked.
     * 
     * @param id the order ID
     * @return the updated order
     */
    @PostMapping("/{id}/pick")
    public ResponseEntity<OrderDto> pickOrder(@PathVariable @Positive Long id) {
        logger.info("Marking order {} as picking", id);
        
        Order order = orderService.updateOrderStatus(id, Order.OrderStatus.PICKING);
        return ResponseEntity.ok(new OrderDto(order));
    }
    
    /**
     * Marks an order as shipped.
     * 
     * @param id the order ID
     * @return the updated order
     */
    @PostMapping("/{id}/ship")
    public ResponseEntity<OrderDto> shipOrder(@PathVariable @Positive Long id) {
        logger.info("Marking order {} as shipped", id);
        
        Order order = orderService.updateOrderStatus(id, Order.OrderStatus.SHIPPED);
        return ResponseEntity.ok(new OrderDto(order));
    }
    
    /**
     * Marks an order as delivered.
     * 
     * @param id the order ID
     * @return the updated order
     */
    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderDto> deliverOrder(@PathVariable @Positive Long id) {
        logger.info("Marking order {} as delivered", id);
        
        Order order = orderService.updateOrderStatus(id, Order.OrderStatus.DELIVERED);
        return ResponseEntity.ok(new OrderDto(order));
    }
    
    /**
     * Retrieves order status counts for dashboard.
     * 
     * @return status count statistics
     */
    @GetMapping("/stats/status-count")
    public ResponseEntity<StatusCountResponse> getOrderStatusCounts() {
        logger.debug("Fetching order status counts");
        
        StatusCountResponse response = new StatusCountResponse();
        response.setPending(orderService.getOrderCountByStatus(Order.OrderStatus.PENDING));
        response.setConfirmed(orderService.getOrderCountByStatus(Order.OrderStatus.CONFIRMED));
        response.setPicking(orderService.getOrderCountByStatus(Order.OrderStatus.PICKING));
        response.setShipped(orderService.getOrderCountByStatus(Order.OrderStatus.SHIPPED));
        response.setDelivered(orderService.getOrderCountByStatus(Order.OrderStatus.DELIVERED));
        response.setCancelled(orderService.getOrderCountByStatus(Order.OrderStatus.CANCELLED));
        return ResponseEntity.ok(response);
    }
    
    // Request DTOs
    
    /**
     * Request DTO for creating orders.
     */
    public static class CreateOrderRequest {
        @NotNull(message = "Customer ID is required")
        private Long customerId;
        
        @NotNull(message = "Order type is required")
        private Order.OrderType type;
        
        @NotNull(message = "Payment method is required")
        private Order.PaymentMethod paymentMethod;
        
        private String notes;
        
        @NotEmpty(message = "Order must have at least one item")
        private List<OrderItemRequest> orderItems;
        
        /**
         * Request DTO for order items.
         */
        public static class OrderItemRequest {
            @NotNull(message = "Book ID is required")
            private Long bookId;
            
            @NotNull(message = "Quantity is required")
            @Positive(message = "Quantity must be positive")
            private Integer quantity;
            
            public Long getBookId() { return bookId; }
            public void setBookId(Long bookId) { this.bookId = bookId; }
            public Integer getQuantity() { return quantity; }
            public void setQuantity(Integer quantity) { this.quantity = quantity; }
        }
        
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public Order.OrderType getType() { return type; }
        public void setType(Order.OrderType type) { this.type = type; }
        public Order.PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(Order.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public List<OrderItemRequest> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItemRequest> orderItems) { this.orderItems = orderItems; }
    }
    
    /**
     * Response DTO for order status counts.
     */
    public static class StatusCountResponse {
        private Long pending;
        private Long confirmed;
        private Long picking;
        private Long shipped;
        private Long delivered;
        private Long cancelled;
        
        public Long getPending() { return pending; }
        public void setPending(Long pending) { this.pending = pending; }
        public Long getConfirmed() { return confirmed; }
        public void setConfirmed(Long confirmed) { this.confirmed = confirmed; }
        public Long getPicking() { return picking; }
        public void setPicking(Long picking) { this.picking = picking; }
        public Long getShipped() { return shipped; }
        public void setShipped(Long shipped) { this.shipped = shipped; }
        public Long getDelivered() { return delivered; }
        public void setDelivered(Long delivered) { this.delivered = delivered; }
        public Long getCancelled() { return cancelled; }
        public void setCancelled(Long cancelled) { this.cancelled = cancelled; }
    }
}