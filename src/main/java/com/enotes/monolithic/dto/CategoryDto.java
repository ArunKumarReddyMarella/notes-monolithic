package com.enotes.monolithic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

	private Integer id;

	private String name;

	private String description;

	private Boolean isActive;

	private Integer createdBy;

	private Date createdOn;

	private Integer updatedBy;

	private Date updatedOn;
}
