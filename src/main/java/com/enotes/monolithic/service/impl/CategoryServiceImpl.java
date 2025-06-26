package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.CategoryDto;
import com.enotes.monolithic.dto.CategoryResponse;
import com.enotes.monolithic.entity.Category;
import com.enotes.monolithic.exception.ExistDataException;
import com.enotes.monolithic.exception.ResourceNotFoundException;
import com.enotes.monolithic.repository.CategoryRepository;
import com.enotes.monolithic.service.CategoryService;
import com.enotes.monolithic.util.Validation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Validation validation;

    @Override
    public Boolean saveCategory(CategoryDto categoryDto) {

        // Validation Checking
        validation.categoryValidation(categoryDto);

        // check category exist or not
        Boolean exist = categoryRepo.existsByName(categoryDto.getName().trim());
        if (exist) {
            // throw error
            throw new ExistDataException("Category already exist");
        }

        Category category = mapper.map(categoryDto, Category.class);

        if (ObjectUtils.isEmpty(category.getId())) {
            category.setIsDeleted(false);
        } else {
            updateCategory(category);
        }

        Category saveCategory = categoryRepo.save(category);
        if (ObjectUtils.isEmpty(saveCategory)) {
            return false;
        }
        return true;
    }

    private void updateCategory(Category category) {
        Optional<Category> findById = categoryRepo.findById(category.getId());
        if (findById.isPresent()) {
            Category existCategory = findById.get();
            category.setCreatedBy(existCategory.getCreatedBy());
            category.setCreatedOn(existCategory.getCreatedOn());
            category.setIsDeleted(existCategory.getIsDeleted());
        }
    }

    @Override
    public List<CategoryDto> getAllCategory() {
        List<Category> categories = categoryRepo.findAll();

        return categories.stream().map(cat -> mapper.map(cat, CategoryDto.class)).toList();
    }

    @Override
    public List<CategoryResponse> getActiveCategory() {

        List<Category> categories = categoryRepo.findByIsActiveTrueAndIsDeletedFalse();
        return categories.stream().map(cat -> mapper.map(cat, CategoryResponse.class))
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Integer id) throws Exception {

        Category category = categoryRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id=" + id));

        if (!ObjectUtils.isEmpty(category)) {
            category.getName().toUpperCase();
            return mapper.map(category, CategoryDto.class);
        }
        return null;
    }

    @Override
    public Boolean deleteCategory(Integer id) {
        Optional<Category> findByCatgeory = categoryRepo.findById(id);

        if (findByCatgeory.isPresent()) {
            Category category = findByCatgeory.get();
            category.setIsDeleted(true);
            categoryRepo.save(category);
            return true;
        }
        return false;
    }

}
