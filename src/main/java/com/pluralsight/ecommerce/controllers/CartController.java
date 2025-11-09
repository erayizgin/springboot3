package com.pluralsight.ecommerce.controllers;

import com.pluralsight.ecommerce.common.ApiResponse;
import com.pluralsight.ecommerce.dto.AddToCartDto;
import com.pluralsight.ecommerce.dto.CartDto;
import com.pluralsight.ecommerce.model.Product;
import com.pluralsight.ecommerce.model.User;
import com.pluralsight.ecommerce.service.CartService;
import com.pluralsight.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    ProductService productService;

    @Autowired
    CartService cartService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse> addToCart(@RequestBody AddToCartDto addToCartDto) throws Exception {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Product product = productService.getProductById(addToCartDto.getProductId());
        cartService.addToCart(addToCartDto, product, user);
        return ResponseEntity.ok(new ApiResponse(true, "added to cart"));
    }

    @GetMapping("/")
    public ResponseEntity<CartDto> getCartItems() {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        CartDto cartDto = cartService.listCartItems(user);
        return new ResponseEntity<CartDto>(cartDto, HttpStatus.OK);
    }
}
