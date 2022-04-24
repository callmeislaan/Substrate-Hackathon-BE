package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.FilterRequestWrapperRequest;
import com.backend.apiserver.bean.request.RequestRequest;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;

public interface RequestService {

	/**
	 * Get all request for home screen
	 *
	 * @return ResponseWrapper wrapper of request
	 */
	WrapperResponse getHomeRequests();

	/**
	 * Get only requests create by user id
	 *
	 * @return ResponseWrapper wrapper of request
	 */
	PagingWrapperResponse getAllCreatedRequests(Integer page, Integer size);

	/**
	 * Get a request by id
	 *
	 * @param id request id
	 * @return RequestResponse
	 */
	RequestResponse getRequest(Long id) throws NotFoundException;

	/**
	 * Update request information
	 *
	 * @param requestRequest request information
	 * @param requestId      id of request
	 */
	void updateRequest(Long requestId, RequestRequest requestRequest) throws NotFoundException, MoneyRelatedException;

	/**
	 * Create new user request
	 *
	 * @param requestRequest
	 */
	Long createRequest(RequestRequest requestRequest) throws NotFoundException, MoneyRelatedException;

	/**
	 * Close a request by set delete status
	 *
	 * @param requestId id of request
	 */
	void closeRequest(Long requestId) throws NotFoundException;

	/**
	 * Reopen a request
	 *
	 * @param requestId id of request
	 */
	void reopenRequest(Long requestId) throws NotFoundException;

	/**
	 * Follow or unfollow a request
	 *
	 * @param requestId
	 * @return FollowingResponse
	 */
	FollowingResponse followOrUnfollowRequest(Long requestId) throws NotFoundException;

	/**
	 * Unfollow all request
	 */
	void unfollowAllRequest();

	/**
	 * get all following request
	 */
	PagingWrapperResponse getFollowingRequests(Integer page, Integer size);

	/**
	 * filter mentor
	 *
	 * @param filterRequestWrapperRequest
	 * @return PagingWrapperResponse
	 */
	PagingWrapperResponse filterRequest(FilterRequestWrapperRequest filterRequestWrapperRequest);

	WrapperResponse getOtherRequests(Long requestId);

	PagingWrapperResponse getReceivedRequest(Integer page, Integer size);

	PagingWrapperResponse getAllAdminRequest(boolean conflict, Integer page, Integer size, String keyword);

	PagingWrapperResponse getAllRentMentors(Integer page, Integer size);
}
