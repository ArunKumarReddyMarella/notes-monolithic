package com.enotes.monolithic.repository;

import com.enotes.monolithic.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
