package hr.algebra.javawebproject.model.dataModel;

import hr.algebra.javawebproject.model.authModel.ApplicationUser;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<TransactionItem> transactionItems;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;
}
