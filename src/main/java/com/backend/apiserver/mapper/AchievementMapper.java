package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.response.AchievementResponse;
import com.backend.apiserver.entity.Achievement;

public class AchievementMapper {

	public static AchievementResponse convertToResponse(Achievement achievement) {
		AchievementResponse achievementResponse = new AchievementResponse();
		achievementResponse.setTitle(achievement.getTitle());
		achievementResponse.setContent(achievement.getContent());
		return achievementResponse;
	}
}
