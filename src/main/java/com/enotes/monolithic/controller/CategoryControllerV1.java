package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.CategoryDto;
import com.enotes.monolithic.dto.CategoryResponse;
import com.enotes.monolithic.service.CategoryService;
import com.enotes.monolithic.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.enotes.monolithic.util.Constants.*;

@Tag(name = "Category", description = "All the Category operation APIs")
@RestController
@RequestMapping("/api/v1/category")
public class CategoryControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(CategoryControllerV1.class);

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Save Category", tags = { "Category" }, description = "Admin Save Category")
    @PostMapping("/save")
    @PreAuthorize(ROLE_ADMIN)
    public ResponseEntity<?> saveCategory(@RequestBody CategoryDto categoryDto) {
        logger.info("Received request to save category : {}", categoryDto);
        Boolean saveCategory = categoryService.saveCategory(categoryDto);
        if (saveCategory) {
            return CommonUtil.createBuildResponseMessage("saved success", HttpStatus.CREATED);
        } else {
            return CommonUtil.createErrorResponseMessage("Category Not saved", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get All Category", tags = { "Category" }, description = "Admin Get All Category")
    @GetMapping("/")
    @PreAuthorize(ROLE_ADMIN)
    public ResponseEntity<?> getAllCategory() {
        logger.info("Received request to get all category");
        List<CategoryDto> allCategory = categoryService.getAllCategory();
        if (CollectionUtils.isEmpty(allCategory)) {
            return ResponseEntity.noContent().build();
        } else {
            return CommonUtil.createBuildResponse(allCategory, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get Active Category", tags = { "Category" }, description = "Admin,User Get Active Category")
    @GetMapping("/active")
    @PreAuthorize(ROLE_ADMIN_USER)
    public ResponseEntity<?> getActiveCategory() {
        logger.info("Received request to get active category");
        List<CategoryResponse> allCategory = categoryService.getActiveCategory();
        if (CollectionUtils.isEmpty(allCategory)) {
            return ResponseEntity.noContent().build();
        } else {
            return CommonUtil.createBuildResponse(allCategory, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get Category By id ", tags = { "Category" }, description = "Admin Get Category Deatils")
    @GetMapping("/{id}")
    @PreAuthorize(ROLE_USER)
    public ResponseEntity<?> getCategortDetailsById(@PathVariable Integer id) throws Exception {
        logger.info("Received request to get category by id : {}", id);
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        if (ObjectUtils.isEmpty(categoryDto)) {
            return CommonUtil.createErrorResponseMessage("Internal Server Error", HttpStatus.NOT_FOUND);
        }
        return CommonUtil.createBuildResponse(categoryDto, HttpStatus.OK);
    }

    @Operation(summary = "Delete Category", tags = { "Category" }, description = "Admin Delete Category")
    @DeleteMapping("/{id}")
    @PreAuthorize(ROLE_ADMIN)
    public ResponseEntity<?> deleteCategoryById(@PathVariable Integer id) {
        logger.info("Received request to delete category by id : {}", id);
        Boolean deleted = categoryService.deleteCategory(id);
        if (deleted) {
            return CommonUtil.createBuildResponse("Category deleted success", HttpStatus.OK);
        }
        return CommonUtil.createErrorResponseMessage("Category Not deleted", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
