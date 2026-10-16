package com.shop.ecommerce.product.repository;

import com.shop.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(Long id);

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
