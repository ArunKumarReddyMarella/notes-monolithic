package com.enotes.monolithic.dto;

import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TodoDto {

	private Integer id;

	private String title;

	private StatusDto status;

	private Integer createdBy;

	private Date createdOn;

	private Integer updatedBy;

	private Date updatedOn;

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Setter
	public static class StatusDto {
		private Integer id;
		private String name;
	}
}
