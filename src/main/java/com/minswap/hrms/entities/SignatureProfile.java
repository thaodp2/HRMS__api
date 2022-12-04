package com.minswap.hrms.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = SignatureProfile.TABLE_NAME)
public class SignatureProfile {

	public static final String TABLE_NAME = "signature_profile";
	@Id
	@Column(name = "signature_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long signatureId;

	@Column(name = "private_key_signature")
	private String privateKeySignature;

	@Column(name = "person_id")
	private Long personId;

	@Column(name = "registered_date")
	private Date registeredDate;

}
