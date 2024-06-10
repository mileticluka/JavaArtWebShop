package hr.algebra.javawebproject.repository;

import hr.algebra.javawebproject.model.authModel.ApplicationUser;
import hr.algebra.javawebproject.model.dataModel.Artist;
import hr.algebra.javawebproject.model.dataModel.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);


}