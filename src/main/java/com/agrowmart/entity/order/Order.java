//// src/main/java/com/agrowmart/entity/order/Order.java
//
//package com.agrowmart.entity.order;
//
//import com.agrowmart.entity.User;
//import com.agrowmart.enums.VendorAcceptThenCancelReason;
//import com.agrowmart.enums.VendorCancelReason;
//
//import jakarta.persistence.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "orders")
//public class Order {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private String  id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id", nullable = false)
//    private User customer;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "merchant_id", nullable = false)
//    private User merchant;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal subtotal = BigDecimal.ZERO;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal discountAmount = BigDecimal.ZERO;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal deliveryCharge = BigDecimal.ZERO;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal totalPrice;
//
//    // FINAL FIX — CLEAN NULLABLE COLUMN
//    @Column(name = "promo_code", length = 50, nullable = true)
//    private String promoCode;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private OrderStatus status = OrderStatus.PENDING;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt = LocalDateTime.now();
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<OrderItem> items = new ArrayList<>();
//
//    @PreUpdate
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public User getCustomer() {
//		return customer;
//	}
//
//	public void setCustomer(User customer) {
//		this.customer = customer;
//	}
//
//	public User getMerchant() {
//		return merchant;
//	}
//
//	public void setMerchant(User merchant) {
//		this.merchant = merchant;
//	}
//
//	public BigDecimal getSubtotal() {
//		return subtotal;
//	}
//
//	public void setSubtotal(BigDecimal subtotal) {
//		this.subtotal = subtotal;
//	}
//
//	public BigDecimal getDiscountAmount() {
//		return discountAmount;
//	}
//
//	public void setDiscountAmount(BigDecimal discountAmount) {
//		this.discountAmount = discountAmount;
//	}
//
//	public BigDecimal getDeliveryCharge() {
//		return deliveryCharge;
//	}
//
//	public void setDeliveryCharge(BigDecimal deliveryCharge) {
//		this.deliveryCharge = deliveryCharge;
//	}
//
//	public BigDecimal getTotalPrice() {
//		return totalPrice;
//	}
//
//	public void setTotalPrice(BigDecimal totalPrice) {
//		this.totalPrice = totalPrice;
//	}
//
//	public OrderStatus getStatus() {
//		return status;
//	}
//
//	public void setStatus(OrderStatus status) {
//		this.status = status;
//	}
//
//	public LocalDateTime getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(LocalDateTime createdAt) {
//		this.createdAt = createdAt;
//	}
//
//	public LocalDateTime getUpdatedAt() {
//		return updatedAt;
//	}
//
//	public void setUpdatedAt(LocalDateTime updatedAt) {
//		this.updatedAt = updatedAt;
//	}
//
//	public List<OrderItem> getItems() {
//		return items;
//	}
//
//	public void setItems(List<OrderItem> items) {
//		this.items = items;
//	}
//
//	public enum OrderStatus {
//        PENDING, ACCEPTED, REJECTED, CANCELLED, DELIVERED
//    }
//
//    // CLEAN GETTER — NEVER TOUCH!
//    public String getPromoCode() {
//        return promoCode;
//    }
//
//    public void setPromoCode(String promoCode) {
//        this.promoCode = promoCode;
//    }
//    
//    
// //----------------------
//    
//    
//    
//    @Column(nullable = false)
//    private String paymentMode; // ONLINE, COD
//
//    @Column(nullable = false)
//    private String paymentStatus = "PENDING"; // PENDING, SUCCESS, FAILED, REFUND_INITIATED
//
//    @Column(nullable = false)
//    private String settlementStatus = "PENDING"; // PENDING, PAID, FAILED
//
//	public String getPaymentMode() {
//		return paymentMode;
//	}
//
//	public void setPaymentMode(String paymentMode) {
//		this.paymentMode = paymentMode;
//	}
//
//	public String getPaymentStatus() {
//		return paymentStatus;
//	}
//
//	public void setPaymentStatus(String paymentStatus) {
//		this.paymentStatus = paymentStatus;
//	}
//
//	public String getSettlementStatus() {
//		return settlementStatus;
//	}
//
//	public void setSettlementStatus(String settlementStatus) {
//		this.settlementStatus = settlementStatus;
//	}
//	
//	
//	// Changes (Ankita ) :- Vendor Order Cancel Reason 
//		@Enumerated(EnumType.STRING)
//		@Column(name = "vendor_cancel_reason")
//		private VendorCancelReason vendorCancelReason;
//
//		@Enumerated(EnumType.STRING)
//		@Column(name = "vendor_accept_cancel_reason")
//		private VendorAcceptThenCancelReason vendorAcceptCancelReason;
//
//		public VendorCancelReason getVendorCancelReason() {
//			return vendorCancelReason;
//		}
//
//		public void setVendorCancelReason(VendorCancelReason vendorCancelReason) {
//			this.vendorCancelReason = vendorCancelReason;
//		}
//
//		public VendorAcceptThenCancelReason getVendorAcceptCancelReason() {
//			return vendorAcceptCancelReason;
//		}
//
//		public void setVendorAcceptCancelReason(VendorAcceptThenCancelReason vendorAcceptCancelReason) {
//			this.vendorAcceptCancelReason = vendorAcceptCancelReason;
//		}
//
//		
//		
//		@Column(name = "cancel_reason", length = 255)
//		private String cancelReason;
//
//		@Column(name = "cancelled_by", length = 20)
//		private String cancelledBy; // CUSTOMER / VENDOR / ADMIN
//
//		@Column(name = "cancelled_at")
//		private LocalDateTime cancelledAt;
//
//		public String getCancelReason() {
//			return cancelReason;
//		}
//
//		public void setCancelReason(String cancelReason) {
//			this.cancelReason = cancelReason;
//		}
//
//		public String getCancelledBy() {
//			return cancelledBy;
//		}
//
//		public void setCancelledBy(String cancelledBy) {
//			this.cancelledBy = cancelledBy;
//		}
//
//		public LocalDateTime getCancelledAt() {
//			return cancelledAt;
//		}
//
//		public void setCancelledAt(LocalDateTime cancelledAt) {
//			this.cancelledAt = cancelledAt;
//		}
//		
//	
//	
//	//---------------
//	
//	
//	
//}

package com.agrowmart.entity.order;

import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.enums.VendorAcceptThenCancelReason;
import com.agrowmart.enums.VendorCancelReason;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", nullable = false)
	private User merchant;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryCharge = BigDecimal.ZERO;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "promo_code", length = 50, nullable = true)
    private String promoCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "payment_mode", length = 10, nullable = false)
    private String paymentMode;

    @Column(name = "payment_status", length = 20, nullable = false)
    private String paymentStatus = "PENDING";

    @Column(name = "settlement_status", length = 20, nullable = false)
    private String settlementStatus = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Column(name = "cancelled_by", length = 20)
    private String cancelledBy;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_cancel_reason")
    private VendorCancelReason vendorCancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_accept_cancel_reason")
    private VendorAcceptThenCancelReason vendorAcceptCancelReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

//    @PrePersist
//    protected void onCreate() {
//        if (createdAt == null) createdAt = LocalDateTime.now();
//        updatedAt = createdAt;
//    }

    
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = "ORD-" + System.currentTimeMillis(); // simple unique string
        }
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getMerchant() { return merchant; }
    public void setMerchant(User merchant) { this.merchant = merchant; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(String settlementStatus) { this.settlementStatus = settlementStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public VendorCancelReason getVendorCancelReason() { return vendorCancelReason; }
    public void setVendorCancelReason(VendorCancelReason reason) { this.vendorCancelReason = reason; }
    public VendorAcceptThenCancelReason getVendorAcceptCancelReason() { return vendorAcceptCancelReason; }
    public void setVendorAcceptCancelReason(VendorAcceptThenCancelReason reason) { this.vendorAcceptCancelReason = reason; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public enum OrderStatus {
        PENDING, ACCEPTED, REJECTED, CANCELLED, DELIVERED
    }
}