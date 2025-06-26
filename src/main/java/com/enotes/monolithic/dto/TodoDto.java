package com.enotes.monolithic.dto;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDto {

	private Integer id;

	private String title;

	private StatusDto status;

	private Integer createdBy;

	private Date createdOn;

	private Integer updatedBy;

	private Date updatedOn;

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class StatusDto {
		private Integer id;
		private String name;
	}
}
