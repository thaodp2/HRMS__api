package com.minswap.hrms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Person.TABLE_NAME)
public class Person {

	public static final String TABLE_NAME = "person";

	@Id
	@Column(name = "person_id")
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
	private String onBoardDate;

	@Column(name = "rank_id")
	private String rankId;

	@Column(name = "department_id")
	private String departmentId;

	@Column(name = "manager_id")
	private Long managerId;

	@Column(name = "gender")
	private String gender;

	@Column(name = "roll_number")
	private String rollNumber;

	@Column(name = "date_of_birth")
	private String dateOfBirth;

	@Column(name = "status")
	private String status;

}
