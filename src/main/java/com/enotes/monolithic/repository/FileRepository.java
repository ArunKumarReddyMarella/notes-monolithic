package com.enotes.monolithic.repository;

import com.enotes.monolithic.entity.FileDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileDetails, Integer> {

}
