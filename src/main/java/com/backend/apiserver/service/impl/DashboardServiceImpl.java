package com.backend.apiserver.service.impl;

import com.backend.apiserver.bean.response.DashboardResponse;
import com.backend.apiserver.bean.response.MonthlyMoneyStatisticResponse;
import com.backend.apiserver.bean.response.OverviewChartResponse;
import com.backend.apiserver.bean.response.OverviewDataResponse;
import com.backend.apiserver.bean.response.RequestStatisticResponse;
import com.backend.apiserver.bean.response.TopMentorDataResponse;
import com.backend.apiserver.bean.response.TopMentorResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.MoneyInHistoryRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.DashboardService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.FormatUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private UserRepository userRepository;

	private MentorRepository mentorRepository;

	private RequestRepository requestRepository;

	private MoneyInHistoryRepository moneyInHistoryRepository;

	private MoneyOutHistoryRepository moneyOutHistoryRepository;

	private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;

	private MentorRequestRepository mentorRequestRepository;

	private UserDetailRepository userDetailRepository;

	@Override
	public DashboardResponse getDashboardInfo() {
		//Count
		DashboardResponse dashboardResponse = new DashboardResponse();
		int numberMentors = mentorRepository.countMentorByStatus(Status.ACTIVE);
		int numberUsers = userRepository.countUserByStatus(Status.ACTIVE);
		int numberCreatedRequests = requestRepository.countAllRequests();
		int numberCompletedRequests = requestRepository.countRequestsByStatus(Status.COMPLETE);
		String completedRates = FormatUtils.percentageFormatter((float) numberCompletedRequests / numberCreatedRequests);
		int totalTransactions = moneyInHistoryRepository.countAllMoneyInHistories() +
				moneyOutHistoryRepository.countAllMoneyOutHistories() +
				moneyExchangeHistoryRepository.countAllMoneyExchangeHistories();
		//Set data to response
		dashboardResponse.setNumberMentors(numberMentors);
		dashboardResponse.setNumberUsers(numberUsers);
		dashboardResponse.setCompletedRates(completedRates);
		dashboardResponse.setNumberCreatedRequests(numberCreatedRequests);
		dashboardResponse.setTotalTransactions(totalTransactions);

		//Chart 1
		YearMonth now = YearMonth.now();

		List<LocalDate> queryMonths = IntStream.range(1, 13)
				.mapToObj(month -> now.withMonth(month).atDay(1))
				.collect(Collectors.toList());

		List<Integer> requestInfo = new ArrayList<>();
		List<Integer> mentorInfo = new ArrayList<>();
		List<Integer> transactionInfo = new ArrayList<>();

		List<MonthlyMoneyStatisticResponse> monthlyMoneyInStatistics = new ArrayList<>();
		List<MonthlyMoneyStatisticResponse> monthlyMoneyOutStatistics = new ArrayList<>();
		List<MonthlyMoneyStatisticResponse> monthlyMoneyExchangeStatistics = new ArrayList<>();

		for (LocalDate localDate : queryMonths) {
			LocalDateTime startDateTime = localDate.atTime(LocalTime.MIN);
			LocalDateTime endDateTime = localDate.with(lastDayOfMonth()).atTime(LocalTime.MAX);

			Integer totalRequest = requestRepository.countRequestsByStartDateAndEndDate(startDateTime, endDateTime);
			requestInfo.add(totalRequest);

			Integer totalMentor = mentorRequestRepository.countMentorRequestByStartDateAndEndDate(startDateTime, endDateTime, Status.PENDING.toString());
			mentorInfo.add(totalMentor);

			MonthlyMoneyStatisticResponse monthlyMoneyInResponse = new MonthlyMoneyStatisticResponse();
			MonthlyMoneyStatisticResponse monthlyOutResponse = new MonthlyMoneyStatisticResponse();
			MonthlyMoneyStatisticResponse monthlyExchangeResponse = new MonthlyMoneyStatisticResponse();

			String currentMonth  = localDate.format(Constants.DATE_FORMAT_MMYYYY);
			Integer totalMoneyInTransaction = moneyInHistoryRepository.countMoneyInByStartDateAndEndDate(startDateTime, endDateTime);
			monthlyMoneyInResponse.setTime(currentMonth);
			monthlyMoneyInResponse.setNumber(totalMoneyInTransaction);
			monthlyMoneyInStatistics.add(monthlyMoneyInResponse);

			Integer totalMoneyOutTransaction = moneyOutHistoryRepository.countMoneyOutByStartDateAndEndDate(startDateTime, endDateTime);
			monthlyOutResponse.setTime(currentMonth);
			monthlyOutResponse.setNumber(totalMoneyOutTransaction);
			monthlyMoneyOutStatistics.add(monthlyOutResponse);

			Integer totalMoneyExchangeTransaction = moneyExchangeHistoryRepository.countMoneyExchangeByStartDateAndEndDate(startDateTime, endDateTime);
			monthlyExchangeResponse.setTime(currentMonth);
			monthlyExchangeResponse.setNumber(totalMoneyExchangeTransaction);
			monthlyMoneyExchangeStatistics.add(monthlyExchangeResponse);

			transactionInfo.add(totalMoneyInTransaction + totalMoneyOutTransaction + totalMoneyExchangeTransaction);
		}

		List<String> timeSlices = queryMonths.stream()
				.map(localDate -> localDate.format(Constants.DATE_FORMAT_MMYYYY))
				.collect(Collectors.toList());

		OverviewDataResponse overviewDataResponse = new OverviewDataResponse();
		overviewDataResponse.setMentor(mentorInfo);
		overviewDataResponse.setRequest(requestInfo);
		overviewDataResponse.setTransaction(transactionInfo);

		OverviewChartResponse overviewChartResponse = new OverviewChartResponse();
		overviewChartResponse.setTimeSlices(timeSlices);
		overviewChartResponse.setData(overviewDataResponse);

		dashboardResponse.setOverviewCharts(overviewChartResponse);
		//Chart 2 top mentor
		List<Mentor> top5ExcellentMentor = mentorRepository.findTop5ExcellentMentor();

		List<String> fullNames = top5ExcellentMentor.stream()
				.map(mentor -> userDetailRepository.findByUserId(mentor.getId()).getFullName())
				.collect(Collectors.toList());

		TopMentorResponse topMentorResponse = new TopMentorResponse();
		topMentorResponse.setMentorNames(fullNames);

		TopMentorDataResponse topMentorDataResponse = new TopMentorDataResponse();
		List<Integer> finishData =  top5ExcellentMentor.stream().map(mentor -> mentor.getTotalRequestFinish()).collect(Collectors.toList());
		List<Integer> denyData =  top5ExcellentMentor.stream().map(mentor -> mentor.getTotalRequestDeny()).collect(Collectors.toList());

		topMentorDataResponse.setFinish(finishData);
		topMentorDataResponse.setDeny(denyData);

		topMentorResponse.setData(topMentorDataResponse);
		dashboardResponse.setTopMentor(topMentorResponse);

		//Chart 3
		RequestStatisticResponse requestStatisticResponse = new RequestStatisticResponse();

		int totalOpen = requestRepository.countRequestsByStatus(Status.OPEN);
		int totalDoing = requestRepository.countRequestsByStatus(Status.DOING);
		int totalComplete = requestRepository.countRequestsByStatus(Status.COMPLETE);
		int totalClose =requestRepository.countRequestsByStatus(Status.DELETE);

		requestStatisticResponse.setData(Lists.newArrayList(totalOpen, totalComplete, totalDoing, totalClose));
		dashboardResponse.setRequest(requestStatisticResponse);

		//Chart 4,5,6
		dashboardResponse.setMoneyIn(monthlyMoneyInStatistics);
		dashboardResponse.setMoneyOut(monthlyMoneyOutStatistics);
		dashboardResponse.setMoneyExchange(monthlyMoneyExchangeStatistics);

		return dashboardResponse;
	}
}
