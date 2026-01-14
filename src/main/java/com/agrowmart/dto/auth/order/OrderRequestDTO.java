//package com.agrowmart.dto.auth.order;
//
//import java.util.List;
//
//import java.util.List;
//
//public record OrderRequestDTO(
//     Long merchantId,
//     List<OrderItemRequestDTO> items,
//     String promoCode  // ← Change this line
//) {
// // ADD THIS CONSTRUCTOR — THIS IS THE MAGIC!
// public OrderRequestDTO {
//     if (promoCode != null) {
//         promoCode = promoCode.trim().isEmpty() ? null : promoCode.toUpperCase();
//     }
// }
//
//
//}

package com.agrowmart.dto.auth.order;

import java.util.List;

public record OrderRequestDTO(
        Long merchantId,
        List<OrderItemRequestDTO> items,
        String paymentMode,   // <-- THIS WAS MISSING!
        String promoCode
) {
    public OrderRequestDTO {
        if (promoCode != null) {
            promoCode = promoCode.trim().isEmpty() ? null : promoCode.toUpperCase();
        }
        // Optional: validate or normalize paymentMode
        if (paymentMode != null) {
            paymentMode = paymentMode.toUpperCase();
            if (!paymentMode.equals("ONLINE") && !paymentMode.equals("COD")) {
                throw new IllegalArgumentException("paymentMode must be ONLINE or COD");
            }
        }
    }
}