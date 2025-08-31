package com.example.rubiesmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageUrl;

    private Boolean isMain;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant variant;

    public void setProductVariant(ProductVariant variant) {
        this.variant = variant;
    }
}
