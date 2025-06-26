package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.CategoryDto;
import com.enotes.monolithic.dto.CategoryResponse;
import com.enotes.monolithic.service.CategoryService;
import com.enotes.monolithic.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/category")
public class CategoryControllerV1 {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveCategory(@RequestBody CategoryDto categoryDto) {

        Boolean saveCategory = categoryService.saveCategory(categoryDto);
        if (saveCategory) {
            return CommonUtil.createBuildResponseMessage("saved success", HttpStatus.CREATED);
        } else {
            return CommonUtil.createErrorResponseMessage("Category Not saved", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCategory() {
        List<CategoryDto> allCategory = categoryService.getAllCategory();
        if (CollectionUtils.isEmpty(allCategory)) {
            return ResponseEntity.noContent().build();
        } else {
            return CommonUtil.createBuildResponse(allCategory, HttpStatus.OK);
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getActiveCategory() {

        List<CategoryResponse> allCategory = categoryService.getActiveCategory();
        if (CollectionUtils.isEmpty(allCategory)) {
            return ResponseEntity.noContent().build();
        } else {
            return CommonUtil.createBuildResponse(allCategory, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCategortDetailsById(@PathVariable Integer id) throws Exception {

        CategoryDto categoryDto = categoryService.getCategoryById(id);
        if (ObjectUtils.isEmpty(categoryDto)) {
            return CommonUtil.createErrorResponseMessage("Internal Server Error", HttpStatus.NOT_FOUND);
        }
        return CommonUtil.createBuildResponse(categoryDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Integer id) {
        Boolean deleted = categoryService.deleteCategory(id);
        if (deleted) {
            return CommonUtil.createBuildResponse("Category deleted success", HttpStatus.OK);
        }
        return CommonUtil.createErrorResponseMessage("Category Not deleted", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
