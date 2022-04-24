package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MentorSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MentorSkillRepository extends JpaRepository<MentorSkill, Long> {

	/**
	 * Get List Mentor Id By List Skill Id
	 *
	 * @param skillIds
	 * @return List<Long>
	 */
	@Query(value = "select distinct ms.mentor.id from MentorSkill ms where ms.skill.id in ?1")
	List<Long> getListMentorIdByListSkillId(List<Long> skillIds);
}
