package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.AchievementRequest;
import com.backend.apiserver.bean.request.FilterMentorRequest;
import com.backend.apiserver.bean.request.FilterMentorWrapperRequest;
import com.backend.apiserver.bean.request.MentorSkillRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.response.AchievementResponse;
import com.backend.apiserver.bean.response.CommentResponse;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.IncomeReportResponse;
import com.backend.apiserver.bean.response.MentorOverviewResponse;
import com.backend.apiserver.bean.response.MentorResumeResponse;
import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.MentorService;
import com.backend.apiserver.controller.MentorController;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class MentorControllerTest {

	@InjectMocks
	MentorController mentorController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private MentorService mentorService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(mentorController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void getAllAnestMentor() throws Exception {
		when(mentorService.getAllHomeMentor(true)).thenReturn((WrapperResponse) generateMentors(true, false));
		mockMvc.perform(get("/api/public/home/anest-mentor"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)))
				.andExpect(jsonPath("$.data[0].anestMentor", is(true)))
				.andExpect(jsonPath("$.data[0].avatar", is("")))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].gender", is(true)))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].id", is(125)))
				.andExpect(jsonPath("$.data[0].job", is("Java Developer")))
				.andExpect(jsonPath("$.data[0].price", is(250000)))
				.andExpect(jsonPath("$.data[0].rating", is(4.5)))
				.andExpect(jsonPath("$.data[0].username", is("namlm3")))
				.andExpect(jsonPath("$.data[0].totalRequestFinish", is(55)))
				.andExpect(jsonPath("$.data[0].listSkill[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].listSkill[0].name", is("JAVA")))
				.andExpect(jsonPath("$.data[0].listSkill[1].id", is(6)))
				.andExpect(jsonPath("$.data[0].listSkill[1].name", is("C#")));
	}

	@Test
	public void getAllOtherMentor() throws Exception {
		when(mentorService.getAllHomeMentor(false)).thenReturn((WrapperResponse) generateMentors(false, false));
		mockMvc.perform(get("/api/public/home/other-mentor"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)))
				.andExpect(jsonPath("$.data[0].anestMentor", is(false)))
				.andExpect(jsonPath("$.data[0].avatar", is("")))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].gender", is(true)))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].id", is(125)))
				.andExpect(jsonPath("$.data[0].job", is("Java Developer")))
				.andExpect(jsonPath("$.data[0].price", is(250000)))
				.andExpect(jsonPath("$.data[0].rating", is(4.5)))
				.andExpect(jsonPath("$.data[0].username", is("namlm3")))
				.andExpect(jsonPath("$.data[0].totalRequestFinish", is(55)))
				.andExpect(jsonPath("$.data[0].listSkill[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].listSkill[0].name", is("JAVA")))
				.andExpect(jsonPath("$.data[0].listSkill[1].id", is(6)))
				.andExpect(jsonPath("$.data[0].listSkill[1].name", is("C#")));
	}

	@Test
	public void unregisterMentorMember() throws Exception {
		mockMvc.perform(put("/api/mentor/unregister-mentor"))
				.andExpect(jsonPath("$.code", is("114")))
				.andExpect(status().isOk());
	}

	@Test
	public void getMentorOverview() throws Exception {
		when(mentorService.getMentorOverview()).thenReturn(generateMentorOverview());
		mockMvc.perform(get("/api/mentor/overview"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.averageRating", is(4.5)))
				.andExpect(jsonPath("$.totalHoursBeHired", is(10)))
				.andExpect(jsonPath("$.totalRequestDeny", is(120)))
				.andExpect(jsonPath("$.totalRequestReceive", is(150)));
	}

	@Test
	public void getIncomeReport() throws Exception {
		when(mentorService.getIncomeReport()).thenReturn(generateIncomeReport());
		mockMvc.perform(get("/api/mentor/income-report"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dailyIncome", is(500000)))
				.andExpect(jsonPath("$.monthlyIncome", is(1200000)))
				.andExpect(jsonPath("$.weeklyIncome", is(800000)));
	}

	@Test
	public void getMentorResume() throws Exception {
		when(mentorService.getMentorResume(250L)).thenReturn(generateMentorResume());
		mockMvc.perform(get("/api/public/mentor/resume/250"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalRequestFinish", is(12)))
				.andExpect(jsonPath("$.totalRating5", is(15)))
				.andExpect(jsonPath("$.totalRating4", is(2)))
				.andExpect(jsonPath("$.totalRating3", is(1)))
				.andExpect(jsonPath("$.totalRating2", is(1)))
				.andExpect(jsonPath("$.totalRating1", is(1)))
				.andExpect(jsonPath("$.avatar", is("")))
				.andExpect(jsonPath("$.averageRating", is(4.5)))
				.andExpect(jsonPath("$.following", is(true)))
				.andExpect(jsonPath("$.totalRequestFinish", is(12)));
	}

	@Test
	public void getMentorComments() throws Exception {
		when(mentorService.getMentorComments(250L, 1, 5)).thenReturn(generateComments());
		mockMvc.perform(get("/api/public/mentor/comments/250?page=1&size=5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].avatar", is("")))
				.andExpect(jsonPath("$.data[0].content", is("Mentor nhiệt tình, đúng giờ")))
				.andExpect(jsonPath("$.data[0].createdDate", is(32546465545L)))
				.andExpect(jsonPath("$.data[0].fullName", is("Nguyễn Văn Học")))
				.andExpect(jsonPath("$.data[0].rating", is(5)))
				.andExpect(jsonPath("$.data[0].id", is(15)))
				.andExpect(jsonPath("$.data[0].gender", is(true)))
				.andExpect(jsonPath("$.totalRecords", is(1)));
	}

	@Test
	public void getMentorAchievements() throws Exception {
		when(mentorService.getMentorAchievements(125L)).thenReturn(generateAchievements());
		mockMvc.perform(get("/api/public/mentor/achievements/125"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].title", is("Gold Medal")))
				.andExpect(jsonPath("$.data[0].content", is("FU BADMINTON CHAMPIONSHIP")))
				.andExpect(jsonPath("$.data[1].title", is("Gold Medal")))
				.andExpect(jsonPath("$.data[1].content", is("CODE IGNITE")));
	}

	@Test
	public void followOrUnfollowMentor() throws Exception {
		FollowingResponse followingResponse = new FollowingResponse();
		followingResponse.setFollowingStatus("FOLLOWING");
		when(mentorService.followOrUnfollowMentor(any())).thenReturn(followingResponse);
		mockMvc.perform(post("/api/mentor/following-or-unfollowing/125"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.followingStatus", is("FOLLOWING")));
	}

	@Test
	public void unfollowAllMentor() throws Exception {
		mockMvc.perform(post("/api/mentor/unfollowing-all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("127")));
	}

	@Test
	public void getFollowingMentors() throws Exception {
		when(mentorService.getFollowingMentors(1, 10)).thenReturn((PagingWrapperResponse) generateMentors(true, true));
		mockMvc.perform(get("/api/mentor/following-mentors"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)))
				.andExpect(jsonPath("$.data[0].anestMentor", is(true)))
				.andExpect(jsonPath("$.data[0].avatar", is("")))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].gender", is(true)))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].id", is(125)))
				.andExpect(jsonPath("$.data[0].job", is("Java Developer")))
				.andExpect(jsonPath("$.data[0].price", is(250000)))
				.andExpect(jsonPath("$.data[0].rating", is(4.5)))
				.andExpect(jsonPath("$.data[0].username", is("namlm3")))
				.andExpect(jsonPath("$.data[0].totalRequestFinish", is(55)))
				.andExpect(jsonPath("$.data[0].listSkill[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].listSkill[0].name", is("JAVA")))
				.andExpect(jsonPath("$.data[0].listSkill[1].id", is(6)))
				.andExpect(jsonPath("$.data[0].listSkill[1].name", is("C#")));
	}

	@Test
	public void filterMentor() throws Exception {
		when(mentorService.filterMentor(any())).thenReturn((PagingWrapperResponse) generateMentors(true, true));
		mockMvc.perform(post("/api/public/mentors/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFilterMentor())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)))
				.andExpect(jsonPath("$.data[0].anestMentor", is(true)))
				.andExpect(jsonPath("$.data[0].avatar", is("")))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].gender", is(true)))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].id", is(125)))
				.andExpect(jsonPath("$.data[0].job", is("Java Developer")))
				.andExpect(jsonPath("$.data[0].price", is(250000)))
				.andExpect(jsonPath("$.data[0].rating", is(4.5)))
				.andExpect(jsonPath("$.data[0].username", is("namlm3")))
				.andExpect(jsonPath("$.data[0].totalRequestFinish", is(55)))
				.andExpect(jsonPath("$.data[0].listSkill[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].listSkill[0].name", is("JAVA")))
				.andExpect(jsonPath("$.data[0].listSkill[1].id", is(6)))
				.andExpect(jsonPath("$.data[0].listSkill[1].name", is("C#")));
	}

	@Test
	public void updateMentorInfo() throws Exception {
		mockMvc.perform(put("/api/mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRegisterMentor())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("150")));
	}

	@Test
	public void getMentorResume_ErrorThrowNotFoundException() throws Exception {
		when(mentorService.getMentorResume(125L)).thenThrow(NotFoundException.class);
		mockMvc.perform(get("/api/public/mentor/resume/125")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateMentorResume())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getMentorComments_ErrorThrowNotFoundException() throws Exception {
		when(mentorService.getMentorComments(125L, 1, 10)).thenThrow(NotFoundException.class);
		mockMvc.perform(get("/api/public/mentor/comments/125")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateMentorResume())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getMentorAchievements_ErrorThrowNotFoundException() throws Exception {
		when(mentorService.getMentorAchievements(125L)).thenThrow(NotFoundException.class);
		mockMvc.perform(get("/api/public/mentor/achievements/125")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateMentorResume())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateMentorInfo_ErrorThrowUserNotFoundException() throws Exception {
		doThrow(new UserNotFoundException()).when(mentorService).updateMentorInfo(generateRegisterMentor());
		mockMvc.perform(put("/api/mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRegisterMentor())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateMentorInfo_ErrorThrowSkillNotFoundException() throws Exception {
		doThrow(new SkillNotFoundException()).when(mentorService).updateMentorInfo(generateRegisterMentor());
		mockMvc.perform(put("/api/mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRegisterMentor())))
				.andExpect(status().isBadRequest());
	}

	private RegisterMentorRequest generateRegisterMentor() {
		AchievementRequest achievementRequest = new AchievementRequest();
		achievementRequest.setTitle("MATH");
		achievementRequest.setContent("MATH OLYMPIC");

		MentorSkillRequest mentorSkillRequest = new MentorSkillRequest();
		mentorSkillRequest.setId(1);
		mentorSkillRequest.setValue(8);

		RegisterMentorRequest registerRequest = RegisterMentorRequest
				.builder()
				.achievements(Arrays.asList(achievementRequest))
				.introduction("Instantiating CacheAwareContextLoaderDelegate from class")
				.job("JAVA DEVELOPER")
				.price(15000)
				.mentorSkills(Arrays.asList(mentorSkillRequest))
				.service("Delegating to GenericXmlContextLoader to process context configuration")
				.skillDescription("Neither @ContextConfiguration nor @ContextHierarchy found")
				.build();

		return registerRequest;
	}

	private FilterMentorWrapperRequest generateFilterMentor() {
		FilterMentorWrapperRequest filter = new FilterMentorWrapperRequest();
		filter.setPage(1);
		filter.setSize(1);
		filter.setKeyWord("Anh");
		filter.setOrder("ascending");
		filter.setSort("name");
		FilterMentorRequest filterMentorRequest = new FilterMentorRequest();
		filterMentorRequest.setAnestMentor(true);
		filterMentorRequest.setMaxPrice(150000);
		filterMentorRequest.setMinPrice(100000);
		filterMentorRequest.setSkillIds(Arrays.asList(1L, 2L, 3L));
		filter.setFilter(filterMentorRequest);
		return filter;
	}

	private WrapperResponse generateAchievements() {
		AchievementResponse badminton = new AchievementResponse();
		badminton.setTitle("Gold Medal");
		badminton.setContent("FU BADMINTON CHAMPIONSHIP");
		AchievementResponse coding = new AchievementResponse();
		coding.setTitle("Gold Medal");
		coding.setContent("CODE IGNITE");
		return new WrapperResponse(Arrays.asList(badminton, coding));
	}

	private MentorResumeResponse generateMentorResume() {
		MentorResumeResponse mentorResumeResponse = new MentorResumeResponse();
		mentorResumeResponse.setTotalRequestFinish(12);
		mentorResumeResponse.setTotalRating5(15);
		mentorResumeResponse.setTotalRating4(2);
		mentorResumeResponse.setTotalRating3(1);
		mentorResumeResponse.setTotalRating2(1);
		mentorResumeResponse.setTotalRating1(1);
		mentorResumeResponse.setAvatar("");
		mentorResumeResponse.setAverageRating(4.5F);
		mentorResumeResponse.setCreatedDate(654684968416416L);
		mentorResumeResponse.setFollowing(true);
		mentorResumeResponse.setTotalRequestFinish(12);
		return mentorResumeResponse;
	}

	private MentorOverviewResponse generateMentorOverview() {
		MentorOverviewResponse mentorOverviewResponse = new MentorOverviewResponse();
		mentorOverviewResponse.setAverageRating(4.5F);
		mentorOverviewResponse.setTotalHoursBeHired(10);
		mentorOverviewResponse.setTotalRequestDeny(120);
		mentorOverviewResponse.setTotalRequestReceive(150);
		return mentorOverviewResponse;
	}

	private Object generateMentors(boolean isAnestMentor, boolean isPagination) {
		MentorShortDescriptionResponse mentorShortDescriptionResponse;
		mentorShortDescriptionResponse = new MentorShortDescriptionResponse();
		mentorShortDescriptionResponse.setAnestMentor(isAnestMentor);
		mentorShortDescriptionResponse.setAvatar("");
		mentorShortDescriptionResponse.setFullName("NGUYỄN VĂN NAM");
		mentorShortDescriptionResponse.setGender(true);
		mentorShortDescriptionResponse.setId(125L);
		mentorShortDescriptionResponse.setJob("Java Developer");
		mentorShortDescriptionResponse.setPrice(250000);
		mentorShortDescriptionResponse.setRating(4.5F);
		mentorShortDescriptionResponse.setUsername("namlm3");
		mentorShortDescriptionResponse.setTotalRequestFinish(55);
		SkillResponse java = new SkillResponse();
		java.setId(5L);
		java.setName("JAVA");
		SkillResponse cSharp = new SkillResponse();
		cSharp.setId(6L);
		cSharp.setName("C#");
		mentorShortDescriptionResponse.setListSkill(Arrays.asList(java, cSharp));
		if (isPagination) return new PagingWrapperResponse(Arrays.asList(mentorShortDescriptionResponse), 1);
		else return new WrapperResponse(Arrays.asList(mentorShortDescriptionResponse));
	}

	private IncomeReportResponse generateIncomeReport() {
		IncomeReportResponse incomeReportResponse = new IncomeReportResponse();
		incomeReportResponse.setDailyIncome(500000);
		incomeReportResponse.setMonthlyIncome(1200000);
		incomeReportResponse.setWeeklyIncome(800000);
		return incomeReportResponse;
	}

	private PagingWrapperResponse generateComments() {
		CommentResponse commentResponse = new CommentResponse();
		commentResponse.setAvatar("");
		commentResponse.setContent("Mentor nhiệt tình, đúng giờ");
		commentResponse.setCreatedDate(32546465545L);
		commentResponse.setFullName("Nguyễn Văn Học");
		commentResponse.setRating(5);
		commentResponse.setId(15L);
		commentResponse.setGender(true);
		return new PagingWrapperResponse(Arrays.asList(commentResponse), 1);
	}
}