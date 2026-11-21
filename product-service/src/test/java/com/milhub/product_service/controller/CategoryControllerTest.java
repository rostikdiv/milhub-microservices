package com.milhub.product_service.controller;

import com.milhub.product_service.dto.category.CategoryRequestDTO;
import com.milhub.product_service.dto.category.CategoryResponseDTO;
import com.milhub.product_service.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    @DisplayName("createCategory: delegates to service and returns status 201")
    void createCategory_ShouldDelegateToService() {
        CategoryRequestDTO request = new CategoryRequestDTO("Helmets", "Tactical helmets", null);
        CategoryResponseDTO expected = CategoryResponseDTO.builder()
                .id(3L)
                .name("Helmets")
                .description("Tactical helmets")
                .build();
        when(categoryService.createCategory(request)).thenReturn(expected);

        ResponseEntity<CategoryResponseDTO> response = categoryController.createCategory(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(categoryService).createCategory(request);
    }

    @Test
    @DisplayName("getAllCategories: delegates to service and returns list")
    void getAllCategories_ShouldDelegateToService() {
        CategoryResponseDTO expected = CategoryResponseDTO.builder()
                .id(1L)
                .name("Tactical")
                .build();
        when(categoryService.getAllCategories()).thenReturn(List.of(expected));

        List<CategoryResponseDTO> results = categoryController.getAllCategories();

        assertThat(results).containsExactly(expected);
        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("getCategoryById: delegates to service and returns DTO")
    void getCategoryById_ShouldDelegateToService() {
        CategoryResponseDTO expected = CategoryResponseDTO.builder()
                .id(1L)
                .name("Tactical")
                .build();
        when(categoryService.getCategoryById(1L)).thenReturn(expected);

        CategoryResponseDTO result = categoryController.getCategoryById(1L);

        assertThat(result).isEqualTo(expected);
        verify(categoryService).getCategoryById(1L);
    }
}
