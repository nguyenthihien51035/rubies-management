package com.example.rubiesmanagement.repository;

import com.example.rubiesmanagement.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Integer> {
    boolean existsByName(String name);

    boolean existsByHexCode(String hexCode);

    Optional<Color> findByNameIgnoreCase(String name);
}
