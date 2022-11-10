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
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

    Optional<Person> findPersonByPersonId(Long id);
    Optional<Person> findPersonByRollNumberEquals(String rollNumber);

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
            " and ( :departmentId IS NULL OR p.departmentId = :departmentId) " +
//            " and  (:status IS NULL OR p.status = :status) " +
            " and (:positionId IS NULL OR p.positionId = :positionId)"+
            " and (:managerRoll IS NULL OR p.managerId = :managerRoll)")
    Page<EmployeeListDto> getSearchListPerson(@Param("fullName")String fullName,
                                              @Param("email")String email,
                                              @Param("departmentId")Long departmentId,
                                              @Param("rollNumber")String rollNumber,
//                                              @Param("status")String status,
                                              @Param("positionId")Long positionId,
                                              @Param(("managerRoll"))Long managerRoll,
                                              Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Person p set " +
            "p.status = :status, " +
            "p.fullName = :fullName," +
//            "p.dateOfBirth = :dateOfBirth," +
            "p.managerId = :managerId," +
            "p.departmentId = :departmentId," +
            "p.positionId = :positionId," +
            "p.rankId = :rankId," +
//            "p.onBoardDate = :onBoardDate," +
            "p.citizenIdentification = :citizenIdentification," +
            "p.phoneNumber = :phoneNumber," +
            "p.address = :address," +
            "p.gender = :gender" +
            " where p.rollNumber = :rollNumber")
    Integer updateEmployee(@Param("status") String status,
                                 @Param("fullName") String fullName,
//                                 @Param("dateOfBirth") String dateOfBirth,
                                 @Param("managerId") Long managerId,
                                 @Param("departmentId") Long departmentId,
                                 @Param("positionId") Long positionId,
                                 @Param("rankId") Long rankId,
//                                 @Param("onBoardDate") String onBoardDate,
                                 @Param("citizenIdentification") String citizenIdentification,
                                 @Param("phoneNumber") String phoneNumber,
                                 @Param("address") String address,
                                 @Param("gender") int gender,
                                 @Param("rollNumber") String rollNumber
                                 );

    @Query(" SELECT p.personId " +
           " from Person p " +
           " WHERE p.managerId = :managerId" +
           " AND (:search IS NULL OR p.fullName LIKE %:search%) ")
    Page<Long> getListPersonIdByManagerId(@Param("managerId") Long managerId,
                                          @Param("search") String search,
                                          Pageable pageable);

    @Query("select new com.minswap.hrms.entities.Person(p.personId as personId, p.fullName as fullName) " +
            "from Person p, Person_Role pr, Role r " +
            "where p.personId = pr.pr.personId and pr.pr.roleId = r.roleId and r.roleId = :roleId " +
            "AND (:search IS NULL OR p.fullName LIKE %:search%) ")
    List<Person> getMasterDataAllManager(@Param("roleId") Long roleId,
                                         @Param("search") String search);

    @Modifying
    @Transactional
    @Query("UPDATE Person p set " +
            "p.status = :status " +
            " where p.rollNumber = :rollNumber")
    Integer updateStatusEmployee(@Param("status") String status,
                           @Param("rollNumber") String rollNumber
    );

}
