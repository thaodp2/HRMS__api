package com.minswap.hrms.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = Rank.TABLE_NAME)
public class Rank {
    public static final String TABLE_NAME = "`rank`";

    @Id
    @Column(name = "rank_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankId;

    @Column(name = "rank_name")
    private String rankName;

}
