package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.entity.Category;
import com.ssginc.showpinglive.repository.CategoryRepository;
import com.ssginc.showpinglive.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}