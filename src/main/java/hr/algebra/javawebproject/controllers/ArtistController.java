package hr.algebra.javawebproject.controllers;

import hr.algebra.javawebproject.model.dataModel.Artist;
import hr.algebra.javawebproject.repository.IArtistRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Controller
@RequestMapping("/artist")
public class ArtistController {

    private final IArtistRepository artistRepository;

    public ArtistController(IArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @GetMapping("/get")
    public String getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "search", required = false) String search,
                         Model model) {
        Pageable pageable = PageRequest.of(page, 5); // Set page size to 5
        Page<Artist> artistPage;
        if (search != null && !search.isEmpty()) {
            artistPage = artistRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            artistPage = artistRepository.findAll(pageable);
        }
        model.addAttribute("artists", artistPage.getContent());
        model.addAttribute("currentPage", artistPage.getNumber());
        model.addAttribute("totalPages", artistPage.getTotalPages());
        model.addAttribute("search", search);
        return "artist/index";
    }

    @GetMapping("/details")
    public String getDetails(@RequestParam(name = "id") Long id, Model model) {
        Optional<Artist> artistOptional = artistRepository.findById(id);
        if (artistOptional.isPresent()) {
            Artist artist = artistOptional.get();
            model.addAttribute("artist", artist);
            return "artist/details";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "artist/create";
    }

    @PostMapping("/create")
    public String createArtist(@ModelAttribute("artist") Artist artist) {
        artistRepository.save(artist);
        return "redirect:/artist/get";
    }

    @GetMapping("/edit")
    public String showUpdateForm(@RequestParam(name = "id") Long id, Model model) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid artist id: " + id));
        model.addAttribute("artist", artist);
        return "artist/edit";
    }

    @PostMapping("/edit")
    public String updateArtist(@RequestParam(name = "id") Long id, @ModelAttribute("artist") Artist artist,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            artist.setId(id);
            return "artist/edit";
        }

        artistRepository.save(artist);
        return "redirect:/artist/get";
    }

    @GetMapping("/delete")
    public String deleteArtist(@RequestParam(name = "id") Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid artist id: " + id));
        artistRepository.delete(artist);
        return "redirect:/artist/get";
    }




}
