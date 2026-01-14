//
//package com.agrowmart.service;
//
//import com.agrowmart.dto.auth.order.*;
//import com.agrowmart.entity.*;
//import com.agrowmart.entity.order.Offer;
//import com.agrowmart.entity.order.OfferUsage;
//import com.agrowmart.entity.order.Order;
//import com.agrowmart.entity.order.OrderItem;
//import com.agrowmart.entity.order.OrderStatusHistory;
//import com.agrowmart.enums.VendorAcceptThenCancelReason;
//import com.agrowmart.enums.VendorCancelReason;
//import com.agrowmart.exception.ForbiddenException;
//import com.agrowmart.exception.ResourceNotFoundException;
//import com.agrowmart.repository.*;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//
//
//
//
//@Service
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final OrderItemRepository orderItemRepository;
//    private final OrderStatusHistoryRepository statusHistoryRepository;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final VegetableDetailRepository vegetableDetailRepository;
//    private final DairyDetailRepository dairyDetailRepository;
//    private final MeatDetailRepository meatDetailRepository;
//    private final WomenProductRepository womenProductRepository;
//
//    // NEW OFFER SYSTEM
//    private final OfferRepository offerRepository;
//    private final OfferUsageRepository offerUsageRepository;
//    
//    private final NotificationService NotificationService;
//
//    public OrderServiceImpl(
//            OrderRepository orderRepository,
//            OrderItemRepository orderItemRepository,
//            OrderStatusHistoryRepository statusHistoryRepository,
//            UserRepository userRepository,
//            ProductRepository productRepository,
//            VegetableDetailRepository vegetableDetailRepository,
//            DairyDetailRepository dairyDetailRepository,
//            MeatDetailRepository meatDetailRepository,
//            WomenProductRepository womenProductRepository,
//            OfferRepository offerRepository,
//            OfferUsageRepository offerUsageRepository,
//            NotificationService NotificationService
//            
//    ) {
//        this.orderRepository = orderRepository;
//        this.orderItemRepository = orderItemRepository;
//        this.statusHistoryRepository = statusHistoryRepository;
//        this.userRepository = userRepository;
//        this.productRepository = productRepository;
//        this.vegetableDetailRepository = vegetableDetailRepository;
//        this.dairyDetailRepository = dairyDetailRepository;
//        this.meatDetailRepository = meatDetailRepository;
//        this.womenProductRepository = womenProductRepository;
//        this.offerRepository = offerRepository;
//        this.offerUsageRepository = offerUsageRepository;
//        this.NotificationService=NotificationService;
//    }
//    
//    
//    @Override
//    @Transactional
//    public OrderResponseDTO createOrder(User customer, OrderRequestDTO request) {
//        User merchant = userRepository.findById(request.merchantId())
//                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
//
//        Order order = new Order();
//        order.setCustomer(customer);
//        order.setMerchant(merchant);
//        order.setStatus(Order.OrderStatus.PENDING);
//        order.setCreatedAt(LocalDateTime.now());
//        order.setUpdatedAt(LocalDateTime.now());
//
//        BigDecimal subtotal = BigDecimal.ZERO;
//        for (OrderItemRequestDTO item : request.items()) {
//            Product product = productRepository.findById(item.productId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//            
//         // Changes StockQuantity - Ankita 
//            
//            // save updated stock
//            // 🔥 STOCK VALIDATION
//            if (product.getStockQuantity() < item.quantity()) {
//                throw new IllegalStateException("Not enough stock for product: " + product.getProductName());
//            }
//            
//            product.updateStock(item.quantity());  // reduce stock
//            productRepository.save(product);       // save updated stock
//            
//            
//            BigDecimal price = getProductPrice(product);
//            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.quantity()));
//
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProduct(product);
//            orderItem.setQuantity(item.quantity());
//            orderItem.setPricePerUnit(price);
//            orderItem.setTotalPrice(itemTotal);
//            order.getItems().add(orderItem);
//
//            subtotal = subtotal.add(itemTotal);
//        }
//        order.setSubtotal(subtotal);
//
//        // Promo Code / Offer Logic
//        BigDecimal discount = BigDecimal.ZERO;
//        String appliedOfferCode = null;
//        if (request.promoCode() != null && !request.promoCode().trim().isBlank()) {
//            String code = request.promoCode().trim().toUpperCase();
//            Offer offer = offerRepository.findActiveOfferByCodeAndMerchant(code, merchant.getId(), LocalDate.now())
//                    .orElse(null);
//            if (offer != null && isOfferApplicable(offer, customer, merchant, subtotal)) {
//                discount = calculateDiscount(offer, subtotal);
//                OfferUsage usage = new OfferUsage();
//                usage.setCustomer(customer);
//                usage.setOffer(offer);
//                usage.setOrder(order);
//                offerUsageRepository.save(usage);
//                appliedOfferCode = offer.getCode();
//            }
//        }
//
//        order.setDiscountAmount(discount);
//        order.setPromoCode(appliedOfferCode);
//
//        BigDecimal afterDiscount = subtotal.subtract(discount);
//        BigDecimal deliveryCharge = afterDiscount.compareTo(BigDecimal.valueOf(100)) < 0
//                ? BigDecimal.valueOf(25) : BigDecimal.ZERO;
//
//        order.setDeliveryCharge(deliveryCharge);
//        order.setTotalPrice(afterDiscount.add(deliveryCharge));
//
//        order = orderRepository.save(order);
//        addStatusHistory(order, "PENDING");
//
//        // NOTIFICATION → Vendor (New Order)
//        NotificationService.sendNotification(
//            merchant.getId(),
//            "New Order Received",
//            "Order #" + order.getId() + " | ₹" + order.getTotalPrice() + " from " + customer.getName(),
//            Map.of("type", "new_order", "orderId", order.getId().toString())
//        );
//
//        return mapToResponse(order);
//    }
//
//
//
//	@Override
//    @Transactional
//    public OrderResponseDTO rejectOrder(String orderId, User vendor) {
//        Order order = getOrderAndCheckOwnership(orderId, vendor);
//
//        order.setStatus(Order.OrderStatus.REJECTED);
//        order.setUpdatedAt(LocalDateTime.now());
//        orderRepository.save(order);
//        addStatusHistory(order, "REJECTED");
//
//        // NOTIFICATION → Customer (Order Rejected)
//        NotificationService.sendNotification(
//            order.getCustomer().getId(),
//            "Order Rejected",
//            "Sorry, your Order #" + order.getId() + " was rejected.",
//            Map.of("type", "order_rejected", "orderId", order.getId().toString())
//        );
//
//        return mapToResponse(order);
//    }
//
//    // Helper method – no duplicate code
//    private Order getOrderAndCheckOwnership(String  orderId, User user) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//        if (!order.getMerchant().getId().equals(user.getId())) {
//            throw new ForbiddenException("Not authorized");
//        }
//        return order;
//    }
//    
//
////    @Override
////    @Transactional
////    public OrderResponseDTO createOrder(User customer, OrderRequestDTO request) {
////        User merchant = userRepository.findById(request.merchantId())
////                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
////
////        Order order = new Order();
////        order.setCustomer(customer);
////        order.setMerchant(merchant);
////        order.setStatus(Order.OrderStatus.PENDING);
////        order.setCreatedAt(LocalDateTime.now());
////        order.setUpdatedAt(LocalDateTime.now());
////
////        BigDecimal subtotal = BigDecimal.ZERO;
////
////        for (OrderItemRequestDTO item : request.items()) {
////            Product product = productRepository.findById(item.productId())
////                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
////
////            BigDecimal price = getProductPrice(product);
////            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.quantity()));
////
////            OrderItem orderItem = new OrderItem();
////            orderItem.setOrder(order);
////            orderItem.setProduct(product);
////            orderItem.setQuantity(item.quantity());
////            orderItem.setPricePerUnit(price);
////            orderItem.setTotalPrice(itemTotal);
////            order.getItems().add(orderItem);
////
////            subtotal = subtotal.add(itemTotal);
////        }
////
////        order.setSubtotal(subtotal);
////
////        // FINAL WORKING OFFER LOGIC
////        BigDecimal discount = BigDecimal.ZERO;
////        String appliedOfferCode = null;
////
////        String inputCode = request.promoCode();
////        if (inputCode != null && !inputCode.trim().isBlank()) {
////            String cleanCode = inputCode.trim().toUpperCase();
////
////            Offer offer = offerRepository.findActiveOfferByCodeAndMerchant(
////                    cleanCode, merchant.getId(), LocalDate.now()
////            ).orElse(null);
////
////            if (offer != null && isOfferApplicable(offer, customer, merchant, subtotal)) {
////                discount = calculateDiscount(offer, subtotal);
////
////                OfferUsage usage = new OfferUsage();
////                usage.setCustomer(customer);
////                usage.setOffer(offer);
////                usage.setOrder(order);
////                offerUsageRepository.save(usage);
////
////                appliedOfferCode = offer.getCode(); // "DIWALI50"
////            }
////        }
////
////        order.setDiscountAmount(discount);
////        order.setPromoCode(appliedOfferCode); // SAVES THE CODE!
////
////        BigDecimal priceAfterDiscount = subtotal.subtract(discount);
////        BigDecimal deliveryCharge = priceAfterDiscount.compareTo(BigDecimal.valueOf(100)) < 0
////                ? BigDecimal.valueOf(25)
////                : BigDecimal.ZERO;
////
////        order.setDeliveryCharge(deliveryCharge);
////        order.setTotalPrice(priceAfterDiscount.add(deliveryCharge));
////
////        order = orderRepository.save(order);
////        addStatusHistory(order, "PENDING");
////
////        return mapToResponse(order);
////    }
//
//    private boolean isOfferApplicable(Offer offer, User customer, User merchant, BigDecimal subtotal) {
//        if (subtotal.compareTo(offer.getMinOrderAmount()) < 0) return false;
//        if (!offer.isActive()) return false;
//
//        LocalDate today = LocalDate.now();
//        if (today.isBefore(offer.getStartDate()) || today.isAfter(offer.getEndDate())) return false;
//
//        if (offer.getCustomerGroup() == Offer.CustomerGroup.NEW_CUSTOMER) {
//            return !orderRepository.existsByCustomerAndMerchant(customer, merchant);
//        }
//        if (offer.getCustomerGroup() == Offer.CustomerGroup.INACTIVE_30_DAYS) {
//            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
//            return !orderRepository.existsByCustomerAndMerchantAndCreatedAtAfter(customer, merchant, thirtyDaysAgo);
//        }
//        return true;
//    }
//
//    private BigDecimal calculateDiscount(Offer offer, BigDecimal subtotal) {
//        BigDecimal discount = BigDecimal.ZERO;
//
//        if (offer.getDiscountType() == Offer.DiscountType.PERCENTAGE && offer.getDiscountPercent() != null) {
//            discount = subtotal.multiply(BigDecimal.valueOf(offer.getDiscountPercent()))
//                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//        } else if (offer.getDiscountType() == Offer.DiscountType.FLAT && offer.getFlatDiscount() != null) {
//            discount = offer.getFlatDiscount();
//        }
//
//        if (offer.getMaxDiscountAmount() != null && discount.compareTo(offer.getMaxDiscountAmount()) > 0) {
//            discount = offer.getMaxDiscountAmount();
//        }
//        return discount;
//    }
//
//    private BigDecimal getProductPrice(Product product) {
//        String type = determineProductType(product.getCategory());
//        return switch (type) {
//            case "VEGETABLE" -> vegetableDetailRepository.findByProductId(product.getId())
//                    .map(VegetableDetail::getMinPrice)
//                    .orElseThrow(() -> new IllegalStateException("Price missing"));
//            case "DAIRY" -> dairyDetailRepository.findByProductId(product.getId())
//                    .map(DairyDetail::getMinPrice)
//                    .orElseThrow(() -> new IllegalStateException("Price missing"));
//            case "MEAT" -> meatDetailRepository.findByProductId(product.getId())
//                    .map(MeatDetail::getMinPrice)
//                    .orElseThrow(() -> new IllegalStateException("Price missing"));
//            case "WOMEN" -> womenProductRepository.findById(product.getId())
//                    .map(WomenProduct::getMinPrice)
//                    .orElseThrow(() -> new IllegalStateException("Price missing"));
//            default -> throw new IllegalArgumentException("Unsupported category");
//        };
//    }
//
//    private void addStatusHistory(Order order, String status) {
//        OrderStatusHistory history = new OrderStatusHistory();
//        history.setOrder(order);
//        history.setStatus(status);
//        statusHistoryRepository.save(history);
//    }
//
//    @Override
//    public List<OrderResponseDTO> getCustomerOrders(User customer) {
//        return orderRepository.findByCustomer(customer)
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//
//    @Override
//    public List<OrderResponseDTO> getVendorPendingOrders(User vendor) {
//        return orderRepository.findByMerchantAndStatus(vendor, Order.OrderStatus.PENDING)
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//    
//  //------------
//    //this is main code
//
////    @Override
////    @Transactional
////    public OrderResponseDTO acceptOrder(Long orderId, User vendor) {
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
////        if (!order.getMerchant().getId().equals(vendor.getId())) {
////            throw new ForbiddenException("Not authorized");
////        }
////        order.setStatus(Order.OrderStatus.ACCEPTED);
////        order.setUpdatedAt(LocalDateTime.now());
////        orderRepository.save(order);
////        addStatusHistory(order, "ACCEPTED");
////        return mapToResponse(order);
////    }
////
////    @Override
////    @Transactional
////    public OrderResponseDTO rejectOrder(Long orderId, User vendor) {
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
////        if (!order.getMerchant().getId().equals(vendor.getId())) {
////            throw new ForbiddenException("Not authorized");
////        }
////        order.setStatus(Order.OrderStatus.REJECTED);
////        order.setUpdatedAt(LocalDateTime.now());
////        orderRepository.save(order);
////        addStatusHistory(order, "REJECTED");
////        return mapToResponse(order);
////    }
//
//    private OrderResponseDTO mapToResponse(Order order) {
//        var items = order.getItems().stream()
//                .map(item -> new OrderItemResponseDTO(
//                        item.getId(),
//                        item.getProduct().getId(),
//                        item.getQuantity(),
//                        item.getPricePerUnit(),
//                        item.getTotalPrice()
//                ))
//                .toList();
//
//        return new OrderResponseDTO(
//                order.getId(),
//                order.getCustomer().getId(),
//                order.getMerchant().getId(),
//                order.getSubtotal(),
//                order.getDiscountAmount(),
//                order.getDeliveryCharge(),
//                order.getTotalPrice(),
//                order.getPromoCode(), // "DIWALI50" or null — NEVER "null" string!
//                order.getStatus().name(),
//                order.getCreatedAt(),
//                order.getUpdatedAt(),
//                items,
//                order.getCancelReason(),
//                order.getCancelledBy(),
//                order.getCancelledAt()
//        );
//    }
//
//    private String determineProductType(Category category) {
//        if (category == null) return "GENERAL";
//        Category root = category;
//        while (root.getParent() != null) root = root.getParent();
//        String name = root.getName().toLowerCase();
//        if (name.contains("vegetable") || name.contains("fruit") || name.contains("fresh")) return "VEGETABLE";
//        if (name.contains("dairy") || name.contains("milk")) return "DAIRY";
//        if (name.contains("meat") || name.contains("chicken") || name.contains("fish") || name.contains("seafood")) return "MEAT";
//        if (name.contains("women") || name.contains("handicraft")) return "WOMEN";
//        return "GENERAL";
//    }
//    
//
//    
//    
//    @Override
//    public List<OrderResponseDTO> getAllVendorOrders(User vendor) {
//        return orderRepository.findByMerchantOrderByCreatedAtDesc(vendor)  // newest first
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//
//
//	@Override
//	public OrderResponseDTO markAsDelivered(Long orderId, User vendor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public OrderResponseDTO confirmCodCollected(Long orderId, User vendor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public OrderResponseDTO cancelOrderByCustomer(String orderId, String reason, User customer) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//    
//    
//	@Override
//	@Transactional
//	public OrderResponseDTO cancelOrderByVendor(
//	        String orderId,
//	        User vendor,
//	        String reason
//	) {
//	    Order order = orderRepository.findById(orderId)
//	            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//	    // Vendor ownership check
//	    if (!order.getMerchant().getId().equals(vendor.getId())) {
//	        throw new ForbiddenException("Not allowed");
//	    }
//
//	    // Already cancelled
//	    if (order.getStatus() == Order.OrderStatus.CANCELLED) {
//	        throw new IllegalStateException("Order already cancelled");
//	    }
//
//	    // 🚨 Reason mandatory
//	    if (reason == null || reason.trim().isEmpty()) {
//	        throw new IllegalStateException("Cancellation reason is mandatory");
//	    }
//
//	    // ✅ CASE 1: PENDING → CANCEL
//	    if (order.getStatus() == Order.OrderStatus.PENDING) {
//	        try {
//	            VendorCancelReason cancelReason =
//	                    VendorCancelReason.valueOf(reason);
//
//	            order.setVendorCancelReason(cancelReason);
//
//	        } catch (IllegalArgumentException e) {
//	            throw new IllegalStateException("Invalid vendor cancellation reason");
//	        }
//	    }
//
//	    // ✅ CASE 2: ACCEPTED → CANCEL
//	    else if (order.getStatus() == Order.OrderStatus.ACCEPTED) {
//	        try {
//	            VendorAcceptThenCancelReason cancelReason =
//	                    VendorAcceptThenCancelReason.valueOf(reason);
//
//	            order.setVendorAcceptCancelReason(cancelReason);
//
//	        } catch (IllegalArgumentException e) {
//	            throw new IllegalStateException("Invalid accept-then-cancel reason");
//	        }
//	    }
//
//	    else {
//	        throw new IllegalStateException("Order cannot be cancelled in this state");
//	    }
//
//	    order.setStatus(Order.OrderStatus.CANCELLED);
//	    order.setCancelledBy("VENDOR");
//	    order.setCancelledAt(LocalDateTime.now());
//
//	    orderRepository.save(order);
//	    addStatusHistory(order, "CANCELLED");
//
//	    return mapToResponse(order);
//	}
//
//
//	@Override
//	public OrderResponseDTO acceptOrder(Long orderId, User vendor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public OrderResponseDTO rejectOrder(Long orderId, User vendor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public OrderResponseDTO cancelOrderByCustomer(String orderId, User customer, String reason) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//    
//}



package com.agrowmart.service;

import com.agrowmart.dto.auth.order.*;
import com.agrowmart.entity.*;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.order.*;
import com.agrowmart.enums.VendorAcceptThenCancelReason;
import com.agrowmart.enums.VendorCancelReason;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VegetableDetailRepository vegetableDetailRepository;
    private final DairyDetailRepository dairyDetailRepository;
    private final MeatDetailRepository meatDetailRepository;
    private final WomenProductRepository womenProductRepository;
    private final OfferRepository offerRepository;
    private final OfferUsageRepository offerUsageRepository;
    private final NotificationService notificationService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderStatusHistoryRepository statusHistoryRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            VegetableDetailRepository vegetableDetailRepository,
            DairyDetailRepository dairyDetailRepository,
            MeatDetailRepository meatDetailRepository,
            WomenProductRepository womenProductRepository,
            OfferRepository offerRepository,
            OfferUsageRepository offerUsageRepository,
            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.vegetableDetailRepository = vegetableDetailRepository;
        this.dairyDetailRepository = dairyDetailRepository;
        this.meatDetailRepository = meatDetailRepository;
        this.womenProductRepository = womenProductRepository;
        this.offerRepository = offerRepository;
        this.offerUsageRepository = offerUsageRepository;
        this.notificationService = notificationService;
    }

//    @Override
//    @Transactional
//    public OrderResponseDTO createOrder(Customer customer, OrderRequestDTO request) {
//        User merchant = userRepository.findById(request.merchantId())
//                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
//        Order order = new Order();
//        order.setCustomer(customer);
//        order.setMerchant(merchant);
//        order.setStatus(Order.OrderStatus.PENDING);
//        order.setPaymentMode(request.paymentMode());
//        order.setPaymentStatus("PENDING");
//        order.setSettlementStatus("PENDING");
//        order.setCreatedAt(LocalDateTime.now());
//        order.setUpdatedAt(LocalDateTime.now());
//        BigDecimal subtotal = BigDecimal.ZERO;
//        for (OrderItemRequestDTO item : request.items()) {
//        	
//        	Product product = productRepository.findById(item.productId())   // returns Optional.empty()
//        			.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//            
//            
//            if (product.getStockQuantity() < item.quantity()) {
//                throw new IllegalStateException("Not enough stock for product: " + product.getProductName());
//            }
//            product.updateStock(item.quantity());
//            productRepository.save(product);
//            BigDecimal price = getProductPrice(product);
//            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.quantity()));
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProduct(product);
//            orderItem.setQuantity(item.quantity());
//            orderItem.setPricePerUnit(price);
//            orderItem.setTotalPrice(itemTotal);
//            order.getItems().add(orderItem);
//            subtotal = subtotal.add(itemTotal);
//        }
//        order.setSubtotal(subtotal);
//        // Promo Code / Offer Logic
//        BigDecimal discount = BigDecimal.ZERO;
//        String appliedOfferCode = null;
//        if (request.promoCode() != null && !request.promoCode().trim().isBlank()) {
//            String code = request.promoCode().trim().toUpperCase();
//            Offer offer = offerRepository.findActiveOfferByCodeAndMerchant(code, merchant.getId(), LocalDate.now())
//                    .orElse(null);
//            if (offer != null && isOfferApplicable(offer, customer, merchant, subtotal)) {
//                // Prevent reuse
//                if (offerUsageRepository.existsByCustomerAndOffer(customer, offer)) {
//                    throw new IllegalStateException("You have already used this coupon");
//                }
//                discount = calculateDiscount(offer, subtotal);
//                appliedOfferCode = offer.getCode();
//                OfferUsage usage = new OfferUsage();
//                usage.setCustomer(customer);
//                usage.setOffer(offer);
//                usage.setOrder(order);
//                offerUsageRepository.save(usage);
//            }
//        }
//        order.setDiscountAmount(discount);
//        order.setPromoCode(appliedOfferCode);
//        BigDecimal afterDiscount = subtotal.subtract(discount);
//        BigDecimal deliveryCharge = afterDiscount.compareTo(BigDecimal.valueOf(100)) < 0
//                ? BigDecimal.valueOf(25) : BigDecimal.ZERO;
//        order.setDeliveryCharge(deliveryCharge);
//        order.setTotalPrice(afterDiscount.add(deliveryCharge));
//  
//        order = orderRepository.save(order);
//        addStatusHistory(order, "PENDING");
//        notificationService.sendNotification(
//                merchant.getId(),
//                "New Order",
//                "Order #" + order.getId() + " | ₹" + order.getTotalPrice() + " | " + order.getPaymentMode(),
//                Map.of("type", "new_order", "orderId", order.getId())
//        );
//        return mapToResponse(order);
//    }
//    
//    
    
    
    @Override
    @Transactional
    public OrderResponseDTO createOrder(Customer customer, OrderRequestDTO request) {
        User merchant = userRepository.findById(request.merchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setMerchant(merchant);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentMode(request.paymentMode());
        order.setPaymentStatus("PENDING");
        order.setSettlementStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequestDTO reqItem : request.items()) {
            Long productId = reqItem.productId();

            Product normalProduct = productRepository.findById(productId).orElse(null);
            WomenProduct womenProduct = null;
            BigDecimal price;
            double availableStock;
            String productType = null;

            if (normalProduct != null) {
                productType = "NORMAL";
                price = getProductPrice(normalProduct);
                availableStock = normalProduct.getStockQuantity() != null ? normalProduct.getStockQuantity() : 0.0;
            } else {
                womenProduct = womenProductRepository.findById(productId).orElse(null);
                if (womenProduct == null) {
                    throw new ResourceNotFoundException("Product not found with ID: " + productId);
                }
                productType = "WOMEN";
                price = womenProduct.getMinPrice();
                availableStock = womenProduct.getStock() != null ? womenProduct.getStock() : 0;
            }

            // Stock validation
            if (availableStock < reqItem.quantity()) {
                String name = normalProduct != null ? normalProduct.getProductName() :
                              (womenProduct != null ? womenProduct.getName() : "Unknown");
                throw new IllegalStateException("Not enough stock for product: " + name);
            }

            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(reqItem.quantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(reqItem.quantity());
            orderItem.setPricePerUnit(price);
            orderItem.setTotalPrice(itemTotal);

            if ("NORMAL".equals(productType)) {
                orderItem.setProduct(normalProduct);
                normalProduct.updateStock(reqItem.quantity());
                productRepository.save(normalProduct);
            } else {
                orderItem.setWomenProduct(womenProduct);
                womenProduct.setStock(womenProduct.getStock() - reqItem.quantity());
                womenProductRepository.save(womenProduct);
            }

            order.getItems().add(orderItem);
            subtotal = subtotal.add(itemTotal);
        }

        order.setSubtotal(subtotal);

        // Promo & discount logic (unchanged)
        BigDecimal discount = BigDecimal.ZERO;
        String appliedOfferCode = null;
        if (request.promoCode() != null && !request.promoCode().trim().isBlank()) {
            String code = request.promoCode().trim().toUpperCase();
            Offer offer = offerRepository.findActiveOfferByCodeAndMerchant(code, merchant.getId(), LocalDate.now())
                    .orElse(null);
            if (offer != null && isOfferApplicable(offer, customer, merchant, subtotal)) {
                if (offerUsageRepository.existsByCustomerAndOffer(customer, offer)) {
                    throw new IllegalStateException("You have already used this coupon");
                }
                discount = calculateDiscount(offer, subtotal);
                appliedOfferCode = offer.getCode();
                OfferUsage usage = new OfferUsage();
                usage.setCustomer(customer);
                usage.setOffer(offer);
                usage.setOrder(order);
                offerUsageRepository.save(usage);
            }
        }

        order.setDiscountAmount(discount);
        order.setPromoCode(appliedOfferCode);

        BigDecimal afterDiscount = subtotal.subtract(discount);
        BigDecimal deliveryCharge = afterDiscount.compareTo(BigDecimal.valueOf(100)) < 0
                ? BigDecimal.valueOf(25) : BigDecimal.ZERO;
        order.setDeliveryCharge(deliveryCharge);
        order.setTotalPrice(afterDiscount.add(deliveryCharge));

        order = orderRepository.save(order);

        addStatusHistory(order, "PENDING");

        notificationService.sendNotification(
                merchant.getId(),
                "New Order",
                "Order #" + order.getId() + " | ₹" + order.getTotalPrice() + " | " + order.getPaymentMode(),
                Map.of("type", "new_order", "orderId", order.getId())
        );

        return mapToResponse(order);
    }

    // ──────────────────────────────────────────────
    // Updated mapToResponse - supports both product types
    // ──────────────────────────────────────────────
    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getRealProductId(),
                        item.getQuantity(),
                        item.getPricePerUnit(),
                        item.getTotalPrice(),
                        item.getDisplayName()  // Correct name for both types
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getId() : null,
                order.getMerchant() != null ? order.getMerchant().getId() : null,
                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getDeliveryCharge(),
                order.getTotalPrice(),
                order.getPromoCode(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items,
                order.getCancelReason(),
                order.getCancelledBy(),
                order.getCancelledAt()
        );
    }

    @Override
    @Transactional
    public OrderResponseDTO acceptOrder(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        order.setStatus(Order.OrderStatus.ACCEPTED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "ACCEPTED");
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Accepted",
                "Your Order #" + order.getId() + " has been accepted",
                Map.of("type", "order_accepted")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO rejectOrder(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        order.setStatus(Order.OrderStatus.REJECTED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "REJECTED");
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Rejected",
                "Sorry, your Order #" + order.getId() + " was rejected",
                Map.of("type", "order_rejected")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO markAsDelivered(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        if (order.getStatus() != Order.OrderStatus.ACCEPTED) {
            throw new IllegalStateException("Order must be accepted first");
        }
        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "DELIVERED");
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Delivered",
                "Your Order #" + order.getId() + " has been delivered!",
                Map.of("type", "order_delivered")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO confirmCodCollected(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        if (!"COD".equals(order.getPaymentMode())) {
            throw new IllegalStateException("Only COD orders can be confirmed");
        }
        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException("Order must be delivered first");
        }
        order.setPaymentStatus("SUCCESS");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "COD Collected",
                "Cash collected for Order #" + order.getId(),
                Map.of("type", "cod_collected")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrderByCustomer(String orderId, Customer customer, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new ForbiddenException("You can only cancel your own orders");
        }
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalStateException("Cancellation reason required");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        order.setCancelledBy("CUSTOMER");
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "CANCELLED");
        notificationService.sendNotification(
                order.getMerchant().getId(),
                "Order Cancelled",
                "Order #" + order.getId() + " cancelled by customer: " + reason,
                Map.of("type", "order_cancelled")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrderByVendor(String orderId, User vendor, String reason) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalStateException("Cancellation reason required");
        }
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            try {
                order.setVendorCancelReason(VendorCancelReason.valueOf(reason));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid reason for pending order");
            }
        } else if (order.getStatus() == Order.OrderStatus.ACCEPTED) {
            try {
                order.setVendorAcceptCancelReason(VendorAcceptThenCancelReason.valueOf(reason));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid reason for accepted order");
            }
        } else {
            throw new IllegalStateException("Order cannot be cancelled in current state");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledBy("VENDOR");
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "CANCELLED");
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Cancelled by Vendor",
                "Order #" + order.getId() + " was cancelled by vendor: " + reason,
                Map.of("type", "order_cancelled")
        );
        return mapToResponse(order);
    }

    private Order getOrderAndCheckOwnership(String orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getMerchant().getId().equals(user.getId())) {
            throw new ForbiddenException("Not authorized");
        }
        return order;
    }

    private void addStatusHistory(Order order, String status) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);
    }

//    private OrderResponseDTO mapToResponse(Order order) {
//        List<OrderItemResponseDTO> items = order.getItems().stream()
//                .map(item -> new OrderItemResponseDTO(
//                        item.getId(),
//                        item.getProduct().getId(),
//                        item.getQuantity(),
//                        item.getPricePerUnit(),
//                        item.getTotalPrice()
//                ))
//                .toList();
//        return new OrderResponseDTO(
//                order.getId(),
//                order.getCustomer().getId(),
//                order.getMerchant().getId(),
//                order.getSubtotal(),
//                order.getDiscountAmount(),
//                order.getDeliveryCharge(),
//                order.getTotalPrice(),
//                order.getPromoCode(),
//                order.getStatus().name(),
//                order.getCreatedAt(),
//                order.getUpdatedAt(),
//                items,
//                order.getCancelReason(),
//                order.getCancelledBy(),
//                order.getCancelledAt()
//        );
//    }

    
    
    
    
    private boolean isOfferApplicable(Offer offer, Customer customer, User merchant, BigDecimal subtotal) {
        if (subtotal.compareTo(offer.getMinOrderAmount()) < 0) return false;
        if (!offer.isActive()) return false;
        LocalDate today = LocalDate.now();
        if (today.isBefore(offer.getStartDate()) || today.isAfter(offer.getEndDate())) return false;
        if (offer.getCustomerGroup() == Offer.CustomerGroup.NEW_CUSTOMER) {
            return !orderRepository.existsByCustomerAndMerchant(customer, merchant);
        }
        if (offer.getCustomerGroup() == Offer.CustomerGroup.INACTIVE_30_DAYS) {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            return !orderRepository.existsByCustomerAndMerchantAndCreatedAtAfter(customer, merchant, thirtyDaysAgo);
        }
        return true;
    }

    private BigDecimal calculateDiscount(Offer offer, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;
        if (offer.getDiscountType() == Offer.DiscountType.PERCENTAGE && offer.getDiscountPercent() != null) {
            discount = subtotal.multiply(BigDecimal.valueOf(offer.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (offer.getDiscountType() == Offer.DiscountType.FLAT && offer.getFlatDiscount() != null) {
            discount = offer.getFlatDiscount();
        }
        if (offer.getMaxDiscountAmount() != null && discount.compareTo(offer.getMaxDiscountAmount()) > 0) {
            discount = offer.getMaxDiscountAmount();
        }
        return discount;
    }

    private BigDecimal getProductPrice(Product product) {
        String type = determineProductType(product.getCategory());
        return switch (type) {
            case "VEGETABLE" -> vegetableDetailRepository.findByProductId(product.getId())
                    .map(VegetableDetail::getMinPrice)
                    .orElseThrow(() -> new IllegalStateException("Price missing"));
            case "DAIRY" -> dairyDetailRepository.findByProductId(product.getId())
                    .map(DairyDetail::getMinPrice)
                    .orElseThrow(() -> new IllegalStateException("Price missing"));
            case "MEAT" -> meatDetailRepository.findByProductId(product.getId())
                    .map(MeatDetail::getMinPrice)
                    .orElseThrow(() -> new IllegalStateException("Price missing"));
            case "WOMEN"      -> womenProductRepository.findById(product.getId())   // ← This line
            .map(WomenProduct::getMinPrice)
            
           
                    .orElseThrow(() -> new IllegalStateException("Price missing"));
            default -> throw new IllegalArgumentException("Unsupported category");
        };
    }

    private String determineProductType(Category category) {
        if (category == null) return "GENERAL";
        Category root = category;
        while (root.getParent() != null) root = root.getParent();
        String name = root.getName().toLowerCase();
        if (name.contains("vegetable") || name.contains("fruit") || name.contains("fresh")) return "VEGETABLE";
        if (name.contains("dairy") || name.contains("milk")) return "DAIRY";
        if (name.contains("meat") || name.contains("chicken") || name.contains("fish") || name.contains("seafood")) return "MEAT";
        if (name.contains("women") || name.contains("handicraft")) return "WOMEN";
        return "GENERAL";
    }

    @Override
    public List<OrderResponseDTO> getCustomerOrders(Customer customer) {
        return orderRepository.findByCustomer(customer)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getVendorPendingOrders(User vendor) {
        return orderRepository.findByMerchantAndStatus(vendor, Order.OrderStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getAllVendorOrders(User vendor) {
        return orderRepository.findByMerchantOrderByCreatedAtDesc(vendor)
                .stream()
                .map(this::mapToResponse)
                .toList();
    

    }
	
}