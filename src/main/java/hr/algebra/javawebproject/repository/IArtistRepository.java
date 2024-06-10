package hr.algebra.javawebproject.repository;

import hr.algebra.javawebproject.model.authModel.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import hr.algebra.javawebproject.model.dataModel.Artist;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface IArtistRepository extends JpaRepository<Artist, Long> {
    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);
}