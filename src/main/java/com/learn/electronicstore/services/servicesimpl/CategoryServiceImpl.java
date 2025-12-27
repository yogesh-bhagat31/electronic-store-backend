package com.learn.electronicstore.services.servicesimpl;

import com.learn.electronicstore.dtos.CategoryDto;
import com.learn.electronicstore.dtos.PageableResponse;
import com.learn.electronicstore.entities.Category;
import com.learn.electronicstore.exceptions.ResourceNotFoundException;
import com.learn.electronicstore.helper.Helper;
import com.learn.electronicstore.repositories.CategoryRepository;
import com.learn.electronicstore.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Value("${category.profile.image.path}")
    private String imagePath;

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;
    private Helper helper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper, Helper helper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.helper = helper;
    }

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        String uniqueId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(uniqueId);
        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found exception!!"));
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);

        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public void delete(String categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found exception!!"));
        String fullPath = imagePath + category.getCoverImage();
        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException e) {
            log.info("Category image not found in folder");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        categoryRepository.delete(category);

    }

    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        PageableResponse<CategoryDto> pageableResponse = helper.getPageableResponse(page, CategoryDto.class);
        return pageableResponse;
    }

    @Override
    public CategoryDto get(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found exception!!"));
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> searchUser(String keyword) {
        List<Category> byTitleContaining = categoryRepository.findByTitleContaining(keyword);
        List<CategoryDto> categoryDtos = byTitleContaining.stream().map(category -> modelMapper.map(category, CategoryDto.class)).collect(Collectors.toList());
        if(categoryDtos.isEmpty()){
            throw  new ResourceNotFoundException("No category found, please enter correct name!");
        }
        return categoryDtos;
    }
}
