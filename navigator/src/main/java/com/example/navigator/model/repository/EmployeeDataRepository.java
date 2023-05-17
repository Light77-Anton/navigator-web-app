package com.example.navigator.model.repository;
import com.example.navigator.model.EmployeeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDataRepository extends JpaRepository<EmployeeData, Long> {
}
