package com.minswap.hrms.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = Person_Role.TABLE_NAME)
public class Person_Role {
    public static final String TABLE_NAME = "person_role";

    @EmbeddedId
    private Pr pr;

    @Embeddable
    public static class Pr implements Serializable {
        private static final long serialVersionUID = 1L;
        @Column(name = "role_id")
        private Long roleId;

        @Column(name = "person_id")
        private Long personId;

    }

//    @Column(name = "role_id")
//    private Long roleId;
//
//    @Column(name = "person_id")
//    private Long personId;

}
