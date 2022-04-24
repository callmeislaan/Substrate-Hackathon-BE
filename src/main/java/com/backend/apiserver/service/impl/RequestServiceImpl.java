package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.FilterRequestWrapperRequest;
import com.backend.apiserver.bean.request.RequestRequest;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestAdminResponse;
import com.backend.apiserver.bean.response.RequestResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.RequestFollowing;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.mapper.MentorMapper;
import com.backend.apiserver.mapper.RequestMapper;
import com.backend.apiserver.repository.MentorFollowingRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestFollowingRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.RequestService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.JwtUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

	/**
	 * JwtUtils
	 */
	private JwtUtils jwtUtils;

	/**
	 * RequestRepository
	 */
	private RequestRepository requestRepository;

	/**
	 * SkillRepository
	 */
	private SkillRepository skillRepository;

	/**
	 * InviteMentorRepository
	 */
	private RequestAnnouncementRepository requestAnnouncementRepository;

	/**
	 * MentorRepository
	 */
	private MentorRepository mentorRepository;

	/**
	 * UserRepository
	 */
	private UserRepository userRepository;

	/**
	 * RequestFollowingRepository
	 */
	private RequestFollowingRepository requestFollowingRepository;

	/**
	 * MentorRequestRepository
	 */
	private MentorRequestRepository mentorRequestRepository;

	/**
	 * UserDetailRepository
	 */
	private UserDetailRepository userDetailRepository;

	private MentorFollowingRepository mentorFollowingRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WrapperResponse getHomeRequests() {
		Long userId = jwtUtils.getUserId();
		List<Request> requests = requestRepository.findFirst50ByStatusOrderByIdDesc(Status.OPEN);
		if (requests.isEmpty()) return new WrapperResponse(Collections.emptyList());
		return new WrapperResponse(
				requests.stream()
						.map(request -> RequestMapper.requestEntityToResponse(request, userId, requestFollowingRepository, mentorRequestRepository))
						.collect(Collectors.toList())
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PagingWrapperResponse getAllCreatedRequests(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		Page<Request> requests = requestRepository.findAllByUserId(userId, PageRequest.of(page - 1, size, Sort.by("lastModifiedDate").descending()));
		if (requests.isEmpty()) return new PagingWrapperResponse(Collections.emptyList(), 0);
		return new PagingWrapperResponse(
				requests.stream()
						.map(request -> RequestMapper.requestEntityToResponse(request, userId, requestFollowingRepository, mentorRequestRepository))
						.collect(Collectors.toList()),
				requests.getTotalElements()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RequestResponse getRequest(Long id) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		Request request = requestRepository.findRequestById(id);
		if (Objects.isNull(request)) throw new NotFoundException("Not found any request with id: " + id);
		RequestResponse requestResponse = RequestMapper.requestEntityToResponse(request, userId, requestFollowingRepository, mentorRequestRepository);
		if (Status.DOING == request.getStatus()) {
			List<RequestAnnouncement> requestAnnouncements = requestAnnouncementRepository.findByRequestId(request.getId());

			List<RequestAnnouncement> filteredList = requestAnnouncements.stream()
					.filter(announcement -> announcement.getStatus() != Status.INVITE)
					.collect(Collectors.toList());

			if (!filteredList.isEmpty()) {
				requestResponse.setConfirmStatus(filteredList.get(0).getStatus().toString());
			}
		}
		return requestResponse;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void updateRequest(Long requestId, RequestRequest requestRequest) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		//get request to update
		Request request = requestRepository.findByIdAndUserIdAndStatus(requestId, userId, Status.OPEN);
		if (Objects.isNull(request)) {
			throw new RequestNotFoundException("Not found any request with id: " + requestId);
		}
		//Check valid list skills
		if (!skillRepository.existsAllByIdInAndStatus(requestRequest.getSkillIds(), Status.ACTIVE)) {
			throw new NotFoundException("List skill contain invalid or deleted skill");
		}
		//Get skills from database
		List<Skill> skills = skillRepository.findAllByIdInAndStatus(requestRequest.getSkillIds(), Status.ACTIVE);
		//Create new request entity to save new record
		request.setPrice(requestRequest.getPrice());
		request.setTitle(requestRequest.getTitle());
		request.setContent(requestRequest.getContent());
		request.setDeadline(DateTimeUtils.fromCurrentTimeMillis(requestRequest.getDeadline()));
		request.getSkills().clear();
		request.getSkills().addAll(skills);
		requestRepository.saveAndFlush(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public Long createRequest(RequestRequest requestRequest) throws NotFoundException {
		//Check valid list skills
		if (!skillRepository.existsAllByIdInAndStatus(requestRequest.getSkillIds(), Status.ACTIVE)) {
			throw new NotFoundException("List skill contain invalid or deleted skill");
		}
		//Get userId from request
		Long userId = jwtUtils.getUserId();
		//Get skills from database
		List<Skill> skills = skillRepository.findAllByIdInAndStatus(requestRequest.getSkillIds(), Status.ACTIVE);
		//Create new request entity to save new record
		Request request = new Request();
		request.setTitle(requestRequest.getTitle());
		request.setContent(requestRequest.getContent());
		request.setDeadline(DateTimeUtils.fromCurrentTimeMillis(requestRequest.getDeadline()));
		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);
		//set price, status, add skill to request to save to db
		request.setPrice(requestRequest.getPrice());
		request.setUser(user);
		request.setStatus(Status.OPEN);
		request.getSkills().addAll(skills);
		UserDetail userDetail = userDetailRepository.findUserDetailByUserId(userId);
		userDetail.setTotalRequestCreate(userDetail.getTotalRequestCreate() + 1);
		userDetailRepository.saveAndFlush(userDetail);
		return requestRepository.saveAndFlush(request).getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void closeRequest(Long requestId) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		Request request = requestRepository.findRequestByIdAndUserIdAndStatus(requestId, userId, Status.OPEN);
		if (Objects.isNull(request)) throw new NotFoundException("Not found any request with id: " + requestId);
		request.setStatus(Status.DELETE);
		requestRepository.saveAndFlush(request);
		requestAnnouncementRepository.deleteAllByRequestIdAndStatus(requestId, Status.INVITE);
		// mentorRequestRepository.deleteAllByRequestId(requestId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void reopenRequest(Long requestId) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		Request request = requestRepository.findRequestByIdAndUserIdAndStatus(requestId, userId, Status.DELETE);
		if (Objects.isNull(request)) throw new NotFoundException("Not found any request with id: " + requestId);
		request.setStatus(Status.OPEN);
		requestRepository.saveAndFlush(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public FollowingResponse followOrUnfollowRequest(Long requestId) throws NotFoundException {
		//validate request exist
		Request request = requestRepository.findByIdAndStatusNot(requestId, Status.DELETE);
		if (Objects.isNull(request)) throw new NotFoundException("Not found any request with id: " + requestId);

		//validate mentor exist
		Long userId = jwtUtils.getUserId();
		Mentor mentor = mentorRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
		if (Objects.isNull(mentor)) throw new NotFoundException("Not found any mentor with id: " + userId);

		//get request following by mentorId and requestId
		RequestFollowing requestFollowing = requestFollowingRepository.findByMentorIdAndRequestId(userId, requestId);
		if (Objects.isNull(requestFollowing)) {
			requestFollowing = new RequestFollowing();
			requestFollowing.setMentor(mentor);
			requestFollowing.setRequest(request);
			requestFollowing.setStatus(Status.ACTIVE);
		} else if (Status.ACTIVE.toString().equals(requestFollowing.getStatus().toString())) {
			requestFollowing.setStatus(Status.DELETE);
		} else {
			requestFollowing.setStatus(Status.ACTIVE);
		}
		requestFollowingRepository.saveAndFlush(requestFollowing);

		FollowingResponse followingResponse = new FollowingResponse();
		followingResponse.setFollowingStatus(requestFollowing.getStatus().toString());
		return followingResponse;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void unfollowAllRequest() {
		Long userId = jwtUtils.getUserId();
		requestFollowingRepository.unfollowAllRequest(Status.DELETE, Status.ACTIVE, userId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return
	 */
	@Override
	public PagingWrapperResponse getFollowingRequests(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		Page<RequestFollowing> requestFollowings = requestFollowingRepository.findAllByMentorIdAndStatusOrderByLastModifiedDateDesc(userId, Status.ACTIVE, PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
		if (requestFollowings.isEmpty()) return new PagingWrapperResponse(Collections.emptyList(), 0);
		return new PagingWrapperResponse(
				requestFollowings.stream()
						.map(requestFollowing -> RequestMapper.requestEntityToResponse(requestFollowing.getRequest(), userId, requestFollowingRepository, mentorRequestRepository))
						.map(requestResponse -> {
							requestResponse.setBookmarked(true);
							return requestResponse;
						})
						.collect(Collectors.toList()),
				requestFollowings.getTotalElements()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PagingWrapperResponse filterRequest(FilterRequestWrapperRequest filterRequestWrapperRequest) {
		Long userId = jwtUtils.getUserId();
		Page<Request> requests = requestRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
			//create default predicate
			Predicate p = criteriaBuilder.conjunction();

			//keyword search
			if (!StringUtils.isEmpty(filterRequestWrapperRequest.getKeyWord())) {
				p = criteriaBuilder.and(
						p, criteriaBuilder.or(
								criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + filterRequestWrapperRequest.getKeyWord().toLowerCase() + "%"),
								criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + filterRequestWrapperRequest.getKeyWord().toLowerCase() + "%")
						)
				);
			}

			//list skill id search
			if (!CollectionUtils.isEmpty(filterRequestWrapperRequest.getFilter().getSkillIds())) {
				List<Long> requestIds = requestRepository.getListRequestIdByListSkillId(filterRequestWrapperRequest.getFilter().getSkillIds());
				if (!CollectionUtils.isEmpty(requestIds)) {
					p = criteriaBuilder.and(p, root.get("id").in(requestIds));
				}
			}

			//price search
			p = criteriaBuilder.and(p, criteriaBuilder.between(root.get("price"), filterRequestWrapperRequest.getFilter().getMinPrice(), filterRequestWrapperRequest.getFilter().getMaxPrice()));

			//status search
			Status status = null;
			switch (filterRequestWrapperRequest.getFilter().getStatus()) {
				case "OPEN":
					status = Status.OPEN;
					break;
				case "DOING":
					status = Status.DOING;
					break;
				case "COMPLETE":
					status = Status.COMPLETE;
					break;
				case "DELETE":
					status = Status.DELETE;
					break;
			}
			if (status != null) p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("status"), status));

			//sorting by
			Expression<Number> expression;
			switch (filterRequestWrapperRequest.getSort().toLowerCase()) {
				case "price":
					expression = root.get("price");
					break;
				case "deadline":
					expression = root.get("deadline");
					break;
				default:
					expression = root.get("createdDate");
					break;
			}

			//order
			switch (filterRequestWrapperRequest.getOrder().toLowerCase()) {
				case "asc":
					criteriaQuery.orderBy(criteriaBuilder.asc(expression));
					break;
				default:
					criteriaQuery.orderBy(criteriaBuilder.desc(expression));
					break;
			}

			return p;
		}, PageRequest.of(filterRequestWrapperRequest.getPage() - 1, filterRequestWrapperRequest.getSize()));
		return new PagingWrapperResponse(
				requests.getContent()
						.stream()
						.map(request -> RequestMapper.requestEntityToResponse(request, userId, requestFollowingRepository, mentorRequestRepository))
						.collect(Collectors.toList()),
				requests.getTotalElements()
		);
	}

	@Override
	public WrapperResponse getOtherRequests(Long requestId) {
		Long userId = jwtUtils.getUserId();
		List<Request> requests = requestRepository.findOtherRequests(requestId, Status.OPEN.toString());
		if (requests.isEmpty()) return new WrapperResponse(Collections.emptyList());
		return new WrapperResponse(
				requests.stream()
						.map(request -> RequestMapper.requestEntityToResponse(request, userId, requestFollowingRepository, mentorRequestRepository))
						.collect(Collectors.toList())
		);
	}

	@Override
	public PagingWrapperResponse getReceivedRequest(Integer page, Integer size) {
		List<Status> acceptedStatuses = Lists.newArrayList(Status.DOING, Status.COMPLETE);
		Long userId = jwtUtils.getUserId();
		List<MentorRequest> mentorRequests = mentorRequestRepository.findAllByMentorIdAndStatusIn(userId, acceptedStatuses);
		if (mentorRequests.isEmpty()) return new PagingWrapperResponse(Collections.emptyList(), 0);
		List<Long> requestIds = mentorRequests
				.stream()
				.map(mentorRequest -> mentorRequest.getRequest().getId())
				.collect(Collectors.toList());
		Page<Request> requests = requestRepository.findAllByIdIn(
				requestIds,
				PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER)
		);
		return new PagingWrapperResponse(
				requests.get()
						.map(request -> RequestMapper.requestEntityToResponse(request, userId, requestFollowingRepository, mentorRequestRepository))
						.collect(Collectors.toList()), requests.getTotalElements()
		);
	}

	@Override
	public PagingWrapperResponse getAllAdminRequest(boolean conflict, Integer page, Integer size, String keyword) {
		Page<Request> requests;
		if (conflict) {
			requests = requestRepository.findAllConflictRequestWithKeyword(Status.CONFLICT.toString(), keyword, PageRequest.of(page - 1, size));
		} else {
			requests = requestRepository.findAllByTitleContaining(keyword, PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
		}
		List<RequestAdminResponse> adminResponses = requests
				.stream()
				.map(request -> {
					RequestAdminResponse requestAdminResponse = new RequestAdminResponse();
					requestAdminResponse.setId(request.getId());
					requestAdminResponse.setCreator(request.getUser().getUsername());
					requestAdminResponse.setPrice(request.getPrice());
					requestAdminResponse.setTitle(request.getTitle());
					requestAdminResponse.setStatus(request.getStatus().toString());
					requestAdminResponse.setConflict(
							requestAnnouncementRepository.existsByRequestIdAndStatus(request.getId(), Status.CONFLICT)
					);
					return requestAdminResponse;
				})
				.collect(Collectors.toList());
		return new PagingWrapperResponse(adminResponses, requests.getTotalElements());
	}

	@Override
	public PagingWrapperResponse getAllRentMentors(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		Set<Long> followedMentorIds = mentorFollowingRepository.getListMentorIdByUserId(userId);
		Page<Mentor> mentors = mentorRepository.findAllRentMentorsByUserIdAndStatus(userId, Status.COMPLETE.toString(), PageRequest.of(page - 1, size, Sort.by("last_modified_date").descending()));
		return new PagingWrapperResponse(
				mentors.stream()
						.map(mentor -> MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString())))
						.map(mentorShortDescriptionResponse -> {
							if (followedMentorIds.contains(mentorShortDescriptionResponse.getId())) {
								mentorShortDescriptionResponse.setFollowed(true);
							}
							return mentorShortDescriptionResponse;
						})
						.collect(Collectors.toList()),
				mentors.getTotalElements()
		);
	}
}
