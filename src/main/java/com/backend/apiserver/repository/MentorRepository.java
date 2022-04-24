package com.backend.apiserver.repository;

import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Set;

public interface MentorRepository extends JpaRepository<Mentor, Long>, PagingAndSortingRepository<Mentor, Long>, JpaSpecificationExecutor<Mentor> {
	/**
	 * Find mentor information by user id
	 *
	 * @param userId user id
	 * @param status active status
	 * @return Mentor
	 */
	Mentor findByUserIdAndStatus(Long userId, Status status);

	/**
	 * Find mentor for update using pessimistic locking
	 *
	 * @param userId user id
	 * @param status active status
	 * @return Mentor
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Mentor findMentorByUserIdAndStatus(Long userId, Status status);

	/**
	 * Check mentor exist
	 *
	 * @param userId user id
	 * @param status status
	 * @return boolean
	 */
	boolean existsByUserIdAndStatus(Long userId, Status status);

	/**
	 * Get List Random Mentor
	 *
	 * @param isAnestmentor
	 * @return List<Mentor>
	 */
	@Query(value = "select * from mentors where anest_mentor = ?1 and status = ?2 order by random() limit 50", nativeQuery = true)
	List<Mentor> getListRandomMentor(boolean isAnestmentor, String status);

	/**
	 * Find list mentor by list mentor id
	 *
	 * @param mentorIds
	 * @param status    only active status
	 * @return List<Mentor>
	 */
	List<Mentor> findAllByUserIdInAndStatus(List<Long> mentorIds, Status status);

	/**
	 * Get List Anest Mentor Suggestion By List Skill Id
	 *
	 * @param skillIds
	 * @return List<Mentor>
	 */
	@Query(value = "" +
			"with t1 as (" +
			"select mentor_id, sum(value) as sum " +
			"from mentor_skills " +
			"where skill_id in ?1 " +
			"group by mentor_id" +
			") " +
			"select coalesce(t1.sum, 0) as sum, t2.* " +
			"from t1 right outer join mentors t2 " +
			"on t1.mentor_id = t2.user_id " +
			"where t2.status = 'ACTIVE' and t2.anest_mentor = true " +
			"order by t2.average_rating desc, t2.total_request_finish desc, sum desc", nativeQuery = true)
	List<Mentor> getListAnestMentorSuggestionByListSkillId(List<Long> skillIds);

	/**
	 * Get List Mentor Suggestion By List Skill Id
	 *
	 * @param skillIds
	 * @return List<Mentor>
	 */
	@Query(value = "" +
			"with t1 as (" +
			"select mentor_id, sum(value) as sum " +
			"from mentor_skills " +
			"where skill_id in ?1 " +
			"group by mentor_id" +
			") " +
			"select coalesce(t1.sum, 0) as sum, t2.* " +
			"from t1 right outer join mentors t2 " +
			"on t1.mentor_id = t2.user_id " +
			"where t2.status = 'ACTIVE' " +
			"order by t2.average_rating desc, t2.total_request_finish desc, sum desc", nativeQuery = true)
	Page<Mentor> getTopRatingMentorByListSkillId(List<Long> skillIds, Pageable pageable);

	/**
	 * Get List Following Mentor Suggestion By List Skill Id and List Mentor Ids
	 *
	 * @param skillIds
	 * @param mentorIds
	 * @return List<Mentor>
	 */
	@Query(value = "" +
			"with t1 as (" +
			"select mentor_id, sum(value) as sum from mentor_skills " +
			"where skill_id in :skillids and mentor_id in :mentorids " +
			"group by mentor_id" +
			") " +
			"select coalesce(t1.sum, 0) as sum, t2.* " +
			"from t1 right outer join mentors t2 " +
			"on t1.mentor_id = t2.user_id " +
			"where t2.status = 'ACTIVE' and t2.user_id in :mentorids " +
			"order by sum desc, t2.average_rating desc", nativeQuery = true)
	List<Mentor> getListFollowingMentorSuggestionByListSkillIdAndListMentorId(@Param("skillids") List<Long> skillIds, @Param("mentorids") Set<Long> mentorIds);

	Page<Mentor> findAllByIdInAndStatus(Iterable<Long> ids, Status status, Pageable pageable);

	@Query(value = "select mt.* from mentors mt inner join users u on mt.user_id = u.id inner join user_details ud on mt.user_id = ud.user_id " +
			"where u.email like %:keyword% or u.username like %:keyword% or ud.full_name like %:keyword%", nativeQuery = true)
	Page<Mentor> findAllByEmailOrUsernameOrFullName(@Param("keyword") String keyword, Pageable pageable);

	Integer countMentorByStatus(Status status);

	@Query(value = "select * from mentors order by total_request_finish desc limit 5", nativeQuery = true)
	List<Mentor> findTop5ExcellentMentor();

	@Query(value = "select m.* " +
			"from mentors m inner join mentor_requests mt on m.user_id = mt.mentor_id inner join requests r on mt.request_id = r.id " +
			"where r.user_id = ?1 and mt.status = ?2", nativeQuery = true)
	Page<Mentor> findAllRentMentorsByUserIdAndStatus(Long userId, String status, Pageable pageable);

	@Query(value = "select * from mentors where status = 'ACTIVE' order by average_rating desc, total_request_finish desc", nativeQuery = true)
	List<Mentor> findAllActiveMentor(Pageable pageable);
}
