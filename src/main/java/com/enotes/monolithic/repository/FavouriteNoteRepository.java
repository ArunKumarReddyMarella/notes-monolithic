package com.enotes.monolithic.repository;

import com.enotes.monolithic.entity.FavouriteNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteNoteRepository extends JpaRepository<FavouriteNote, Integer> {

	List<FavouriteNote> findByUserId(int userId);

    Optional<FavouriteNote> findByUserIdAndNoteId(int userId, Integer noteId);
}
