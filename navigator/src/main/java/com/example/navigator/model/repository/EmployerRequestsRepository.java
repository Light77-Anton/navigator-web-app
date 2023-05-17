package com.example.navigator.model.repository;
import com.example.navigator.model.EmployerRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRequestsRepository extends JpaRepository<EmployerRequests, Long> {

}
