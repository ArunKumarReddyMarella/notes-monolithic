package com.enotes.monolithic.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

	private Integer id;

	private String firstName;

	private String lastName;

	private String email;

	private String mobNo;

	private String password;

	private List<RoleDto> roles;

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Setter
	@Builder
	public static class RoleDto {
		private Integer id;
		private String name;
	}
}
