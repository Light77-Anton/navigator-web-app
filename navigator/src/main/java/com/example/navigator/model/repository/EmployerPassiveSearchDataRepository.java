package com.example.navigator.model.repository;
import com.example.navigator.model.EmployerPassiveSearchData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerPassiveSearchDataRepository extends JpaRepository<EmployerPassiveSearchData, Long> {
}
