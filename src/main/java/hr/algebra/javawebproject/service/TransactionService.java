package hr.algebra.javawebproject.service;

import hr.algebra.javawebproject.model.dataModel.ArtPiece;
import hr.algebra.javawebproject.model.dataModel.Transaction;
import hr.algebra.javawebproject.repository.IArtPieceRepository;
import hr.algebra.javawebproject.repository.ITransactionRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final ITransactionRepository transactionRepository;

    public TransactionService(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    public Page<Transaction> getFilteredTransactions(String customerName,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime,
                                               int page,
                                               int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("totalAmount").ascending());

        return transactionRepository.findAll((Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customerName != null && !customerName.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("username")), "%" + customerName.toLowerCase() + "%"));
            }

            if (startTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startTime));
            }

            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
