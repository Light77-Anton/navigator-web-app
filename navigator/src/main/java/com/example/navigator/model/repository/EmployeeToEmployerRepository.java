package com.example.navigator.model.repository;
import com.example.navigator.model.EmployeeToEmployer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeToEmployerRepository extends JpaRepository<EmployeeToEmployer, Long> {

    @Query(value = "SELECT FROM com.example.navigator.model.EmployeeToEmployer AS ete " +
            "WHERE ete.employeeId = :employeeId AND btu.employerId = :employerId")
    Optional<EmployeeToEmployer> findByEmployeeAndEmployerId(long employeeId, long employerId);
}
