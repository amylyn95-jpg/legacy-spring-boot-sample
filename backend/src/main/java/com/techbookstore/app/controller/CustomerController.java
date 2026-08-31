package com.techbookstore.app.controller;

import com.techbookstore.app.dto.CustomerDto;
import com.techbookstore.app.dto.OrderDto;
import com.techbookstore.app.entity.Customer;
import com.techbookstore.app.entity.Order;
import com.techbookstore.app.service.CustomerService;
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
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * REST controller for customer management operations.
 */
@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class CustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
    private final CustomerService customerService;
    
    /**
     * Constructor injection for dependencies.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    /**
     * Creates a new customer.
     * 
     * @param request the customer creation request
     * @return the created customer
     */
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        logger.info("Creating new customer with email: {}", request.getEmail());
        
        Customer customer = mapRequestToCustomer(request);
        Customer savedCustomer = customerService.createCustomer(customer);
        return ResponseEntity.ok(new CustomerDto(savedCustomer));
    }
    
    /**
     * Updates an existing customer.
     * 
     * @param id the customer ID
     * @param request the customer update request
     * @return the updated customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable @Positive Long id, 
                                                     @Valid @RequestBody UpdateCustomerRequest request) {
        logger.info("Updating customer with ID: {}", id);
        
        Customer customer = mapRequestToCustomer(request);
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(new CustomerDto(updatedCustomer));
    }
    
    /**
     * Retrieves customers with optional filtering and pagination.
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param sort sort parameter (e.g., "name,asc")
     * @param keyword search keyword
     * @param customerType customer type filter
     * @param status customer status filter
     * @param startDate start date filter
     * @param endDate end date filter
     * @return page of customers
     */
    @GetMapping
    public ResponseEntity<Page<CustomerDto>> getCustomers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "name,asc") String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        
        Page<Customer> customers;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            logger.debug("Searching customers with keyword: {}", keyword);
            customers = customerService.searchCustomers(keyword.trim(), pageable);
        } else if (customerType != null || status != null || startDate != null || endDate != null) {
            logger.debug("Filtering customers with type: {}, status: {}, startDate: {}, endDate: {}", 
                    customerType, status, startDate, endDate);
            Customer.CustomerType typeFilter = customerType != null ? Customer.CustomerType.valueOf(customerType) : null;
            Customer.CustomerStatus statusFilter = status != null ? Customer.CustomerStatus.valueOf(status) : null;
            customers = customerService.findCustomersWithFilters(typeFilter, statusFilter, startDate, endDate, pageable);
        } else {
            logger.debug("Fetching all customers with page: {}, size: {}", page, size);
            customers = customerService.getAllCustomers(pageable);
        }
        
        Page<CustomerDto> customerDtos = customers.map(CustomerDto::new);
        return ResponseEntity.ok(customerDtos);
    }
    
    /**
     * Retrieves a specific customer by ID.
     * 
     * @param id the customer ID
     * @return the customer
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable @Positive Long id) {
        logger.debug("Fetching customer with ID: {}", id);
        
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(new CustomerDto(customer));
    }
    
    /**
     * Deletes a customer (logical delete).
     * 
     * @param id the customer ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable @Positive Long id) {
        logger.info("Deleting customer with ID: {}", id);
        
        customerService.deleteCustomer(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all orders for a specific customer.
     * 
     * @param id the customer ID
     * @return list of orders for the customer
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderDto>> getCustomerOrders(@PathVariable @Positive Long id) {
        logger.debug("Fetching orders for customer: {}", id);
        
        List<Order> orders = customerService.getCustomerOrders(id);
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }
    
    /**
     * Advanced customer search endpoint.
     * 
     * @param keyword search keyword
     * @param page page number
     * @param size page size
     * @return search results
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerDto>> searchCustomers(
            @RequestParam @NotBlank String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        logger.debug("Advanced search for customers with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Customer> customers = customerService.searchCustomers(keyword, pageable);
        Page<CustomerDto> customerDtos = customers.map(CustomerDto::new);
        
        return ResponseEntity.ok(customerDtos);
    }
    
    /**
     * Gets customer statistics.
     * 
     * @return customer statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCustomerStats() {
        logger.debug("Fetching customer statistics");
        
        Long totalCustomers = customerService.getCustomerCountByStatus(Customer.CustomerStatus.ACTIVE);
        Long inactiveCustomers = customerService.getCustomerCountByStatus(Customer.CustomerStatus.INACTIVE);
        Long deletedCustomers = customerService.getCustomerCountByStatus(Customer.CustomerStatus.DELETED);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", totalCustomers);
        stats.put("inactiveCustomers", inactiveCustomers);
        stats.put("deletedCustomers", deletedCustomers);
        stats.put("activeCustomers", totalCustomers);
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Maps a customer request DTO to a Customer entity with proper exception handling.
     * 
     * @param request the customer request
     * @return the mapped customer entity
     */
    private Customer mapRequestToCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        
        try {
            customer.setCustomerType(Customer.CustomerType.valueOf(request.getCustomerType()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid customer type: " + request.getCustomerType());
        }
        
        customer.setName(request.getName());
        customer.setNameKana(request.getNameKana());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setBirthDate(request.getBirthDate());
        
        if (request.getGender() != null && !request.getGender().trim().isEmpty()) {
            try {
                customer.setGender(Customer.Gender.valueOf(request.getGender()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid gender: " + request.getGender());
            }
        }
        
        customer.setOccupation(request.getOccupation());
        customer.setCompanyName(request.getCompanyName());
        customer.setDepartment(request.getDepartment());
        customer.setPostalCode(request.getPostalCode());
        customer.setAddress(request.getAddress());
        customer.setNotes(request.getNotes());
        
        return customer;
    }
    
    /**
     * Request DTO for creating customers.
     */
    public static class CreateCustomerRequest {
        
        @NotBlank(message = "Customer type is required")
        @Pattern(regexp = "INDIVIDUAL|CORPORATE", message = "Customer type must be INDIVIDUAL or CORPORATE")
        private String customerType;
        
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        private String name;
        
        @Size(max = 100, message = "Name kana must not exceed 100 characters")
        private String nameKana;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        private String email;
        
        @NotBlank(message = "Phone is required")
        @Size(max = 20, message = "Phone must not exceed 20 characters")
        private String phone;
        
        @Past(message = "Birth date must be in the past")
        private LocalDate birthDate;
        
        @Pattern(regexp = "MALE|FEMALE|OTHER|", message = "Gender must be MALE, FEMALE, OTHER, or empty")
        private String gender;
        
        @Size(max = 100, message = "Occupation must not exceed 100 characters")
        private String occupation;
        
        @Size(max = 100, message = "Company name must not exceed 100 characters")
        private String companyName;
        
        @Size(max = 100, message = "Department must not exceed 100 characters")
        private String department;
        
        @Pattern(regexp = "^\\d{3}-\\d{4}$|^\\d{7}$|^$", message = "Postal code must be in format XXX-XXXX or XXXXXXX")
        private String postalCode;
        
        private String address;
        private String notes;
        
        // Getters and Setters
        public String getCustomerType() { return customerType; }
        public void setCustomerType(String customerType) { this.customerType = customerType; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNameKana() { return nameKana; }
        public void setNameKana(String nameKana) { this.nameKana = nameKana; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getOccupation() { return occupation; }
        public void setOccupation(String occupation) { this.occupation = occupation; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    /**
     * Request DTO for updating customers.
     */
    public static class UpdateCustomerRequest extends CreateCustomerRequest {
        // Inherits all fields from CreateCustomerRequest
    }
}