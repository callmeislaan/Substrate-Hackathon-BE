package com.backend.apiserver.repository;

import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

	/**
	 * Find all skills
	 *
	 * @param status
	 * @return list of skills
	 */
	@Query(value = "select * from skills s where s.status = :status order by random()", nativeQuery = true)
	List<Skill> findAllByStatus(String status);

	/**
	 * Find skill by id
	 *
	 * @param id
	 * @return Skill
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Skill findSkillById(Long id);

	Skill findByIdAndStatus(long id, Status status);

	/**
	 * Find list skill by list ids
	 *
	 * @param ids    list of id
	 * @param status only active status
	 * @return List<Skill>
	 */
	List<Skill> findAllByIdInAndStatus(List<Long> ids, Status status);

	/**
	 * Check if list of ids is valid
	 *
	 * @param ids
	 * @param status
	 * @return
	 */
	boolean existsAllByIdInAndStatus(List<Long> ids, Status status);

	boolean existsByIdAndStatus(Long id, Status status);

	@Query(value = "select s.* from skills s join mentor_skills ms on s.id = ms.skill_id where ms.mentor_id = ?1 and s.status = ?2", nativeQuery = true)
	List<Skill> findAllByMentorIdAndStatus(Long mentorId, String status);
}
