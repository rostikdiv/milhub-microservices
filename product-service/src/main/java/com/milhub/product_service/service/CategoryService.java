package com.milhub.product_service.service;

import com.milhub.product_service.dto.category.CategoryRequestDTO;
import com.milhub.product_service.dto.category.CategoryResponseDTO;
import com.milhub.product_service.entity.Category;
import com.milhub.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing product categories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Creates a new product category.
     *
     * @param request the category creation request
     * @return the created category details
     */
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        // 1. Check for duplicates
        if (categoryRepository.findByName(request.name()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        Category parent = null;
        if (request.parentId() != null) {
            parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category not found"));
        }

        // 2. Map DTO -> Entity
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .parent(parent)
                .build();

        // 3. Save to database
        Category savedCategory = categoryRepository.save(category);

        // 4. Map Entity -> Response DTO
        return mapToResponse(savedCategory);
    }

    /**
     * Retrieves all available product categories built hierarchically in memory (single SQL query).
     *
     * @return list of root categories with nested subcategories
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAll();

        // Group child categories by their parent ID in memory to prevent N+1 lazy queries
        Map<Long, List<Category>> childrenByParentId = allCategories.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return allCategories.stream()
                .filter(c -> c.getParent() == null) // Root categories
                .map(root -> mapToTreeResponse(root, childrenByParentId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param id category ID
     * @return category details
     */
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private CategoryResponseDTO mapToTreeResponse(Category category, Map<Long, List<Category>> childrenByParentId) {
        List<Category> children = childrenByParentId.getOrDefault(category.getId(), Collections.emptyList());
        List<CategoryResponseDTO> subCats = children.isEmpty() ? null : children.stream()
                .map(child -> mapToTreeResponse(child, childrenByParentId))
                .collect(Collectors.toList());

        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .subCategories(subCats)
                .build();
    }

    private CategoryResponseDTO mapToResponse(Category category) {
        List<CategoryResponseDTO> subCats = null;
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            subCats = category.getSubCategories().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .subCategories(subCats)
                .build();
    }
}
