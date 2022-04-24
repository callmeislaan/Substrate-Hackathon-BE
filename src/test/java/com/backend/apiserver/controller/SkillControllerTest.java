package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.SkillRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.SkillFullResponse;
import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.SkillService;
import com.backend.apiserver.controller.SkillController;
import com.backend.apiserver.utils.FormatUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
public class SkillControllerTest {

    @InjectMocks
    SkillController skillController;

    @InjectMocks
    ApiExceptionHandler apiExceptionHandler;

    private MockMvc mockMvc;

    @Mock
    private SkillService skillService;

    private WrapperResponse wrapperResponse;

    private PagingWrapperResponse pagingWrapperResponse;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(skillController)
                .setControllerAdvice(apiExceptionHandler)
                .build();

        SkillResponse java = new SkillResponse();
        java.setName("JAVA");
        java.setId(0L);
        SkillResponse python = new SkillResponse();
        python.setName("PYTHON");
        python.setId(1L);
        wrapperResponse = new WrapperResponse(Arrays.asList(java, python));
        pagingWrapperResponse = new PagingWrapperResponse(Arrays.asList(java, python), 2);
    }

    @Test
    public void getActiveSkills_Success() throws Exception {
        when(skillService.getActiveSkills()).thenReturn(wrapperResponse);
        mockMvc.perform(get("/api/public/skill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is(0)))
                .andExpect(jsonPath("$.data[0].name", is("JAVA")))
                .andExpect(jsonPath("$.data[1].id", is(1)))
                .andExpect(jsonPath("$.data[1].name", is("PYTHON")));
    }

    @Test
    public void getAllSkill_Success() throws Exception {
        when(skillService.getAllSkill(1, 10)).thenReturn(pagingWrapperResponse);
        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.totalRecords", is(2)))
                .andExpect(jsonPath("$.data[0].id", is(0)))
                .andExpect(jsonPath("$.data[0].name", is("JAVA")))
                .andExpect(jsonPath("$.data[1].id", is(1)))
                .andExpect(jsonPath("$.data[1].name", is("PYTHON")));
    }

    @Test
    public void getSkill_Success() throws Exception {
        SkillFullResponse java = new SkillFullResponse();
        java.setName("JAVA");
        java.setId(0L);
        java.setStatus(Status.ACTIVE.toString());
        when(skillService.getSkill(0L)).thenReturn(java);
        mockMvc.perform(get("/api/skill/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("JAVA")))
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    public void createSkill_Success() throws Exception {
        SkillRequest java = new SkillRequest();
        java.setName("JAVA");
        SkillFullResponse skillFullResponse = new SkillFullResponse();
        skillFullResponse.setStatus(Status.ACTIVE.toString());
        skillFullResponse.setId(5L);
        skillFullResponse.setName("JAVA");
        when(skillService.createSkill(any())).thenReturn(skillFullResponse);
        mockMvc.perform(post("/api/skill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(FormatUtils.convertObjectToJsonBytes(java)))
                .andExpect(jsonPath("$.name", is("JAVA")))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(status().isOk());
    }

    @Test
    public void updateSkill_Success() throws Exception {
        SkillRequest java = new SkillRequest();
        java.setName("JAVA");
        mockMvc.perform(put("/api/skill/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(FormatUtils.convertObjectToJsonBytes(java)))
                .andExpect(jsonPath("$.code", is("128")))
                .andExpect(status().isOk());
    }

    @Test
    public void undisable_Success() throws Exception {
        mockMvc.perform(post("/api/skill/5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}