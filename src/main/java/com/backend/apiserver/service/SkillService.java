package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.SkillRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.SkillFullResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.NotFoundException;

public interface SkillService {

	/**
	 * Get all active skill
	 *
	 * @return ResponseWrapper
	 */
	WrapperResponse getActiveSkills();

	/**
	 * Get all skill
	 *
	 * @return ResponseWrapper
	 */
	PagingWrapperResponse getAllSkill(Integer page, Integer size);

	/**
	 * Create skill
	 *
	 * @param skillRequest
	 * @return Skill
	 */
	SkillFullResponse createSkill(SkillRequest skillRequest);

	/**
	 * Delete skill with given id
	 *
	 * @param id
	 * @throws NotFoundException
	 */
	void undisableSkill(Long id) throws NotFoundException;

	/**
	 * Update card information
	 *
	 * @param id
	 * @param skillRequest
	 * @throws NotFoundException
	 */
	void updateSkill(Long id, SkillRequest skillRequest) throws NotFoundException;

	/**
	 * Find skill by given id
	 *
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	SkillFullResponse getSkill(Long id) throws NotFoundException;
}
