//package com.agrowmart.controller;
////
////import com.agrowmart.dto.auth.order.OrderCancelRequestDTO;
////import com.agrowmart.dto.auth.order.OrderRequestDTO;
////import com.agrowmart.dto.auth.order.OrderResponseDTO;
////import com.agrowmart.entity.User;
////import com.agrowmart.service.OrderService;
////
////import jakarta.validation.Valid;
////
////import org.springframework.http.ResponseEntity;
////import org.springframework.security.access.prepost.PreAuthorize;
////import org.springframework.security.core.annotation.AuthenticationPrincipal;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.List;
////
////@RestController
////@RequestMapping("/api/orders")
////
////public class OrderController {
////
////    private final OrderService orderService;
////
////  public OrderController(OrderService orderService) {
////  this.orderService = orderService;
////}
////    
////    // 1. Customer places order
////    @PostMapping("/create")
////    @PreAuthorize("hasRole('CUSTOMER')")
////    public ResponseEntity<OrderResponseDTO> createOrder(
////            @AuthenticationPrincipal User customer,
////            @RequestBody OrderRequestDTO request) {
////        return ResponseEntity.status(201).body(orderService.createOrder(customer, request));
////    }
////
////    // 2. Customer views their orders
////    @GetMapping("/customer")
////    @PreAuthorize("hasRole('CUSTOMER')")
////    public ResponseEntity<List<OrderResponseDTO>> getCustomerOrders(@AuthenticationPrincipal User customer) {
////        return ResponseEntity.ok(orderService.getCustomerOrders(customer));
////    }
////
////    // 3. Vendor sees pending orders
////    @GetMapping("/vendor/pending")
////    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
////    public ResponseEntity<List<OrderResponseDTO>> getVendorPendingOrders(@AuthenticationPrincipal User vendor) {
////        return ResponseEntity.ok(orderService.getVendorPendingOrders(vendor));
////    }
////
////    // 4. Vendor accepts order
////    @PostMapping("/accept/{orderId}")
////    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
////    public ResponseEntity<OrderResponseDTO> acceptOrder(
////            @PathVariable String orderId,
////            @AuthenticationPrincipal User vendor) {
////        return ResponseEntity.ok(orderService.acceptOrder(orderId, vendor));
////    }
////
////    // 5. Vendor rejects order
////    @PostMapping("/reject/{orderId}")
////    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
////    public ResponseEntity<OrderResponseDTO> rejectOrder(
////            @PathVariable String orderId,
////            @AuthenticationPrincipal User vendor) {
////        return ResponseEntity.ok(orderService.rejectOrder(orderId, vendor));
////    }
////
////    // 6. NEW: Vendor (or delivery boy) marks order as delivered
//////    @PostMapping("/delivered/{orderId}")
//////    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
//////    public ResponseEntity<OrderResponseDTO> markAsDelivered(
//////            @PathVariable Long orderId,
//////            @AuthenticationPrincipal User actor) {
//////        return ResponseEntity.ok(orderService.markAsDelivered(orderId, actor));
//////    }
////
////    // 7. Vendor sees all their orders (history)
////    @GetMapping("/vendor/all")
////    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
////    public ResponseEntity<List<OrderResponseDTO>> getAllVendorOrders(@AuthenticationPrincipal User vendor) {
////        return ResponseEntity.ok(orderService.getAllVendorOrders(vendor));
////    }
////    
////    @PostMapping("/cancel/{orderId}")
////    @PreAuthorize("hasRole('CUSTOMER')")
////    public ResponseEntity<OrderResponseDTO> cancelOrder(
////            @PathVariable String orderId,
////            @AuthenticationPrincipal User customer,
////            @RequestBody OrderCancelRequestDTO request
////    ) {
////        return ResponseEntity.ok(
////                orderService.cancelOrderByCustomer(orderId, customer, request.reason())
////        );
////    }
////
////    @PostMapping("/vendor/cancel/{orderId}")
////    @PreAuthorize("hasAnyRole('VEGETABLE','DAIRY','SEAFOODMEAT','WOMEN','FARMER')")
////    public ResponseEntity<OrderResponseDTO> vendorCancelOrder(
////            @PathVariable String orderId,
////            @AuthenticationPrincipal User vendor,
////            @RequestBody @Valid OrderCancelRequestDTO request
////    ) {
////        return ResponseEntity.ok(
////            orderService.cancelOrderByVendor(orderId, vendor, request.reason())
////        );
////    }
////
////
////
////    
////    // NEW: Vendor confirms COD collected (after delivery)
////    @PostMapping("/cod-collected/{orderId}")
////    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
////    public ResponseEntity<OrderResponseDTO> confirmCodCollected(
////            @PathVariable Long orderId,
////            @AuthenticationPrincipal User vendor) {
////        return ResponseEntity.ok(orderService.confirmCodCollected(orderId, vendor));
////    }
////
////    // NEW: Mark as delivered (extended from your commented code)
////    @PostMapping("/delivered/{orderId}")
////    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
////    public ResponseEntity<OrderResponseDTO> markAsDelivered(
////            @PathVariable Long orderId,
////            @AuthenticationPrincipal User vendor) {
////        return ResponseEntity.ok(orderService.markAsDelivered(orderId, vendor));
////    }
////}
////
//////======================= 
/////
/////
//import com.agrowmart.dto.auth.order.OrderCancelRequestDTO;
//import com.agrowmart.dto.auth.order.OrderRequestDTO;
//import com.agrowmart.dto.auth.order.OrderResponseDTO;
//import com.agrowmart.entity.User;
//import com.agrowmart.service.OrderService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//public class OrderController {
//    private final OrderService orderService;
//
//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    // 1. Customer places order
//    @PostMapping("/create")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
//    public ResponseEntity<OrderResponseDTO> createOrder(
//            @AuthenticationPrincipal User customer,
//            @RequestBody OrderRequestDTO request) {
//        return ResponseEntity.status(201).body(orderService.createOrder(customer, request));
//    }
//
//    // 2. Customer views their orders
//    @GetMapping("/customer")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
//    public ResponseEntity<List<OrderResponseDTO>> getCustomerOrders(@AuthenticationPrincipal User customer) {
//        return ResponseEntity.ok(orderService.getCustomerOrders(customer));
//    }
//
//    // 3. Vendor sees pending orders
//    @GetMapping("/vendor/pending")
//    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
//    public ResponseEntity<List<OrderResponseDTO>> getVendorPendingOrders(@AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.getVendorPendingOrders(vendor));
//    }
//
//    // 4. Vendor accepts order
//    @PostMapping("/accept/{orderId}")
//    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
//    public ResponseEntity<OrderResponseDTO> acceptOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.acceptOrder(orderId, vendor));
//    }
//
//    // 5. Vendor rejects order
//    @PostMapping("/reject/{orderId}")
//    @PreAuthorize("hasAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
//    public ResponseEntity<OrderResponseDTO> rejectOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.rejectOrder(orderId, vendor));
//    }
//
//    // 6. Vendor marks order as delivered
//    @PostMapping("/delivered/{orderId}")
//    @PreAuthorize("hasAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
//    public ResponseEntity<OrderResponseDTO> markAsDelivered(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.markAsDelivered(orderId, vendor));
//    }
//
//    // 7. Vendor sees all their orders (history)
//    @GetMapping("/vendor/all")
//    @PreAuthorize("hasAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
//    public ResponseEntity<List<OrderResponseDTO>> getAllVendorOrders(@AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.getAllVendorOrders(vendor));
//    }
//
//    @PostMapping("/cancel/{orderId}")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
//    public ResponseEntity<OrderResponseDTO> cancelOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User customer,
//            @RequestBody OrderCancelRequestDTO request
//    ) {
//        return ResponseEntity.ok(
//                orderService.cancelOrderByCustomer(orderId, customer, request.reason())
//        );
//    }
//
//    @PostMapping("/vendor/cancel/{orderId}")
//    @PreAuthorize("hasAuthority('VEGETABLE','DAIRY','SEAFOODMEAT','WOMEN','FARMER')")
//    public ResponseEntity<OrderResponseDTO> vendorCancelOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor,
//            @RequestBody @Valid OrderCancelRequestDTO request
//    ) {
//        return ResponseEntity.ok(
//            orderService.cancelOrderByVendor(orderId, vendor, request.reason())
//        );
//    }
//
//    // NEW: Vendor confirms COD collected (after delivery)
//    @PostMapping("/cod-collected/{orderId}")
//    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER')")
//    public ResponseEntity<OrderResponseDTO> confirmCodCollected(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.confirmCodCollected(orderId, vendor));
//    }
//}



//-------------
// src/main/java/com/agrowmart/controller/OrderController.java

package com.agrowmart.controller;

import com.agrowmart.dto.auth.order.*;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.User;
import com.agrowmart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Customer places order
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @AuthenticationPrincipal Customer customer,
            @Valid @RequestBody OrderRequestDTO request) {
        return ResponseEntity.status(201).body(orderService.createOrder(customer, request));
    }

    // Customer views their orders
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(@AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customer));
    }

    // Vendor sees pending orders
    @GetMapping("/vendor/pending")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<List<OrderResponseDTO>> getPendingOrders(@AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.getVendorPendingOrders(vendor));
    }

    // Vendor accepts order
    @PostMapping("/accept/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> acceptOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.acceptOrder(orderId, vendor));
    }

    // Vendor rejects order
    @PostMapping("/reject/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> rejectOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.rejectOrder(orderId, vendor));
    }

    // Vendor marks delivered
    @PostMapping("/delivered/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> markDelivered(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.markAsDelivered(orderId, vendor));
    }

    // Vendor sees all orders
    @GetMapping("/vendor/all")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<List<OrderResponseDTO>> getAllVendorOrders(@AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.getAllVendorOrders(vendor));
    }

    // Customer cancels order
    @PostMapping("/cancel/{orderId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal Customer customer,
            @Valid @RequestBody OrderCancelRequestDTO request) {
        return ResponseEntity.ok(orderService.cancelOrderByCustomer(orderId, customer, request.reason()));
    }

    // Vendor cancels order
    @PostMapping("/vendor/cancel/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> vendorCancelOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor,
            @Valid @RequestBody OrderCancelRequestDTO request) {
        return ResponseEntity.ok(orderService.cancelOrderByVendor(orderId, vendor, request.reason()));
    }

    // Vendor confirms COD collected
    @PostMapping("/cod-collected/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> confirmCodCollected(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.confirmCodCollected(orderId, vendor));
    }
}
