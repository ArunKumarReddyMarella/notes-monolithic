package com.enotes.monolithic.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotesDto {

	private Integer id;

	private String title;

	private String description;

	private CategoryDto category;

	private Integer createdBy;

	private Date createdOn;

	private Integer updatedBy;

	private Date updatedOn;

	private FilesDto fileDetails;

	private Boolean isDeleted;

	private LocalDateTime deletedOn;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FilesDto {
		private Integer id;
		private String originalFileName;
		private String displayFileName;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CategoryDto {
		private Integer id;
		private String name;
	}

}
