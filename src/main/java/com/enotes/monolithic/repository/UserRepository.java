package com.enotes.monolithic.repository;

import com.enotes.monolithic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

	Boolean existsByEmail(String email);

}
