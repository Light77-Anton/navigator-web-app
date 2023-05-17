package com.example.navigator.model.repository;
import com.example.navigator.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {

    /*
    @Query(value = "SELECT p FROM com.example.navigator.model.Profession AS p WHERE p.name = :name")
    Optional<Profession> findByName(String name);

     */
}
