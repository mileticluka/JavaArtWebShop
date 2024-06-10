package hr.algebra.javawebproject.repository;

import hr.algebra.javawebproject.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILogRepository extends JpaRepository<Log, Long> {
}