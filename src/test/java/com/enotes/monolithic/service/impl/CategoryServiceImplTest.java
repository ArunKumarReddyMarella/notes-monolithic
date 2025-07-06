package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.CategoryDto;
import com.enotes.monolithic.entity.Category;
import com.enotes.monolithic.exception.ExistDataException;
import com.enotes.monolithic.repository.CategoryRepository;
import com.enotes.monolithic.util.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private Validation validation;

    private CategoryDto categoryDto=null;
    private Category category=null;
    private List<Category> categories=new ArrayList<>();
    private List<CategoryDto> categoriesDto=new ArrayList<>();

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void initalize()
    {
        categoryDto=CategoryDto.builder()
                .id(null)
                .name("Java Notes")
                .description("java notes")
                .isActive(true).build();


        category=Category.builder()
                .id(null)
                .name("Java Notes")
                .description("java notes")
                .isActive(true)
                .isDeleted(false)
                .build();

        categories.add(category);
        categoriesDto.add(categoryDto);

    }

    @Test
    public void testSaveCategory() {
        // arrange
        when(categoryRepo.existsByName(categoryDto.getName())).thenReturn(false);
        when(mapper.map(categoryDto, Category.class)).thenReturn(category);
        when(categoryRepo.save(category)).thenReturn(category);

        // act
        Boolean saveCategory = categoryService.saveCategory(categoryDto);

        // assert
        assertTrue(saveCategory);

        // verify
        verify(validation).categoryValidation(categoryDto);
        verify(categoryRepo).existsByName(categoryDto.getName());
        verify(categoryRepo).save(category);
    }

    @Test
    public void testCategoryExist()
    {
        when(categoryRepo.existsByName(categoryDto.getName())).thenReturn(true);
        ExistDataException exception = assertThrows(ExistDataException.class, ()->{
            categoryService.saveCategory(categoryDto);
        });

        assertEquals("Category already exist", exception.getMessage());
        verify(validation).categoryValidation(categoryDto);
        verify(categoryRepo).existsByName(categoryDto.getName());
        verify(categoryRepo,never()).save(category);
    }

    @Test
    public void testUpdateCategory() {
        categoryDto.setId(1);
        category.setId(1);

        // arrange
        when(categoryRepo.existsByName(categoryDto.getName())).thenReturn(false);
        when(mapper.map(categoryDto, Category.class)).thenReturn(category);
        when(categoryRepo.save(category)).thenReturn(category);

        // act
        Boolean saveCategory = categoryService.saveCategory(categoryDto);

        // assert
        assertTrue(saveCategory);

        // verify
        verify(validation).categoryValidation(categoryDto);
        verify(categoryRepo).existsByName(categoryDto.getName());
        verify(categoryRepo).save(category);
    }

    @Test
    public void testGetAllCategory() {
        when(categoryRepo.findByIsDeletedFalse()).thenReturn(categories);
        List<CategoryDto> allCategory = categoryService.getAllCategory();

        assertEquals(allCategory.size(), categories.size());
        verify(categoryRepo).findByIsDeletedFalse();
    }
}