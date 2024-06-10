package hr.algebra.javawebproject.repository;

import hr.algebra.javawebproject.model.dataModel.ArtPiece;
import hr.algebra.javawebproject.model.dataModel.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface IArtPieceRepository extends JpaRepository<ArtPiece, Long>, JpaSpecificationExecutor<ArtPiece> {

}