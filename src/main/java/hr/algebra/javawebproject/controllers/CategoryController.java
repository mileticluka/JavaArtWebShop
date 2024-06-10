package hr.algebra.javawebproject.controllers;

import hr.algebra.javawebproject.model.dataModel.Category;
import hr.algebra.javawebproject.repository.ICategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final ICategoryRepository categoryRepository;

    public CategoryController(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/get")
    public String getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "search", required = false) String search,
                         Model model) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Category> categoryPage;
        if (search != null && !search.isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", categoryPage.getNumber());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("search", search);
        return "category/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/create";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute("category") Category category) {


        categoryRepository.save(category);
        return "redirect:/category/get";
    }

    @GetMapping("/edit")
    public String showUpdateForm(@RequestParam(name = "id") Long id, Model model) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category id: " + id));
        model.addAttribute("category", category);
        return "category/edit";
    }

    @PostMapping("/edit")
    public String updateCategory(@RequestParam(name = "id") Long id, @ModelAttribute("category") Category category,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "category/edit";
        }

        categoryRepository.save(category);
        return "redirect:/category/get";
    }

    @GetMapping("/delete")
    public String deleteCategory(@RequestParam(name = "id") Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category id: " + id));
        categoryRepository.delete(category);
        return "redirect:/category/get";
    }
}
