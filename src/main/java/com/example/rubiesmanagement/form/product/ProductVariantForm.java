package com.example.rubiesmanagement.form.product;

import com.example.rubiesmanagement.enums.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductVariantForm {
    private Integer id;
    @NotNull(message = "Màu sắc không được để trống")
    private Integer colorId;

    @NotNull(message = "Kích thước không được để trống")
    private Size size;

    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private List<ProductImageForm> images;
}