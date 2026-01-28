package com.example.order.service;

import com.example.order.dto.AddAddressRequest;
import com.example.order.dto.CreateOrderFromProductRequest;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.DeleteOrderItemsRequest;
import com.example.order.dto.ProductResponse;
import com.example.order.entity.Address;
import com.example.order.entity.AddressType;
import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final String productServiceBaseUrl;
    private final String productServiceToken;

    public OrderService(
            OrderRepository orderRepository,
            RestTemplateBuilder restTemplateBuilder,
            @Value("${product.service.base-url:http://localhost:81}") String productServiceBaseUrl,
            @Value("${product.service.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${product.service.read-timeout-ms:5000}") int readTimeoutMs,
            @Value("${product.service.token:}") String productServiceToken
    ) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
        this.productServiceBaseUrl = normalizeBaseUrl(productServiceBaseUrl);
        this.productServiceToken = productServiceToken;
    }

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest req) {
        OrderEntity order = new OrderEntity();
        if (req.getOrder() != null) {
            order.setOrderTotal(req.getOrder().getOrderTotal());
            order.setItemTotal(req.getOrder().getItemTotal());
            order.setShippingTotal(req.getOrder().getShippingTotal());
            order.setPaymentMethod(req.getOrder().getPaymentMethod());
        }

        if (req.getOrderItems() != null) {
            for (CreateOrderRequest.OrderItemData it : req.getOrderItems()) {
                OrderItem item = new OrderItem();
                item.setSku(it.getSku());
                Double effectivePrice = it.getPrice() != null ? it.getPrice() : it.getUnitPrice();
                item.setUnitPrice(it.getUnitPrice() != null ? it.getUnitPrice() : it.getPrice());
                item.setPrice(effectivePrice);
                item.setQuantity(it.getQty());
                item.setProductName(it.getProductName());
                item.setCategoryId(it.getCategoryId());
                item.setCategoryName(it.getCategoryName());
                item.setBrandId(it.getBrandId());
                item.setBrandName(it.getBrandName());
                item.setShortDesc(it.getShortDesc());
                order.addItem(item);
            }
        }

        if (req.getAddress() != null) {
            for (CreateOrderRequest.AddressData a : req.getAddress()) {
                Address addr = new Address();
                addr.setFirstName(a.getFirstName());
                addr.setLastName(a.getLastName());
                addr.setAddress1(a.getAddress1());
                addr.setCity(a.getCity());
                addr.setCountry(a.getCountry());
                addr.setProvince(a.getProvince());
                addr.setZipCode(a.getZipCode());
                addr.setAddressType(resolveAddressType(a.getAddressType()));
                order.addAddress(addr);
            }
        }

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity createOrderFromProductRequest(CreateOrderFromProductRequest req) {
        OrderEntity order = new OrderEntity();
        double itemTotal = 0;
        boolean hasPrice = false;

        if (req.getOrderItems() != null) {
            for (CreateOrderFromProductRequest.OrderItemRequest it : req.getOrderItems()) {
                ProductResponse product = fetchProduct(it.getId());

                OrderItem item = new OrderItem();
                item.setProductId(it.getId());
                item.setSku(product.getSku());
                item.setProductName(product.getProductName());
                item.setCategoryId(product.getCategoryId());
                item.setCategoryName(product.getCategoryName());
                item.setBrandId(product.getBrandId());
                item.setBrandName(product.getBrandName());
                item.setPrice(product.getPrice());
                item.setUnitPrice(product.getPrice());
                item.setShortDesc(product.getShortDesc());
                item.setQuantity(it.getQty());
                order.addItem(item);

                if (product.getPrice() != null && it.getQty() != null) {
                    itemTotal += product.getPrice() * it.getQty();
                    hasPrice = true;
                }
            }
        }

        if (hasPrice) {
            order.setItemTotal(itemTotal);
            order.setOrderTotal(itemTotal);
            order.setShippingTotal(0.0);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity addItemsToOrder(String orderId, CreateOrderFromProductRequest req) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));

        if (req.getOrderItems() != null) {
            for (CreateOrderFromProductRequest.OrderItemRequest it : req.getOrderItems()) {
                ProductResponse product = fetchProduct(it.getId());
                OrderItem existing = findItemByProductId(order, it.getId());
                if (existing != null) {
                    Integer currentQty = existing.getQuantity() != null ? existing.getQuantity() : 0;
                    Integer addQty = it.getQty() != null ? it.getQty() : 0;
                    existing.setQuantity(currentQty + addQty);
                    applyProductDetails(existing, product, it.getId());
                } else {
                    OrderItem item = new OrderItem();
                    item.setQuantity(it.getQty());
                    applyProductDetails(item, product, it.getId());
                    order.addItem(item);
                }
            }
        }

        updateTotals(order);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity addAddressesToOrder(String orderId, List<AddAddressRequest> addresses) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));

        Map<AddressType, AddAddressRequest> byType = validateAddressPayload(addresses);
        for (var entry : byType.entrySet()) {
            AddressType type = entry.getKey();
            AddAddressRequest request = entry.getValue();
            boolean updated = false;
            for (Address existing : order.getAddresses()) {
                if (type.equals(existing.getAddressType())) {
                    applyAddressFields(existing, request, type);
                    updated = true;
                }
            }
            if (!updated) {
                order.addAddress(toAddress(request, type));
            }
        }

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity removeItemsFromOrder(String orderId, DeleteOrderItemsRequest req) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));

        if (req == null || req.getOrderItems() == null || req.getOrderItems().getIds() == null) {
            return order;
        }

        Set<Long> ids = new HashSet<>(req.getOrderItems().getIds());
        if (ids.isEmpty()) {
            return order;
        }

        boolean removed = false;
        for (var iterator = order.getOrderItems().iterator(); iterator.hasNext(); ) {
            OrderItem item = iterator.next();
            if (item.getId() != null && ids.contains(item.getId())) {
                item.setOrder(null);
                iterator.remove();
                removed = true;
            }
        }

        if (removed) {
            updateTotals(order);
        }

        return orderRepository.save(order);
    }

    public Optional<OrderEntity> findById(String id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Optional<OrderEntity> updateStatus(String id, String status) {
        Optional<OrderEntity> o = orderRepository.findById(id);
        o.ifPresent(order -> {
            order.setOrderStatus(status);
            orderRepository.save(order);
        });
        return o;
    }

    private ProductResponse fetchProduct(String productId) {
        String url = productServiceBaseUrl + "/products/" + productId;
        try {
            log.info("Fetching product {} from {}", productId, url);
            HttpHeaders headers = new HttpHeaders();
            if (productServiceToken != null && !productServiceToken.isBlank()) {
                headers.setBearerAuth(productServiceToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<ProductResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProductResponse.class
            );
            ProductResponse product = response.getBody();
            if (product == null) {
                log.warn("Product service returned null response for id {} at {}", productId, url);
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Product service returned empty response for id " + productId
                );
            }
            return product;
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Product not found for id {} at {}", productId, url);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId, ex);
        } catch (RestClientException ex) {
            log.error("Product service error for id {} at {}", productId, url, ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Product service error for id " + productId,
                    ex
            );
        }
    }

    private OrderItem findItemByProductId(OrderEntity order, String productId) {
        if (productId == null) {
            return null;
        }
        for (OrderItem item : order.getOrderItems()) {
            if (productId.equals(item.getProductId())) {
                return item;
            }
        }
        return null;
    }

    private void applyProductDetails(OrderItem item, ProductResponse product, String productId) {
        item.setProductId(productId);
        item.setSku(product.getSku());
        item.setProductName(product.getProductName());
        item.setCategoryId(product.getCategoryId());
        item.setCategoryName(product.getCategoryName());
        item.setBrandId(product.getBrandId());
        item.setBrandName(product.getBrandName());
        item.setPrice(product.getPrice());
        item.setUnitPrice(product.getPrice());
        item.setShortDesc(product.getShortDesc());
    }

    private void updateTotals(OrderEntity order) {
        double itemTotal = 0;
        boolean hasPrice = false;
        for (OrderItem item : order.getOrderItems()) {
            Double price = item.getPrice() != null ? item.getPrice() : item.getUnitPrice();
            Integer qty = item.getQuantity();
            if (price != null && qty != null) {
                itemTotal += price * qty;
                hasPrice = true;
            }
        }
        if (hasPrice) {
            order.setItemTotal(itemTotal);
            Double shipping = order.getShippingTotal();
            order.setOrderTotal(shipping != null ? itemTotal + shipping : itemTotal);
        }
    }

    private AddressType resolveAddressType(String addressType) {
        if (addressType == null || addressType.isBlank()) {
            return null;
        }
        try {
            return AddressType.valueOf(addressType);
        } catch (IllegalArgumentException ex) {
            return AddressType.valueOf(addressType.toLowerCase());
        }
    }

    private AddressType resolveAddressTypeRequired(String addressType) {
        if (addressType == null || addressType.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "address_type is required");
        }
        try {
            return AddressType.valueOf(addressType);
        } catch (IllegalArgumentException ex) {
            try {
                return AddressType.valueOf(addressType.toLowerCase());
            } catch (IllegalArgumentException inner) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid address_type: " + addressType
                );
            }
        }
    }

    private Map<AddressType, AddAddressRequest> validateAddressPayload(List<AddAddressRequest> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Address payload must include billing and shipping addresses"
            );
        }
        EnumMap<AddressType, AddAddressRequest> byType = new EnumMap<>(AddressType.class);
        for (AddAddressRequest request : addresses) {
            if (request == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address payload contains null entry");
            }
            AddressType type = resolveAddressTypeRequired(request.getAddressType());
            if (byType.containsKey(type)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate address type: " + type.name()
                );
            }
            byType.put(type, request);
        }
        if (!byType.containsKey(AddressType.billing) || !byType.containsKey(AddressType.shipping)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Address payload must include billing and shipping addresses"
            );
        }
        return byType;
    }

    private Address toAddress(AddAddressRequest request, AddressType type) {
        Address addr = new Address();
        applyAddressFields(addr, request, type);
        return addr;
    }

    private void applyAddressFields(Address target, AddAddressRequest request, AddressType type) {
        target.setFirstName(request.getFirstName());
        target.setLastName(request.getLastName());
        target.setAddress1(request.getAddress1());
        target.setCity(request.getCity());
        target.setCountry(request.getCountry());
        target.setProvince(request.getProvince());
        target.setZipCode(request.getZipCode());
        target.setAddressType(type);
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:81";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
