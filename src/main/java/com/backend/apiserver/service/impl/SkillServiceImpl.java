package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.SkillRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.SkillFullResponse;
import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.service.SkillService;
import com.backend.apiserver.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {

	/**
	 * SkillRepository
	 */
	private SkillRepository skillRepository;

	@Override
	public WrapperResponse getActiveSkills() {
		List<Skill> skills = skillRepository.findAllByStatus(Status.ACTIVE.toString());
		return new WrapperResponse(
				skills
						.stream()
						.map(skill -> entityToSkillResponse(skill))
						.collect(Collectors.toList())
		);
	}

	@Override
	public PagingWrapperResponse getAllSkill(Integer page, Integer size) {
		Page<Skill> skills = skillRepository.findAll(PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
		return new PagingWrapperResponse(
				skills
						.stream()
						.map(skill -> entityToSkillFullResponse(skill))
						.collect(Collectors.toList()),
				skills.getTotalElements()
		);
	}

	@Override
	@AnestTransactional
	public SkillFullResponse createSkill(SkillRequest skillRequest) {
		Skill skill = skillRepository.saveAndFlush(requestToEntity(skillRequest));
		return entityToSkillFullResponse(skill);
	}

	@Override
	@AnestTransactional
	public void undisableSkill(Long id) throws NotFoundException {
		Skill skill = skillRepository.findSkillById(id);
		if (Objects.isNull(skill)) throw new NotFoundException("Skill with this id is not exist: " + id);
		skill.setStatus(skill.getStatus().equals(Status.ACTIVE) ? Status.DELETE : Status.ACTIVE);
		skillRepository.saveAndFlush(skill);
	}

	@Override
	@AnestTransactional
	public void updateSkill(Long id, SkillRequest skillRequest) throws NotFoundException {
		Skill skill = skillRepository.findSkillById(id);
		if (Objects.isNull(skill)) {
			throw new NotFoundException("Skill with this id is not exist: " + id);
		}
		if (!skillRequest.getName().equals(skill.getName())) {
			skill.setName(StringUtils.upperCase(skillRequest.getName()));
		}
		skill.setStatus(skillRequest.isStatus() ? Status.ACTIVE : Status.DELETE);
		skillRepository.saveAndFlush(skill);
	}

	@Override
	public SkillFullResponse getSkill(Long id) throws NotFoundException {
		Optional<Skill> skill = skillRepository.findById(id);
		if (!skill.isPresent()) {
			throw new NotFoundException("Skill with this id is not exist: " + id);
		}
		return entityToSkillFullResponse(skill.get());
	}

	private Skill requestToEntity(SkillRequest skillRequest) {
		Skill skill = new Skill();
		skill.setName(StringUtils.upperCase(skillRequest.getName()));
		skill.setStatus(Status.ACTIVE);
		return skill;
	}

	private SkillResponse entityToSkillResponse(Skill skill) {
		SkillResponse skillResponse = new SkillResponse();
		skillResponse.setId(skill.getId());
		skillResponse.setName(skill.getName());
		return skillResponse;
	}

	private SkillFullResponse entityToSkillFullResponse(Skill skill) {
		SkillFullResponse skillFullResponse = new SkillFullResponse();
		skillFullResponse.setId(skill.getId());
		skillFullResponse.setName(skill.getName());
		skillFullResponse.setStatus(skill.getStatus().toString());
		return skillFullResponse;
	}
}
