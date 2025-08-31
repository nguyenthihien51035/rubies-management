package com.example.rubiesmanagement.form.product;

import com.example.rubiesmanagement.model.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductForm {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "SKU không được để trống")
    private String sku;

    private String description;

    @NotNull(message = "Giá là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá khuyến mãi không hợp lệ")
    private BigDecimal discountPrice;

    @NotNull(message = "Phải chọn danh mục sản phẩm")
    private Integer categoryId;

    private MultipartFile mainImageUrl;

    private Boolean inStock;

    private List<ProductVariantForm> variant;

}