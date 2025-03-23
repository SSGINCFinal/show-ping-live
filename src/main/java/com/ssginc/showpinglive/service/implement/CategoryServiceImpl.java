package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.entity.Category;
import com.ssginc.showpinglive.repository.CategoryRepository;
import com.ssginc.showpinglive.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}