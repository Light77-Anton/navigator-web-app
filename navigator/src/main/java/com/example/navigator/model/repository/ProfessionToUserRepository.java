package com.example.navigator.model.repository;
import com.example.navigator.model.ProfessionToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionToUserRepository extends JpaRepository<ProfessionToUser, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM com.example.navigator.model.ProfessionToUser AS ptu WHERE ptu.employee.getId = :employeeId " +
    "ptu.profession.getId = :professionId")
    void deleteByEmployeeAndProfessionId(long employeeId, long professionId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM com.example.navigator.model.ProfessionToUser AS ptu WHERE ptu.employee.getId = :employeeId")
    void deleteAllByEmployeeId(long employeeId);


    @Query(value = "SELECT ptu FROM com.example.navigator.model.ProfessionToUser AS ptu " +
            "WHERE ptu.employee.getId = :employeeId")
    List<ProfessionToUser> findAllByEmployeeId(long employeeId);

    @Query(value = "SELECT ptu FROM com.example.navigator.model.ProfessionToUser AS ptu " +
            "WHERE ptu.employee.getId = :employeeId AND ptu.profession.getId = :professionId")
    Optional<ProfessionToUser> findByEmployeeAndProfessionId(long employeeId, long professionId);


    @Transactional
    @Modifying
    @Query(value = "UPDATE com.example.navigator.model.ProfessionToUser AS ptu SET ptu.extendedInfoFromEmployee = " +
            ":extendedInfoFromEmployee " +
            "WHERE ptu.employee.getId = :employeeId AND ptu.profession.getId = :professionId")
    void setExtendedInfoByEmployeeAndProfessionId(long employeeId, long professionId, String extendedInfoFromEmployee);
}
