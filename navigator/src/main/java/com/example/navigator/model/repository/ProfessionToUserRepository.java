package com.example.navigator.model.repository;

import com.example.navigator.model.ProfessionToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProfessionToUserRepository extends JpaRepository<ProfessionToUser, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM com.example.navigator.model.ProfessionToUser AS ptu WHERE ptu.employeeId = :employeeId")
    void deleteAllByEmployeeId(long employeeId);


    @Query(value = "SELECT ptu FROM com.example.navigator.model.ProfessionToUser AS ptu " +
            "WHERE ptu.employeeId = :employeeId")
    List<ProfessionToUser> findAllByEmployeeId(long employeeId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE com.example.navigator.model.ProfessionToUser AS ptu SET ptu.extendedInfoFromEmployee = " +
            ":extendedInfoFromEmployee " +
            "WHERE ptu.employeeId = :employeeId AND ptu.professionId = :professionId")
    void setExtendedInfoByEmployeeAndProfessionId(long employeeId, long professionId, String extendedInfoFromEmployee);
}
