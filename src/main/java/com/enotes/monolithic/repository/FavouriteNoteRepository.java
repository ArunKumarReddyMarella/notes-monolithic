package com.enotes.monolithic.repository;

import com.enotes.monolithic.entity.FavouriteNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteNoteRepository extends JpaRepository<FavouriteNote, Integer> {

	List<FavouriteNote> findByUserId(int userId);

}
