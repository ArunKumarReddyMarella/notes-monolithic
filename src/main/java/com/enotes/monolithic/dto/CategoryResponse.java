package com.enotes.monolithic.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

	private Integer id;

	private String name;

	private String description;

}
