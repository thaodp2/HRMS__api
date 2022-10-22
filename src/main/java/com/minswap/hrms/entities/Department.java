package com.minswap.hrms.entities;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = Department.TABLE_NAME)
public class Department {
    public static final String TABLE_NAME = "department";

    public Department(String departmentName) {
        this.departmentName = departmentName;
    }

    @Id
    @Column(name = "department_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(name = "department_name")
    private String departmentName;
}
