package com.example.rubiesmanagement.repository.specification;

import com.example.rubiesmanagement.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<Product> hasName(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return null;
            return cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasCategory(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null)
                return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasSku(String sku) {
        return (root, query, cb) -> {
            if (sku == null || sku.isBlank())
                return null;
            return cb.equal(root.get("sku"), sku);
        };
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("price"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("price"), max);
            }
            return null;
        };
    }

    // Lọc sản phẩm có giảm giá
    public static Specification<Product> hasDiscount(Boolean hasDiscount) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(hasDiscount)) {
                return cb.lessThan(root.get("discountPrice"), root.get("price"));
            }
            return null;
        };
    }

    public static Specification<Product> isInStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(inStock)) {
                return cb.isTrue(root.get("inStock"));
            }
            return null;
        };
    }
}
