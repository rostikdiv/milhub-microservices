package com.milhub.product_service.controller;

import com.milhub.product_service.dto.category.CategoryRequestDTO;
import com.milhub.product_service.dto.category.CategoryResponseDTO;
import com.milhub.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing product categories.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new product category.
     *
     * @param request the category creation request
     * @return the created category details
     */
    @PreAuthorize("hasAuthority('product:create')")
    @PostMapping("/")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }

    /**
     * Retrieves all available product categories built hierarchically.
     *
     * @return list of root categories with nested subcategories
     */
    @GetMapping("/")
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param id category ID
     * @return category details
     */
    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }
}
