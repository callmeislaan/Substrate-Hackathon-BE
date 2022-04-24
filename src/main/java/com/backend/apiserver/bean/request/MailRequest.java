package com.backend.apiserver.bean.request;

import com.backend.apiserver.utils.FormatUtils;
import lombok.Data;

import java.util.Map;

@Data
public class MailRequest {
	//DEFAULT VALUE
	public static final String CC = "anest.academy@gmail.com";
	public static final String FROM = "Anest Academy <anest.academy@gmail.com>";

	//MAIL TITLE
	public static final String TITLE_REGISTRATION = "[ANEST] THÔNG BÁO ĐĂNG KÍ TÀI KHOẢN";
	public static final String TITLE_MONEY_IN = "[ANEST] THÔNG BÁO NẠP TIỀN";
	public static final String TITLE_MONEY_OUT = "[ANEST] THÔNG BÁO RÚT TIỀN";
	public static final String TITLE_REQUEST_SUCCESS = "[ANEST] THÔNG BÁO HOÀN THÀNH";
	public static final String TITLE_REQUEST_CONFLICT = "[ANEST] THÔNG BÁO XẢY RA MÂU THUẪN";
	public static final String TITLE_HIRE_MENTOR = "[ANEST] THÔNG BÁO YÊU CẦU THUÊ OFFLINE";
	public static final String TITLE_RESET_PASSWORD = "[ANEST] YÊU CẦU ĐẶT LẠI MẬT KHẨU";

	//MAIL TEMPLATE
	public static String TEMPLATE_REGISTRATION = "template-registration.ftl";
	public static String TEMPLATE_MONEY_IN = "template-money-in.ftl";
	public static String TEMPLATE_BANK_CARD = "template-bank-card.ftl";
	public static String TEMPLATE_E_WALLET = "template-e-wallet.ftl";
	public static String TEMPLATE_REQUEST_SUCCESS = "template-request-success.ftl";
	public static String TEMPLATE_REQUEST_CONFLICT = "template-request-conflict.ftl";
	public static String TEMPLATE_HIRE_MENTOR = "template-hire-mentor.ftl";
	public static String TEMPLATE_RESET_PASSWORD = "template-reset-password.ftl";

	private String from;
	private String [] to;
	private String [] cc;
	private String subject;
	private String body;
	private String template;
	private Map<String, Object> params;

	public MailRequest(String [] to, Map<String, Object> params, String template, String subject) {
		this.from = FROM;
		this.to = to;
		this.cc = FormatUtils.makeArray(CC);
		this.subject = subject;
		this.params = params;
		this.template = template;
	}
}
