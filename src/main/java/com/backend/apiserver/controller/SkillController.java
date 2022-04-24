package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.SkillRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.bean.response.SkillFullResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.SkillService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@AllArgsConstructor
public class SkillController {

	private static final Logger LOG = LoggerFactory.getLogger(SkillController.class);

	private SkillService skillService;

	@GetMapping("api/public/skill")
	public WrapperResponse getActiveSkills() {
		LOG.info("Start to find all active skill");
		WrapperResponse wrapperResponse = skillService.getActiveSkills();
		LOG.info("End to find all active skill");
		return wrapperResponse;
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("api/skills")
	public PagingWrapperResponse getAllSkill(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) throws MessagingException, IOException, TemplateException {
		LOG.info("Start to find all skill");
		PagingWrapperResponse wrapperResponse = skillService.getAllSkill(page, size);
		LOG.info("End to find all skill");
		return wrapperResponse;
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("api/skill/{id}")
	public SkillFullResponse getSkill(@PathVariable final Long id) throws NotFoundException {
		LOG.info("Start to find skill with id: ", id);
		SkillFullResponse skill = skillService.getSkill(id);
		LOG.info("End to find skill with id: ", id);
		return skill;
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("api/skill")
	public SkillFullResponse createSkill(@Valid @RequestBody final SkillRequest skillRequest) {
		LOG.info("Start to create skill with information: ", skillRequest);
		SkillFullResponse skill = skillService.createSkill(skillRequest);
		LOG.info("End to create skill with information: ", skillRequest);
		return skill;
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("api/skill/{id}")
	public Response updateSkill(@PathVariable final Long id, @Valid @RequestBody final SkillRequest skillRequest) throws NotFoundException {
		LOG.info("Start to update skill with id: ", id);
		skillService.updateSkill(id, skillRequest);
		LOG.info("End to update skill with id: ", id);
		return new Response(ResponseMessage.UpdateSkillSuccess, id);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("api/skill/{id}")
	public Response undisableSkill(@PathVariable final Long id) throws NotFoundException {
		LOG.info("Start to delete skill with with id: ", id);
		skillService.undisableSkill(id);
		LOG.info("End to delete skill with with id: ", id);
		return new Response(ResponseMessage.DeleteSkillSuccess, id);
	}
}
