package com.minswap.hrms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Person.TABLE_NAME)
public class Person {

	public static final String TABLE_NAME = "person";

	public Person(Long personId, String fullName) {
		this.personId = personId;
		this.fullName = fullName;
	}

	@Id
	@Column(name = "person_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long personId;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "address")
	private String address;

	@Column(name = "citizen_identification")
	private String citizenIdentification;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "on_board_date")
	private Date onBoardDate;

	@Column(name = "rank_id")
	private Long rankId;

	@Column(name = "department_id")
	private Long departmentId;

	@Column(name = "manager_id")
	private Long managerId;

	@Column(name = "gender")
	private int gender;

	@Column(name = "roll_number")
	private String rollNumber;

	@Column(name = "date_of_birth")
	private Date dateOfBirth;

	@Column(name = "status")
	private String status;

	@Column(name = "position_id")
	private Long positionId;

	@Column(name = "salary_basic")
	private Double salaryBasic;

	@Column(name = "salary_bonus")
	private Double salaryBonus;

	@Column(name = "annual_leave_budget")
	private Double annualLeaveBudget;
}
