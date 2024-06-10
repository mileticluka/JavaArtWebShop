package hr.algebra.javawebproject.repository;

import hr.algebra.javawebproject.model.dataModel.Category;
import hr.algebra.javawebproject.model.dataModel.TransactionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITransactionItemRepository extends JpaRepository<TransactionItem, Long> {
    List<TransactionItem> findAllByTransaction_Id(long Id);

}