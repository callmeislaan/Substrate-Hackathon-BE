package com.backend.apiserver.utils;

import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.RequestConfirmationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ScheduleUtils
 */
@Component
@AllArgsConstructor
public class ScheduleUtils {

	/**
	 * logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ScheduleUtils.class);

	/**
	 * UserRepository
	 */
	private UserRepository userRepository;

	/**
	 * RequestRepository
	 */
	private RequestRepository requestRepository;

	/**
	 * RequestConfirmationService
	 */
	private RequestConfirmationService requestConfirmationService;

	/**
	 * Using to delete pending user that don't active mail after 12 hours
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void expiredPendingUsersRemover() {
		LOG.info("Start to delete expired pending user");
		List<User> pendingUsers = userRepository.findAllByStatus(Status.PENDING);
		List<User> expiredPendingUsers = pendingUsers.stream()
				.filter(user -> {
					Duration duration = Duration.between(user.getCreatedDate(), LocalDateTime.now());
					return duration.toHours() > Constants.VALID_ACTIVE_HOURS;
				})
				.collect(Collectors.toList());
		userRepository.deleteAll(expiredPendingUsers);
		LOG.info("End to delete expired pending user, total deleted: {}", expiredPendingUsers.size());
	}

	/**
	 * Using to back money to user when mentor doesn't confirm request status
	 */
	@Scheduled(cron = "0 55 0 ? * *")
	public void mentorIgnoreConfirmRequestHandler() throws Exception {
		LocalDateTime startOfToday = LocalDateTime.now().minusDays(1);
		List<Request> requests = requestRepository.findAllExpiredRejectRequest(startOfToday, Status.REJECT.toString());
		LOG.info("Total expired reject request: " + requests.size());
		if (requests.isEmpty()) return;
		for (Request request : requests) {
			requestConfirmationService.removeExpiredRejectRequest(request.getId());
		}
	}
}
