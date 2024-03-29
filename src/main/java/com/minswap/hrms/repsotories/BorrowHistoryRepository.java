package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.BorrowHistory;
import com.minswap.hrms.response.dto.BorrowHistoryDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
    @Query("SELECT new com.minswap.hrms.response.dto.BorrowHistoryDto(bh.borrowHistoryId as borrowHistoryId, " +
            "p.rollNumber as rollNumber, p.fullName as fullName, " +
            "dt.deviceTypeName as deviceTypeName, d.deviceName as deviceName, " +
            "d.deviceCode as deviceCode, bh.borrowDate as borrowDate, " +
            "bh.returnDate as returnDate, " +
            "CASE " +
            "When d.status = 1 then 'Using' " +
            "When d.status = 0 then 'Returned' " +
            "else 'Deleted' " +
            "END as status) " +
            "from BorrowHistory bh " +
            "left join Person p on bh.personId = p.personId " +
            "left join Device d on bh.deviceId = d.deviceId " +
            "left join DeviceType dt on d.deviceTypeId = dt.deviceTypeId " +
            "where (:search IS NULL OR p.rollNumber like %:search% OR p.fullName like %:search%) " +
            "and (:deviceTypeId IS NULL OR dt.deviceTypeId = :deviceTypeId) " +
            "and (:managerId IS NULL OR p.managerId = :managerId) " +
            "and (:personId IS NULL OR p.personId = :personId) " +
            "and (:status IS NULL or d.status =:status)")
    Page<BorrowHistoryDto> getBorrowHistoryList(@Param("search") String search,
                                                @Param("deviceTypeId") Long deviceTypeId,
                                                @Param("managerId") Long managerId,
                                                @Param("personId") Long personId,
                                                @Param("status") Integer status,
                                                Pageable pageable);

    @Query("SELECT new com.minswap.hrms.response.dto.BorrowHistoryDto(bh.borrowHistoryId as borrowHistoryId, " +
            "p.rollNumber as rollNumber, p.fullName as fullName, " +
            "dt.deviceTypeName as deviceTypeName, d.deviceName as deviceName, " +
            "d.deviceCode as deviceCode, bh.borrowDate as borrowDate, " +
            "bh.returnDate as returnDate, " +
            "CASE " +
            "When d.status = 1 then 'Using' " +
            "When d.status = 0 then 'Returned' " +
            "else 'Deleted' " +
            "END as status) " +
            "from BorrowHistory bh " +
            "left join Person p on bh.personId = p.personId " +
            "left join Device d on bh.deviceId = d.deviceId " +
            "left join DeviceType dt on d.deviceTypeId = dt.deviceTypeId " +
            "where bh.borrowHistoryId = :borrowHistoryId")
    BorrowHistoryDto getBorrowHistoryDetail(@Param("borrowHistoryId") Long borrowHistoryId);
}
