package com.minswap.hrms.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = Position.TABLE_NAME)
public class Position {
    public static final String TABLE_NAME = "position";

    @Id
    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "position_name")
    private String positionName;

    @Column(name = "department_id")
    private Long departmentId;
}
