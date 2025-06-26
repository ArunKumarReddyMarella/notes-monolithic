package com.enotes.monolithic.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotesResponse {

	private List<NotesDto> notes;

	private Integer pageNo;
	private Integer pageSize;
	private Long totalElements;
	private Integer totalPages;
	private Boolean isFirst;
	private Boolean isLast;

}
