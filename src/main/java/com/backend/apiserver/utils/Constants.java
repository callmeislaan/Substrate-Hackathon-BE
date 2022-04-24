package com.backend.apiserver.utils;

import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;

public class Constants {

	/**
	 * Role user
	 */
	public static final String ROLE_MENTEE = "ROLE_MENTEE";

	/**
	 * Role mentor
	 */
	public static final String ROLE_MENTOR = "ROLE_MENTOR";

	/**
	 * Role mentor
	 */
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	/**
	 * CLAIM_ID
	 */
	public static final String CLAIM_USER_ID = "id";

	/**
	 * PAYMENT
	 */
	public static final String PAYMENT = "Thanh toán";

	/**
	 * RECEIVE
	 */
	public static final String RECEIVE = "Nhận tiền";

	/**
	 * NUMBER OF SUGGESTION
	 */
	public static final int NUMBER_OF_SUGGESTION = 100;

	/**
	 * HOURS BEFORE EXPIRE PENDING USER
	 */
	public static final int VALID_ACTIVE_HOURS = 1;

	/**
	 * ACTIVATION PATH
	 */
	public static final String ACTIVATION_PATH = "api/activation";

	public static final String REQUEST_PATH = "request";

	public static final String ACTIVE_PAGE = "/active-user";

	public static final String ERROR_PAGE = "/error";

	/**
	 * ACTIVATION EXPIRE TIME
	 */
	public static final long ACTIVATION_EXPIRE_TIME = 1800000;

	public static final String PENDING_MESSAGE = "Giao dịch chuyển tiền sẽ được xử lý trong khoảng 3 - 5 ngày làm việc.";

	public static final String PENDING_STATUS = "ĐANG CHỜ";

	public static final String SUCCESS_STATUS = "THÀNH CÔNG";

	public static final String PENDING_ANNOUNCEMENT = "Yêu cầu rút tiền của bạn đã được lưu lại với thông tin như sau:";

	public static final String SUCCESS_ANNOUNCEMENT = "Yêu cầu rút tiền của bạn đã được thực hiện thành công:";

	public static final String PENDING_COLOR = "#F1C40F";

	public static final String SUCCESS_COLOR = "#28A745";


	/**
	 * ANEST
	 */
	public static String ANEST = "ANEST";

	public static String DEFAULT_CRITERIA = "lastModifiedDate";

	public static Sort DEFAULT_ORDER = Sort.by(DEFAULT_CRITERIA).descending();

	public static DateTimeFormatter DATE_FORMAT_MMYYYY = DateTimeFormatter.ofPattern("MM/yyyy");

	public static DateTimeFormatter DATE_FORMAT_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public static int CANCELLATION_MILLIS = 300000;

	//firebase notification configuration
	public static String TYPE_REQUEST = "request";
	public static String TYPE_MONEY = "money";

	public static String TITLE_INVITATION = "LỜI MỜI";
	public static String TITLE_RESERVE_REQUEST = "NHẬN YÊU CẦU";
	public static String TITLE_ACCEPT_REQUEST = "CHẤP NHẬN YÊU CẦU";
	public static String TITLE_COMPLETE = "HOÀN THÀNH";
	public static String TITLE_CANCELLATION = "HỦY YÊU CẦU";
	public static String TITLE_INCOMPLETE = "PHẢN HỒI";
	public static String TITLE_CONFIRMATION = "XÁC NHẬN";
	public static String TITLE_ANNOUNCEMENT = "THÔNG BÁO";
	public static String TITLE_CONFLICT = "MÂU THUẪN";

	public static String CONTENT_INVITATION = "notification.invitation";
	public static String CONTENT_RESERVE_REQUEST = "notification.reserve.request";
	public static String CONTENT_ACCEPT_REQUEST = "notification.accept.request";
	public static String CONTENT_COMPLETE = "notification.complete";
	public static String CONTENT_CANCELLATION = "notification.cancellation";
	public static String CONTENT_INCOMPLETE = "notification.incomplete";
	public static String CONTENT_CONFIRMATION = "notification.confirmation";
	public static String CONTENT_CONFLICT = "notification.conflict";
	public static String CONTENT_RESOLVE_CONFLICT = "notification.resolve.conflict";
}
