package com.minswap.hrms.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BorrowHistory.TABLE_NAME)
public class BorrowHistory {
    public static final String TABLE_NAME = "borrow_history";

    public BorrowHistory(Long deviceId, Long personId, Date borrowDate, Date returnDate) {
        this.deviceId = deviceId;
        this.personId = personId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    @Id
    @Column(name = "borrow_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrowHistoryId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "borrow_date")
    private Date borrowDate;

    @Column(name = "return_date")
    private Date returnDate;
}
