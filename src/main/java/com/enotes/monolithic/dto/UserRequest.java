package com.enotes.monolithic.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

	private Integer id;

	private String firstName;

	private String lastName;

	private String email;

	private String mobNo;

	private String password;

	private List<RoleDto> roles;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class RoleDto {
		private Integer id;
		private String name;
	}
}
