package com.enotes.monolithic.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

	private Integer id;

	private String firstName;

	private String lastName;

	private String email;

	private String mobNo;

	private StatusDto accountStatus;

	private List<RoleDto> roles;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class RoleDto {
		private Integer id;
		private String name;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class StatusDto {
		private Integer id;
		private Boolean isActive;
	}
}
