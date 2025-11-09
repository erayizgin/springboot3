package com.pluralsight.ecommerce.service;

import com.pluralsight.ecommerce.dto.CartDto;
import com.pluralsight.ecommerce.dto.CartItemDto;
import com.pluralsight.ecommerce.model.Order;
import com.pluralsight.ecommerce.model.User;
import com.pluralsight.ecommerce.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${stripe.secret.key}")
    private String secretKey;

    SessionCreateParams.LineItem.PriceData createPriceData(CartItemDto cartItemDto) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount(((long) cartItemDto.getProduct().getPrice()) * 100)
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(cartItemDto.getProduct().getName())
                                .build())
                .build();
    }

    SessionCreateParams.LineItem createSessionLineItem(CartItemDto cartItemDto) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(cartItemDto))
                .setQuantity(Long.parseLong(String.valueOf(cartItemDto.getQuantity())))
                .build();
    }

    public Session createSession() throws StripeException {

        Stripe.apiKey = secretKey;
        List<SessionCreateParams.LineItem> sessionItemsList = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        CartDto cartDto = cartService.listCartItems(user);

        for (CartItemDto cartItemDto : cartDto.getcartItems()) {
            sessionItemsList.add(createSessionLineItem(cartItemDto));
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl("http://localhost:8080/#/")
                .addAllLineItem(sessionItemsList)
                .setSuccessUrl("http://localhost:8080/#/")
                .build();
        return Session.create(params);
    }

    public void createOrder(String sessionId, String status, String metadata) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Order order = new Order(sessionId, user, metadata, status);
        orderRepository.save(order);
    }

}
