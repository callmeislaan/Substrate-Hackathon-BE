package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.entity.Skill;

public class SkillMapper {
    public static SkillResponse skillEntityToResponse(Skill skill) {
        SkillResponse skillResponse = new SkillResponse();
        skillResponse.setId(skill.getId());
        skillResponse.setName(skill.getName());
        return skillResponse;
    }
}
