package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.FilterRequestWrapperRequest;
import com.backend.apiserver.bean.request.RequestRequest;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.bean.response.ResponseWithId;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.RequestService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class RequestController {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RequestController.class);

	/**
	 * RequestService
	 */
	private RequestService requestService;

	@GetMapping("api/public/requests")
	public WrapperResponse getHomeRequests() {
		LOG.info("Start to find all request");
		WrapperResponse wrapperResponse = requestService.getHomeRequests();
		LOG.info("End to find all request");
		return wrapperResponse;
	}

	@PostMapping("api/public/requests/filter")
	public PagingWrapperResponse filterRequest(@Valid @RequestBody FilterRequestWrapperRequest filterRequestWrapperRequest) {
		LOG.info("Start to filter request");
		PagingWrapperResponse pagingWrapperResponse = requestService.filterRequest(filterRequestWrapperRequest);
		LOG.info("End to filter request");
		return pagingWrapperResponse;
	}

	@GetMapping("api/user/requests")
	public PagingWrapperResponse getAllCreatedRequests(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		LOG.info("Start to find all created request");
		PagingWrapperResponse wrapperResponse = requestService.getAllCreatedRequests(page, size);
		LOG.info("End to find all created request");
		return wrapperResponse;
	}

	@GetMapping("api/public/request/other-requests/{requestId}")
	public WrapperResponse getOtherRequest(@PathVariable final Long requestId) {
		return requestService.getOtherRequests(requestId);
	}

	@GetMapping("api/public/request/{requestId}")
	public RequestResponse getRequest(@PathVariable final Long requestId) throws BadRequestException {
		try {
			LOG.info("Start to find user request");
			RequestResponse requestResponse = requestService.getRequest(requestId);
			LOG.info("End to find user request");
			return requestResponse;
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound, requestId);
		}
	}

	@PostMapping("api/user/request")
	public ResponseWithId createRequest(@Valid @RequestBody final RequestRequest requestRequest) throws BadRequestException {
		try {
			LOG.info("Start to create user request");
			Long requestId = requestService.createRequest(requestRequest);
			LOG.info("End to create user request");
			return new ResponseWithId(ResponseMessage.RequestHasBeenCreated, requestId);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestHasInvalidSkill);
		} catch (MoneyRelatedException e) {
			throw new BadRequestException(ResponseMessage.RequestMoneyWithdrawExceedBalance);
		}
	}

	@PutMapping("api/user/request/{requestId}")
	public Response updateRequest(@PathVariable final Long requestId, @Valid @RequestBody final RequestRequest requestRequest) throws BadRequestException {
		try {
			LOG.info("Start to update user request");
			requestService.updateRequest(requestId, requestRequest);
			LOG.info("End to update user request");
			return new Response(ResponseMessage.RequestHasBeenUpdated);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound, requestId);
		} catch (MoneyRelatedException e) {
			throw new BadRequestException(ResponseMessage.RequestMoneyWithdrawExceedBalance);
		}
	}

	@DeleteMapping("api/user/request/{requestId}")
	public Response closeRequest(@PathVariable final Long requestId) throws BadRequestException {
		try {
			LOG.info("Start to close user request");
			requestService.closeRequest(requestId);
			LOG.info("End to close user request");
			return new Response(ResponseMessage.RequestHasBeenDeleted);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound, requestId);
		}
	}

	@PostMapping("api/user/reopen-request/{requestId}")
	public Response reopenRequest(@PathVariable final Long requestId) throws BadRequestException {
		try {
			LOG.info("Start to reopen user request");
			requestService.reopenRequest(requestId);
			LOG.info("End to reopen user request");
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound, requestId);
		}
	}

	@Secured("ROLE_MENTOR")
	@PostMapping("api/request/following-or-unfollowing/{requestId}")
	public FollowingResponse followOrUnfollowRequest(@PathVariable final Long requestId) throws NotFoundException {
		LOG.info("Start to follow or unfollow request");
		FollowingResponse followingResponse = requestService.followOrUnfollowRequest(requestId);
		LOG.info("End to follow or unfollow request");
		return followingResponse;
	}

	@Secured("ROLE_MENTOR")
	@PostMapping("api/request/unfollowing-all")
	public Response unfollowAllRequest() {
		LOG.info("Start to unfollow all request");
		requestService.unfollowAllRequest();
		LOG.info("End to unfollow all request");
		return new Response(ResponseMessage.FollowOrUnfollowSuccess);
	}

	@Secured("ROLE_MENTOR")
	@GetMapping("api/request/following-requests")
	public PagingWrapperResponse getFollowingRequests(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		LOG.info("Start to get following request");
		PagingWrapperResponse responseWrapper = requestService.getFollowingRequests(page, size);
		LOG.info("End to get following request");
		return responseWrapper;
	}

	@Secured("ROLE_MENTOR")
	@GetMapping("api/request/mentor/receive-request")
	public PagingWrapperResponse getReceivedRequest(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		return requestService.getReceivedRequest(page, size);
	}


	@GetMapping("api/hiring/rent-mentors")
	public PagingWrapperResponse getListRentMentors(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		return requestService.getAllRentMentors(page, size);
	}
}
