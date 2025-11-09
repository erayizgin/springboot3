package com.pluralsight.ecommerce.service;

import com.pluralsight.ecommerce.dto.AddToCartDto;
import com.pluralsight.ecommerce.dto.CartDto;
import com.pluralsight.ecommerce.dto.CartItemDto;
import com.pluralsight.ecommerce.model.Cart;
import com.pluralsight.ecommerce.model.Product;
import com.pluralsight.ecommerce.model.User;
import com.pluralsight.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepository;

    public void addToCart(AddToCartDto addToCartDto, Product product, User user){
        Cart cart = new Cart(product, addToCartDto.getQuantity(), user);
        cartRepository.save(cart);
    }

    public CartDto listCartItems(User user) {
        List<Cart> cartList = cartRepository.findAllByUserOrderByCreatedDate(user);

        List<CartItemDto> cartItems = new ArrayList<>();
        for (Cart cart:cartList){
            CartItemDto cartItemDto = new CartItemDto(cart);
            cartItems.add(cartItemDto);
        }

        double totalCost = 0;
        for (CartItemDto cartItemDto :cartItems){
            totalCost += cartItemDto.getProduct().getPrice() * cartItemDto.getQuantity();
        }

        return new CartDto(cartItems,totalCost);
    }
}
