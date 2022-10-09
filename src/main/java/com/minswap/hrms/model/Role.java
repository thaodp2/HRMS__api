package com.minswap.hrms.model;

import com.minswap.hrms.repository.RoleRespository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name = Role.TABLE_NAME)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    public static final String TABLE_NAME = "role";
    @Id
    @Column(name = "role_id")
    private int RoleID;

    @Column(name = "role_name")
    private String RoleName;
}
