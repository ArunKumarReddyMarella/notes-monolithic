package com.enotes.monolithic.schdular;

import com.enotes.monolithic.entity.Notes;
import com.enotes.monolithic.repository.NotesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotesSchedular {
	private static final Logger logger = LoggerFactory.getLogger(NotesSchedular.class);

	@Autowired
	private NotesRepository notesRepo;

	@Scheduled(cron = "0 0 0 * * ?")
//	@Scheduled(cron = "* * * ? * *")
	public void deleteNotesSchdular() {
		// 20-nov -14 nov -7days
		LocalDateTime cutOffDate = LocalDateTime.now().minusDays(7);
		List<Notes> deleteNotes = notesRepo.findAllByIsDeletedAndDeletedOnBefore(true, cutOffDate);
		notesRepo.deleteAll(deleteNotes);
		logger.info("Deleted {} notes", deleteNotes.size());
	}

}
