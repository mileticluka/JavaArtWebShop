package hr.algebra.javawebproject.service;

import hr.algebra.javawebproject.model.dataModel.ArtPiece;
import hr.algebra.javawebproject.model.dataModel.Artist;
import hr.algebra.javawebproject.repository.IArtPieceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArtPieceService {

    private final IArtPieceRepository artPieceRepository;

    public ArtPieceService(IArtPieceRepository artPieceRepository) {
        this.artPieceRepository = artPieceRepository;
    }

    public Optional<ArtPiece> getById(long id)
    {
        return artPieceRepository.findById(id);
    }

    public Page<ArtPiece> getFilteredArtPieces(String title,
                                               String artistName,
                                               String categoryName,
                                               Double minPrice,
                                               Double maxPrice,
                                               int page,
                                               int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        return artPieceRepository.findAll((Root<ArtPiece> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (artistName != null && !artistName.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("artist").get("name")), "%" + artistName.toLowerCase() + "%"));
            }

            if (categoryName != null && !categoryName.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")), "%" + categoryName.toLowerCase() + "%"));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public void save(ArtPiece artPiece) {
        artPieceRepository.save(artPiece);
    }

    public Optional<ArtPiece> findById(Long id) {
        return artPieceRepository.findById(id);
    }

    public void delete(ArtPiece artPiece) {
        artPieceRepository.delete(artPiece);
    }
}
