package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.request.NotificationRequest;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.MessageUtils;

import java.time.LocalDateTime;

public class NotificationBuilder {

	public static NotificationRequest createInvitation(Long requestId, String username) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_INVITATION);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_INVITATION, String.valueOf(requestId), username));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createReserveRequest(Long requestId, String mentorFullName) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_RESERVE_REQUEST);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_RESERVE_REQUEST, mentorFullName));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createAcceptRequest(Long requestId) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_ACCEPT_REQUEST);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_ACCEPT_REQUEST, String.valueOf(requestId)));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createCompleteRequest(Long requestId, int amount) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_MONEY);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_COMPLETE);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_COMPLETE, String.valueOf(requestId), amount));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createIncompleteRequest(Long requestId) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_INCOMPLETE);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_INCOMPLETE, String.valueOf(requestId)));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createCancellation(Long requestId) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_CANCELLATION);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_CANCELLATION, String.valueOf(requestId)));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createConfirmation(Long requestId, String mentorFullName) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_CONFIRMATION);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_CONFIRMATION, mentorFullName));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createResolveConflict(Long requestId) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_ANNOUNCEMENT);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_RESOLVE_CONFLICT, String.valueOf(requestId)));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}

	public static NotificationRequest createConflict(Long requestId, String mentorFullName) {
		NotificationRequest invitationNotification = new NotificationRequest();
		invitationNotification.setType(Constants.TYPE_REQUEST);
		invitationNotification.setRequestId(requestId);
		invitationNotification.setTitle(Constants.TITLE_CONFLICT);
		invitationNotification.setContent(MessageUtils.getMessage(Constants.CONTENT_CONFLICT, mentorFullName, String.valueOf(requestId)));
		invitationNotification.setCreated(DateTimeUtils.toCurrentTimeMillis(LocalDateTime.now()));
		return invitationNotification;
	}
}
