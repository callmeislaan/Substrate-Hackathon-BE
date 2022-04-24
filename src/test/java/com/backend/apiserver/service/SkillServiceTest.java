package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.SkillRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.service.impl.SkillServiceImpl;
import com.backend.apiserver.utils.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SkillServiceTest {

    @Mock
    SkillRepository skillRepository;

    @InjectMocks
    SkillService skillService = new SkillServiceImpl(skillRepository);

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getActiveSkills() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setStatus(Status.ACTIVE);
        skill.setName("JAVASCRIPT");
        List<Skill> skills = Arrays.asList(skill);
        when(skillRepository.findAllByStatus(Status.ACTIVE.toString())).thenReturn(skills);
        WrapperResponse wrapperResponse = skillService.getActiveSkills();
        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getAllSkill() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setStatus(Status.ACTIVE);
        skill.setName("JAVASCRIPT");
        Page<Skill> skills = new PageImpl(Arrays.asList(skill));
        when(skillRepository.findAll(PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(skills);
        PagingWrapperResponse wrapperResponse = skillService.getAllSkill(1, 10);
        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void createSkill() {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setName("JAVA");
        Skill skill = new Skill();
        skill.setName(skillRequest.getName());
        skill.setId(3L);
        skill.setStatus(Status.ACTIVE);
        when(skillRepository.saveAndFlush(any())).thenReturn(skill);
        skillService.createSkill(skillRequest);
        verify(skillRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void undisableSkill() throws NotFoundException {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setName("JAVA");
        Skill skill = new Skill();
        skill.setName(skillRequest.getName());
        skill.setId(3L);
        skill.setStatus(Status.ACTIVE);
        when(skillRepository.findSkillById(any())).thenReturn(skill);
        skillService.undisableSkill(any());
        verify(skillRepository, times(1)).saveAndFlush(any());
    }

    @Test(expected = NotFoundException.class)
    public void undisableSkill_NotFoundException() throws NotFoundException {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setName("JAVA");
        when(skillRepository.findSkillById(any())).thenReturn(null);
        skillService.undisableSkill(any());
    }

    @Test
    public void updateSkill() throws NotFoundException {
        Long id = 5L;
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setName("JAVA");
        Skill skill = new Skill();
        skill.setName(skillRequest.getName());
        skill.setId(3L);
        skill.setStatus(Status.ACTIVE);
        when(skillRepository.findSkillById(any())).thenReturn(skill);
        skillService.updateSkill(id, skillRequest);
        verify(skillRepository, times(1)).saveAndFlush(any());
    }

    @Test(expected = NotFoundException.class)
    public void updateSkillThrowNotFoundException() throws NotFoundException {
        Long id = 5L;
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setName("JAVA");
        Skill skill = new Skill();
        skill.setName(skillRequest.getName());
        skill.setId(3L);
        skill.setStatus(Status.ACTIVE);
        when(skillRepository.findSkillById(id)).thenReturn(null);
        skillService.updateSkill(id, skillRequest);
    }

    @Test
    public void getSkill() throws NotFoundException {
        Skill skill = new Skill();
        skill.setName("JAVA");
        skill.setId(3L);
        skill.setStatus(Status.ACTIVE);
        Optional<Skill> skillOptional = Optional.of(skill);
        when(skillRepository.findById(any())).thenReturn(skillOptional);
        skillService.getSkill(any());
    }

    @Test(expected = NotFoundException.class)
    public void getSkillThrowNotFoundException() throws NotFoundException {
        Skill skill = new Skill();
        skill.setName("JAVA");
        skill.setId(3L);
        skill.setStatus(Status.ACTIVE);
        when(skillRepository.findById(any())).thenReturn(Optional.ofNullable(null));
        skillService.getSkill(any());
    }
}