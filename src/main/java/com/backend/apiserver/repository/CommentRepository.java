package com.backend.apiserver.repository;

import com.backend.apiserver.entity.Comment;
import com.backend.apiserver.entity.RatingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Get List Rating By List Skill Id For Best Suggestion
     *
     * @param
     * @return List<RatingBean>
     */
    @Query(value = "" +
            "select user_id, mentor_id, cast(avg(rating) as real) as rating from comments c " +
            "group by mentor_id, user_id order by c.user_id", nativeQuery = true)
    List<RatingBean> getListRatingBean();

    /**
     * Get List Rating By List Skill Id For Best Suggestion
     *
     * @param skillIds
     * @return List<RatingBean>
     */
    @Query(value = "" +
            "select user_id, mentor_id, cast(avg(rating) as real) as rating from comments c " +
            "where c.mentor_id in (" +
            "select distinct ms.mentor_id from mentor_skills ms where ms.skill_id in ?1" +
            ") group by mentor_id, user_id order by c.mentor_id", nativeQuery = true)
    List<RatingBean> getListRatingByListSkillId(List<Long> skillIds);

    /**
     * Get List Rating By List Skill Id For Hired Mentor Suggestion
     *
     * @param skillIds
     * @param userId
     * @return List<RatingBean>
     */
    @Query(value = "" +
            "with t1 as (" +
            "select distinct mentor_id from mentor_skills where skill_id in ?1" +
            "), t2 as (" +
            "select mentor_id, user_id, avg(rating) as rating from comments where user_id = ?2 group by mentor_id, user_id " +
            ") " +
            "select t2.*, " +
            "case when t1.mentor_id is null then 1 else 0 end as checknull " +
            "from t1 right outer join t2 on t1.mentor_id = t2.mentor_id " +
            "order by checknull, rating desc", nativeQuery = true)
    List<RatingBean> getListRatingByListSkillIdAndUserId(List<Long> skillIds, Long userId);

    Page<Comment> findAllByMentorId(Long mentorId, Pageable pageable);
}
