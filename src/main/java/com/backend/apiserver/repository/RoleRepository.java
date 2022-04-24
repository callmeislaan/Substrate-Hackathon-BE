package com.backend.apiserver.repository;

import com.backend.apiserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	/**
	 * find a Role with given name
	 *
	 * @param name
	 * @return Role
	 */
	Role findByName(String name);
}
