package com.enotes.monolithic.repository;

import com.enotes.monolithic.entity.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotesRepository extends JpaRepository<Notes, Integer> {

    Page<Notes> findByCreatedBy(Integer userId, Pageable pageable);

    List<Notes> findByCreatedByAndIsDeletedTrue(Integer userId);

    Page<Notes> findByCreatedByAndIsDeletedFalse(Integer userId, Pageable pageable);

    List<Notes> findAllByIsDeletedAndDeletedOnBefore(boolean b, LocalDateTime cutOffDate);

    @Query("SELECT n FROM Notes n " +
            "WHERE (LOWER(n.title) LIKE LOWER(concat('%', :keyword, '%')) " +
            "OR LOWER(n.description) LIKE LOWER(concat('%', :keyword, '%')) " +
            "OR LOWER(n.category.name) LIKE LOWER(concat('%', :keyword, '%'))) " +
            "AND n.createdBy = :userId " +
            "AND n.isDeleted = false")
    Page<Notes> searchNotes(@Param("keyword") String keyword, @Param("userId") Integer userId, PageRequest pageRequest);

//    @Query("select n from Notes n where (Lower(n.title) like lower(concat('%',:keyword,'%')) "
//            + "or lower(n.description) like lower(concat('%',:keyword,'%')) "
//            + "or lower(n.category.name) like lower(concat('%',:keyword,'%'))) "
//            + "and n.isDeleted=false "
//            + "and n.createdBy=:userId")
//    Page<Notes> searchNotes(@Param("keyword") String keyword,@Param("userId")Integer userId,Pageable pageable);
}
