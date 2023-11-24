package com.example.navigator.model.repository;
import com.example.navigator.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE com.example.navigator.model.Location AS l SET l.latitude = :latitude, " +
            "l.longitude = :longitude WHERE l.id = :id")
    void updateLocation(double latitude, double longitude, long id);

    @Query(value = "SELECT l FROM com.example.navigator.model.Location AS l " +
            "INNER JOIN l.user AS u " +
            "INNER JOIN u.employeeData.professions AS p " +
            "WHERE p.id = :professionId")
    List<Location> findAllLocationsWithDesignatedProfession(long professionId);
}
