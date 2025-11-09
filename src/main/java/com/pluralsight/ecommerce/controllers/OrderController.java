package com.pluralsight.ecommerce.controllers;

import com.pluralsight.ecommerce.common.ApiResponse;
import com.pluralsight.ecommerce.dto.StripeResponse;
import com.pluralsight.ecommerce.service.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeResponse> checkoutList() throws StripeException {
        Session session = orderService.createSession();
        System.out.println(session);
        StripeResponse stripeResponse = new StripeResponse(session.getUrl());
        orderService.createOrder(session.getId(), "pending", "");
        return new ResponseEntity<StripeResponse>(stripeResponse, HttpStatus.OK);
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> processWebhook(@RequestBody Map<String, Object> payload) {
        try {

            String eventType = (String) payload.get("type");
            if (!"checkout.session.completed".equals(eventType)) {
                return ResponseEntity.ok("Ignored event type: " + eventType);
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> object = (Map<String, Object>) data.get("object");

            String sessionId = (String) object.get("id");
            String paymentStatus = (String) object.get("payment_status");

            /*
             * CHALLENGE TODO:
             * 1. Update order status to paid
             * 2. Update metadata of order with the stringified json of array of cart items so
             * that we can be shown in the order details page
             * 3. Clear cart for the user
            */ 

            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }
    }
}
