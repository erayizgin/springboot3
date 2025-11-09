package com.pluralsight.ecommerce.repository;

import com.pluralsight.ecommerce.model.Cart;
import com.pluralsight.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findAllByUserOrderByCreatedDate(User user);
}
