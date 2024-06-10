package hr.algebra.javawebproject.controllers;

import hr.algebra.javawebproject.model.dataModel.ArtPiece;
import hr.algebra.javawebproject.repository.IArtistRepository;
import hr.algebra.javawebproject.repository.ICategoryRepository;
import hr.algebra.javawebproject.service.ArtPieceService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/artpiece")
public class ArtPieceController {

    private final ArtPieceService artPieceService;
    private final ICategoryRepository categoryRepository;
    private final IArtistRepository artistRepository;


    public ArtPieceController(ArtPieceService artPieceService, ICategoryRepository categoryRepository, IArtistRepository artistRepository) {
        this.artPieceService = artPieceService;
        this.categoryRepository = categoryRepository;
        this.artistRepository = artistRepository;
    }

    @GetMapping("/get")
    public String getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artistName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        Page<ArtPiece> artPiecePage = artPieceService.getFilteredArtPieces(title, artistName, categoryName, minPrice, maxPrice, page, size);

        model.addAttribute("artPieces", artPiecePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", artPiecePage.getTotalPages());
        model.addAttribute("totalItems", artPiecePage.getTotalElements());

        return "artpiece/index";
    }

    @GetMapping("/details")
    public String getDetails(@RequestParam(name = "id") Long id, Model model) {
        Optional<ArtPiece> artPieceOptional = artPieceService.getById(id);
        if (artPieceOptional.isPresent()) {
            ArtPiece artPiece = artPieceOptional.get();
            model.addAttribute("artPiece", artPiece);
            return "artpiece/details";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("artPiece", new ArtPiece());
        model.addAttribute("artists",artistRepository.findAll());
        model.addAttribute("categories",categoryRepository.findAll());
        return "artpiece/create";
    }

    @PostMapping("/create")
    public String createArtPiece(@ModelAttribute("artPiece") ArtPiece artPiece, BindingResult result) {
        if (result.hasErrors()) {
            return "artpiece/create";
        }

        artPieceService.save(artPiece);
        return "redirect:/artpiece/get";
    }

    @GetMapping("/edit")
    public String showUpdateForm(@RequestParam(name = "id") Long id, Model model) {
        Optional<ArtPiece> artPieceOptional = artPieceService.getById(id);
        if (artPieceOptional.isPresent()) {
            model.addAttribute("artPiece", artPieceOptional.get());
            model.addAttribute("artists",artistRepository.findAll());
            model.addAttribute("categories",categoryRepository.findAll());
            return "artpiece/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit")
    public String updateArtPiece(@RequestParam(name = "id") Long id, @ModelAttribute("artPiece") ArtPiece artPiece,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            artPiece.setId(id);
            return "artpiece/edit";
        }

        artPieceService.save(artPiece);
        return "redirect:/artpiece/get";
    }

    @GetMapping("/delete")
    public String deleteArtPiece(@RequestParam(name = "id") Long id) {
        ArtPiece artPiece = artPieceService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid artPiece id: " + id));
        artPieceService.delete(artPiece);
        return "redirect:/artpiece/get";
    }

}
