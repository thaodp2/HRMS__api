package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findPersonByEmail(String email);

    Optional<Person> findPersonByPersonId(Long id);

    Optional<Person> findPersonByRollNumberEquals(String rollNumber);

    List<Person> findByRankIdIsNot(Long rankId);

    @Query("select p.personId from Person p")
    List<Long> getAllPersonId();

    @Query("select new com.minswap.hrms.response.dto.EmployeeDetailDto(" +
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
            " p.managerId as managerId, " +
            "p.avatarImg as avatarImg," +
            "p.salaryBasic as salaryBasic," +
            "p.salaryBonus as salaryBonus," +
            "p3.fullName as managerName," +
            "d.departmentName as departmentName," +
            "p2.positionName as positionName," +
            "r.rankName as rankingName) " +
            "FROM Person p " +
            "left join Department d on p.departmentId = d.departmentId " +
            "left join Position p2 on p.positionId = p2.positionId " +
            "left join Rank r on p.rankId = r.rankId " +
            "left join Person p3 on p.managerId = p3.personId " +
            "WHERE p.rollNumber = :rollNumber")
    EmployeeDetailDto getDetailEmployee(@Param("rollNumber") String rollNumber);

    @Query("select new com.minswap.hrms.response.dto.EmployeeListDto(" +
            "  p.personId as personId, p.fullName as fullName,p.email as email,d.departmentName as departmentName,p.rollNumber as rollNumber," +
            "  p.status as active, p2.positionName as positionName )" +
            "  FROM Person p " +
            "  LEFT JOIN Department d on " +
            "    p.departmentId = d.departmentId " +
            "    LEFT JOIN Position p2 ON " +
            "    p.positionId = p2.positionId  " +
            "     where  1 = 1  " +
            " and ((:fullName IS NULL OR p.fullName LIKE  %:fullName%)" +
            " or (:rollNumber IS NULL OR p.rollNumber LIKE %:rollNumber%))" +
            " and (:email IS NULL OR p.email LIKE %:email%) " +
            " and (:departmentId IS NULL OR p.departmentId = :departmentId) " +
            " and (:positionId IS NULL OR p.positionId = :positionId)" +
            " and (:status IS NULL OR p.status = :status)" +
            " and (:managerRoll IS NULL OR p.managerId = :managerRoll)")
    Page<EmployeeListDto> getSearchListPerson(@Param("fullName") String fullName,
                                              @Param("email") String email,
                                              @Param("departmentId") Long departmentId,
                                              @Param("rollNumber") String rollNumber,
                                              @Param("positionId") Long positionId,
                                              @Param(("managerRoll")) Long managerRoll,
                                              @Param(("status")) String status,
                                              Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Person p set " +
            "p.fullName = :fullName," +
//            "p.dateOfBirth = :dateOfBirth," +
            "p.managerId = :managerId," +
            "p.departmentId = :departmentId," +
            "p.positionId = :positionId," +
            "p.rankId = :rankId," +
            "p.citizenIdentification = :citizenIdentification," +
            "p.phoneNumber = :phoneNumber," +
            "p.address = :address," +
            "p.gender = :gender," +
            "p.salaryBasic = :salaryBasic," +
            "p.salaryBonus = :salaryBonus," +
            "p.onBoardDate = :onBoardDate," +
            "p.status = :status" +
            " where p.rollNumber = :rollNumber")
    Integer updateEmployee(
            @Param("fullName") String fullName,
//                                 @Param("dateOfBirth") String dateOfBirth,
            @Param("managerId") Long managerId,
            @Param("departmentId") Long departmentId,
            @Param("positionId") Long positionId,
            @Param("rankId") Long rankId,
            @Param("citizenIdentification") String citizenIdentification,
            @Param("phoneNumber") String phoneNumber,
            @Param("address") String address,
            @Param("gender") int gender,
            @Param("rollNumber") String rollNumber,
            @Param("salaryBasic") Double salaryBasic,
            @Param("salaryBonus") Double salaryBonus,
            @Param("onBoardDate") Date onBoardDate,
            @Param("status") String status
    );

    @Query(" SELECT p.personId " +
            " from Person p " +
            " WHERE p.managerId = :managerId" +
            " AND (:search IS NULL OR p.fullName LIKE %:search%) ")
    Page<Long> getListPersonIdByManagerId(@Param("managerId") Long managerId,
                                          @Param("search") String search,
                                          Pageable pageable);

    @Query(" SELECT p.personId " +
            " from Person p " +
            " WHERE 1 = 1 " +
            " AND (:search IS NULL OR p.fullName LIKE %:search%) ")
    Page<Long> getListPersonIdByFullName(@Param("search") String search,
                                         Pageable pageable);

    @Query("select new com.minswap.hrms.entities.Person(p.personId as personId, p.fullName as fullName) " +
            "from Person p, PersonRole pr, Role r " +
            "where p.personId = pr.personId and pr.roleId = r.roleId and r.roleId = :roleId " +
            "AND (:search IS NULL OR p.fullName LIKE %:search%) ")
    List<Person> getMasterDataPersonByRole(@Param("roleId") Long roleId,
                                           @Param("search") String search);

    @Query("select new com.minswap.hrms.entities.Person(p.personId as personId, p.fullName as fullName, p.rollNumber as rollNumber) " +
            "from Person p, PersonRole pr, Role r " +
            "where p.personId = pr.personId and pr.roleId = r.roleId and r.roleId = :roleId " +
            "AND (:search IS NULL OR p.rollNumber like %:search% OR p.fullName like %:search%) " +
            "AND (:departmentId IS NULL OR p.departmentId =:departmentId)")
    List<Person> getMasterDataManagerToCreate(@Param("roleId") Long roleId,
                                                  @Param("search") String search,
                                                  @Param("departmentId") Long departmentId);

    @Modifying
    @Transactional
    @Query("UPDATE Person p set " +
            "p.status = :status " +
            " where p.rollNumber = :rollNumber")
    Integer updateStatusEmployee(@Param("status") String status,
                                 @Param("rollNumber") String rollNumber
    );

    @Query("select count(p.personId) from Person p where p.departmentId=:departmentId")
    Integer getNumberOfEmplInDepartment(@Param("departmentId") Long departmentId);

    @Query("SELECT p FROM Person p WHERE p.email = :email AND p.status = 1")
    Person getUserByEmail(@Param("email") String email);

    @Query("select p.rollNumber from Person p where p.personId=:personId")
    String getRollNumberByPersonId(@Param("personId") Long personId);

    @Query("select p.fullName from Person p where p.personId=:personId")
    String getPersonNameByPersonId(@Param("personId") Long personId);

    @Query("select p.managerId from Person p where p.personId=:personId")
    Long getManagerIdByPersonId(@Param("personId") Long personId);

    @Modifying
    @Transactional
    @Query("UPDATE Person p set " +
            "p.annualLeaveBudget = :annualLeaveBudget" +
            " where p.rollNumber = :rollNumber")
    Integer updateAnnualLeaveBudget(@Param("annualLeaveBudget") Double annualLeaveBudget,
                                    @Param("rollNumber") String rollNumber);

    @Query("SELECT count(p.citizenIdentification) FROM Person p WHERE p.citizenIdentification = :citizenIdentification")
    Integer getUserByCitizenIdentification(@Param("citizenIdentification") String citizenIdentification);

    @Query("select r.roleId from Person p " +
            "left join PersonRole pr on p.personId = pr.personId " +
            "left join Role r on r.roleId = pr.roleId " +
            "where p.personId=:personId")
    List<Long> getListRoleIdByPersonId(@Param("personId") Long personId);

    @Query("select p.personId from Person p where p.positionId=:id and p.status =:status")
    List<Long> getListPersonIdByPositionId(@Param("id") Long id,
                                           @Param("status") String status);
}
