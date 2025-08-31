package com.example.rubiesmanagement.service.impl;

import com.example.rubiesmanagement.dto.response.ColorResponse;
import com.example.rubiesmanagement.exception.BusinessException;
import com.example.rubiesmanagement.exception.ErrorCodeConstant;
import com.example.rubiesmanagement.exception.NotFoundException;
import com.example.rubiesmanagement.form.product.ColorForm;
import com.example.rubiesmanagement.model.Color;
import com.example.rubiesmanagement.repository.ColorRepository;
import com.example.rubiesmanagement.service.ColorService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColorServiceImpl implements ColorService {
    public final ColorRepository colorRepository;
    public final ModelMapper modelMapper;

    public ColorServiceImpl(ColorRepository colorRepository, ModelMapper modelMapper) {
        this.colorRepository = colorRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ColorResponse createColor(ColorForm form) {
        if (colorRepository.existsByName(form.getName())) {
            throw new BusinessException("Màu đã tồn tại", ErrorCodeConstant.COLOR_NAME_ALREADY_EXISTS);
        }

        if (colorRepository.existsByHexCode(form.getHexCode())) {
            throw new BusinessException("Mã hex đã tồn tại", ErrorCodeConstant.HEX_CODE_ALREADY_EXISTS);
        }

        Color color = modelMapper.map(form, Color.class);
        Color saved = colorRepository.save(color);
        return modelMapper.map(saved, ColorResponse.class);
    }

    @Override
    public ColorResponse updateColor(Integer id, ColorForm form) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy màu trong hệ thống", ErrorCodeConstant.COLOR_NOT_FOUND_BY_ID));

        if (!color.getName().equalsIgnoreCase(form.getName())
                && colorRepository.existsByName(form.getName())) {
            throw new BusinessException("Màu đã tồn tại", ErrorCodeConstant.COLOR_NAME_ALREADY_EXISTS);
        }

        if (!color.getHexCode().equalsIgnoreCase(form.getHexCode())
                && colorRepository.existsByHexCode(form.getHexCode())) {
            throw new BusinessException("Mã hex đã tồn tại", ErrorCodeConstant.HEX_CODE_ALREADY_EXISTS);
        }

        modelMapper.map(form, color);
        Color saved = colorRepository.save(color);
        return modelMapper.map(saved, ColorResponse.class);
    }

    @Override
    public void deleteColor(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trong hệ thống", ErrorCodeConstant.COLOR_NOT_FOUND_BY_ID));
        colorRepository.deleteById(id);
    }

    @Override
    public ColorResponse getColorById(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trong hệ thống", ErrorCodeConstant.COLOR_NOT_FOUND_BY_ID));
        return modelMapper.map(color, ColorResponse.class);
    }

    @Override
    public List<ColorResponse> getAllColor() {
        return colorRepository.findAll()
                .stream()
                .map(color -> modelMapper.map(color, ColorResponse.class))
                .collect(Collectors.toList());
    }
}
