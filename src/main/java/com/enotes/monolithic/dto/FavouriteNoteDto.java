package com.enotes.monolithic.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteNoteDto {
	
	private Integer id;

	private NotesDto note;

	private Integer userId;
}
