package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;

import java.util.List;
import java.util.stream.Collectors;

public class MentorMapper {
	public static MentorShortDescriptionResponse mentorEntityToShortDesResponse(Mentor mentor, List<Skill> skills) {
		User user = mentor.getUser();
		UserDetail userDetail = user.getUserDetail();
		MentorShortDescriptionResponse mentorShortDescriptionResponse = new MentorShortDescriptionResponse();
		mentorShortDescriptionResponse.setId(user.getId());
		mentorShortDescriptionResponse.setUsername(user.getUsername());
		mentorShortDescriptionResponse.setFullName(userDetail.getFullName());
		mentorShortDescriptionResponse.setGender(userDetail.isGender());
		mentorShortDescriptionResponse.setAvatar(userDetail.getAvatar());
		mentorShortDescriptionResponse.setJob(mentor.getJob());
		mentorShortDescriptionResponse.setListSkill(skills.stream().map(SkillMapper::skillEntityToResponse).collect(Collectors.toList()));
		mentorShortDescriptionResponse.setAnestMentor(mentor.isAnestMentor());
		mentorShortDescriptionResponse.setRating(mentor.getAverageRating());
		mentorShortDescriptionResponse.setPrice(mentor.getPrice());
		mentorShortDescriptionResponse.setTotalRequestFinish(mentor.getTotalRequestFinish());
		return mentorShortDescriptionResponse;
	}

	public static void setRating(Mentor mentor, int rating) {
		if (rating == 1) {
			mentor.setTotalRating1(mentor.getTotalRating1() + 1);
		}
		if (rating == 2) {
			mentor.setTotalRating2(mentor.getTotalRating2() + 1);
		}
		if (rating == 3) {
			mentor.setTotalRating3(mentor.getTotalRating3() + 1);
		}
		if (rating == 4) {
			mentor.setTotalRating4(mentor.getTotalRating4() + 1);
		}
		if (rating == 5) {
			mentor.setTotalRating5(mentor.getTotalRating5() + 1);
		}
	}

	public static float calculateAverageRating(Mentor mentor) {
		float totalRating5 = (float) mentor.getTotalRating5();
		float totalRating4 = (float) mentor.getTotalRating4();
		float totalRating3 = (float) mentor.getTotalRating3();
		float totalRating2 = (float) mentor.getTotalRating2();
		float totalRating1 = (float) mentor.getTotalRating1();

		float averageRating = (
				1 * totalRating1 +
						2 * totalRating2 +
						3 * totalRating3 +
						4 * totalRating4 +
						5 * totalRating5
		) / (totalRating1 + totalRating2 + totalRating3 + totalRating4 + totalRating5);

		return Math.round(averageRating * 2) / 2f;
	}
}
