package com.example.rubiesmanagement.service;

import com.example.rubiesmanagement.dto.response.ColorResponse;
import com.example.rubiesmanagement.form.product.ColorForm;
import jakarta.validation.Valid;

import java.util.List;

public interface ColorService {
    ColorResponse createColor(@Valid ColorForm form);

    ColorResponse updateColor(Integer id, @Valid ColorForm form);

    void deleteColor(Integer id);

    ColorResponse getColorById(Integer id);

    List<ColorResponse> getAllColor();
}
