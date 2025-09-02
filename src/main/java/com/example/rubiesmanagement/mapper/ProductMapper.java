package com.example.rubiesmanagement.mapper;

import com.example.rubiesmanagement.dto.response.CategoryResponse;
import com.example.rubiesmanagement.dto.response.ProductImageResponse;
import com.example.rubiesmanagement.dto.response.ProductResponse;
import com.example.rubiesmanagement.dto.response.ProductVariantResponse;
import com.example.rubiesmanagement.model.Product;
import com.example.rubiesmanagement.model.ProductImage;
import com.example.rubiesmanagement.model.ProductVariant;

import java.util.stream.Collectors;

public class ProductMapper {
    public static ProductResponse toResponse(Product product) {
        if (product == null) return null;

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setPrice(product.getPrice());
        response.setDiscountPrice(product.getDiscountPrice());
        response.setInStock(product.getInStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        // category
        if (product.getCategory() != null) {
            CategoryResponse catRes = new CategoryResponse();
            catRes.setId(product.getCategory().getId());
            catRes.setName(product.getCategory().getName());
            catRes.setImage(product.getCategory().getImage());
            response.setCategory(catRes);
        }

        // variants
        if (product.getVariants() != null) {
            response.setVariants(product.getVariants().stream()
                    .map(ProductMapper::toVariantResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private static ProductVariantResponse toVariantResponse(ProductVariant variant) {
        ProductVariantResponse vRes = new ProductVariantResponse();
        vRes.setId(variant.getId());
        vRes.setColorName(variant.getColor().getName());
        vRes.setSize(variant.getSize());
        vRes.setQuantity(variant.getQuantity());

        if (variant.getImages() != null) {
            vRes.setImages(variant.getImages().stream()
                    .map(ProductMapper::toImageResponse)
                    .collect(Collectors.toList()));
        }

        return vRes;
    }

    private static ProductImageResponse toImageResponse(ProductImage img) {
        ProductImageResponse iRes = new ProductImageResponse();
        iRes.setId(img.getId());
        iRes.setImageUrl(img.getImageUrl());
        iRes.setIsMain(img.getIsMain());
        return iRes;
    }
}
