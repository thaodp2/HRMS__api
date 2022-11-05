package com.minswap.hrms.repsotories;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.minswap.hrms.entities.Person;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

    Optional<Person> findPersonByPersonId(Long id);
    Optional<Person> findPersonByRollNumberEquals(String rollNumber);
    @Query("select new com.minswap.hrms.response.dto.EmployeeListDto(" +
            "  p.personId as personId, p.fullName as fullName,p.email as email,d.departmentName as departmentName,p.rollNumber as rollNumber," +
            "  p.status as active, p2.positionName as positionName )" +
            "  FROM Person p " +
            "  LEFT JOIN Department d on " +
            "    p.departmentId = d.departmentId " +
            "    LEFT JOIN Position p2 ON " +
            "    p.positionId = p2.positionId  " +
            "     where  1 = 1 ")
    Page<EmployeeListDto> getListPerson(Pagination pageable);
    @Value("a")
    default String getListPersonQuery() {
        StringBuilder queryBuilder = new StringBuilder();
        String s=
        "select new com.minswap.hrms.response.dto.EmployeeListDto(" +
                "  p.personId as personId, p.fullName as fullName,p.email as email,d.departmentName as departmentName,p.rollNumber as rollNumber," +
                "  p.status as status, p2.positionName as positionName )" +
                " FROM Person p " +
                "LEFT JOIN Department d on " +
                " p.departmentId = d.departmentId " +
                "LEFT JOIN Position p2 ON " +
                " p.positionId = p2.positionId  " +
                " where  1 = 1 ";
        return s;
    }
    @Query("select new com.minswap.hrms.response.dto.EmployeeDetailDto("+
            " p.personId as personId," +
            " p.fullName as fullName," +
            " p.dateOfBirth as dateOfBirth," +
            " p.gender as gender," +
            " p.phoneNumber as phoneNumber," +
            " p.citizenIdentification as citizenIdentification," +
            " p.address as address," +
            " p.rollNumber as rollNumber," +
            " p.email as email," +
            " p.departmentId as departmentId," +
            " p.positionId as positionId," +
            " p.rankId as rankId," +
            " p.onBoardDate as onBoardDate," +
            " p.status as status," +
            " p.rollNumber as rollNumber, " +
            "p.managerId as managerId ) " +
            " FROM Person p " +
            " WHERE p.rollNumber = :rollNumber")
    EmployeeDetailDto getDetailEmployee(@Param("rollNumber") String rollNumber);

    @Query("select new com.minswap.hrms.response.dto.EmployeeListDto(" +
            "  p.personId as personId, p.fullName as fullName,p.email as email,d.departmentName as departmentName,p.rollNumber as rollNumber," +
            "  p.status as active, p2.positionName as positionName )" +
            "  FROM Person p " +
            "  LEFT JOIN Department d on " +
            "    p.departmentId = d.departmentId " +
            "    LEFT JOIN Position p2 ON " +
            "    p.positionId = p2.positionId  " +
            "     where  1 = 1  "+
            " and ( :fullName IS NULL OR p.fullName LIKE  %:fullName%)" +
            " and (:rollNumber IS NULL OR p.rollNumber LIKE %:rollNumber%)" +
            " and (:email IS NULL OR p.email LIKE %:email%) " +
            " and ( :departmentName IS NULL OR d.departmentName LIKE %:departmentName%) " +
//            " and  (:status IS NULL OR p.status = :status) " +
            " and (:positionName IS NULL OR p2.positionName LIKE %:positionName%)"+
            " and (:managerRoll IS NULL OR p.managerId = :managerRoll)")
    Page<EmployeeListDto> getSearchListPerson(@Param("fullName")String fullName,
                                              @Param("email")String email,
                                              @Param("departmentName")String departmentName,
                                              @Param("rollNumber")String rollNumber,
//                                              @Param("status")String status,
                                              @Param("positionName")String positionName,
                                              @Param(("managerRoll"))Long managerRoll,
                                              Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Person p set p.status = :status where p.rollNumber LIKE %:personId%")
    Integer updateStatusEmployee(@Param("status") String status,
                             @Param("personId") String personId);


}
