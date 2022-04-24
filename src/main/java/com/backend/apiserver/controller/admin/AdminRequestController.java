package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.RequestConfirmationService;
import com.backend.apiserver.service.RequestService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@AllArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("api/admin/")
public class AdminRequestController {

	private RequestService requestService;

	private RequestConfirmationService requestConfirmationService;

	@GetMapping("requests")
	public PagingWrapperResponse viewAllRequests(
			@RequestParam(required = false) boolean conflict,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer size,
			@RequestParam(required = false) String keyword
	) {
		PagingWrapperResponse pagingWrapperResponse = requestService.getAllAdminRequest(conflict, page, size, keyword);
		return pagingWrapperResponse;
	}

	@PostMapping("resolve-conflict/{requestId}")
	public Response resolveConflict(@PathVariable final Long requestId, @RequestParam final boolean forUser) throws BadRequestException, MessagingException, IOException, TemplateException, URISyntaxException {
		try {
			requestConfirmationService.resolveConflict(requestId, forUser);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound);
		}
	}
}
