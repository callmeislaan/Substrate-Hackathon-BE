package com.backend.apiserver.service.impl;

import com.backend.apiserver.bean.response.MentorRatingResponse;
import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.RatingBean;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.mapper.MentorMapper;
import com.backend.apiserver.repository.CommentRepository;
import com.backend.apiserver.repository.MentorFollowingRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.service.SuggestionService;
import com.backend.apiserver.utils.CFUtils;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private JwtUtils jwtUtils;

    private MentorRepository mentorRepository;

    private MentorFollowingRepository mentorFollowingRepository;

    private CommentRepository commentRepository;

    private SkillRepository skillRepository;

    @Override
    public WrapperResponse getAllAnestMentorSuggestion(List<Long> skillIds) {
        List<Mentor> mentors = mentorRepository.getListAnestMentorSuggestionByListSkillId(skillIds);
        return new WrapperResponse(
                mentors.stream()
                        .map(mentor -> MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString())))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public WrapperResponse getAllFollowingMentorSuggestion(List<Long> skillIds) {
        Long userId = jwtUtils.getUserId();
        Set<Long> mentorIds = mentorFollowingRepository.getListMentorIdByUserId(userId);
        List<Mentor> mentors = mentorRepository.getListFollowingMentorSuggestionByListSkillIdAndListMentorId(skillIds, mentorIds);
        return new WrapperResponse(
                mentors.stream()
                        .map(mentor -> MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString())))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public WrapperResponse getAllHiredMentorSuggestion(List<Long> skillIds) {
        Long userId = jwtUtils.getUserId();
        List<RatingBean> ratingBeans = commentRepository.getListRatingByListSkillIdAndUserId(skillIds, userId);
        List<MentorShortDescriptionResponse> mentorShortDescriptionRespons =
                ratingBeans.stream()
                        .map(ratingBean -> {
                            Mentor mentor = mentorRepository.findByUserIdAndStatus(ratingBean.getMentorId(), Status.ACTIVE);
                            return MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString()));
                        }).collect(Collectors.toList());
        return new WrapperResponse(mentorShortDescriptionRespons);
    }

    @Override
    public WrapperResponse getAllBestMentorSuggestion(List<Long> skillIds) {
        try {
            Long userId = jwtUtils.getUserId();

            //get all rating
            List<RatingBean> ratingBeans = commentRepository.getListRatingBean();

            //if no rating, select by avg rating of mentor
            if (ratingBeans.isEmpty()) return getTopRatingMentor(1, Constants.NUMBER_OF_SUGGESTION);

            // recommend
            HashMap<Long, Float> currentUserRatingHashMap = CFUtils.recommend(ratingBeans, userId, Constants.NUMBER_OF_SUGGESTION);

            //create tree set order by rating desc
            TreeSet<MentorRatingResponse> mentorRatingResponses = new TreeSet<>((o1, o2) -> {
                if(o1.getRating() < o2.getRating()){
                    return 1;
                } else {
                    return -1;
                }
            });
            for (Long mentorId : currentUserRatingHashMap.keySet()) {
                Mentor mentor = mentorRepository.findByUserIdAndStatus(mentorId, Status.ACTIVE);
                if (!Objects.isNull(mentor)) {
                    MentorRatingResponse mentorRatingResponse = new MentorRatingResponse();
                    MentorShortDescriptionResponse mentorShortDescriptionResponse = MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString()));

                    if (mentorFollowingRepository.existsByMentorIdAndUserIdAndStatus(mentorId, userId, Status.ACTIVE))
                        mentorShortDescriptionResponse.setFollowed(true);

                    if (currentUserRatingHashMap.get(mentorId) >= 10) {
                        mentorRatingResponse.setRating(currentUserRatingHashMap.get(mentorId) / 10);
                        mentorShortDescriptionResponse.setHired(true);
                    } else {
                        mentorRatingResponse.setRating(currentUserRatingHashMap.get(mentorId));
                        mentorShortDescriptionResponse.setHired(false);
                    }
                    mentorRatingResponse.setMentorInfo(mentorShortDescriptionResponse);
                    mentorRatingResponses.add(mentorRatingResponse);
                }

            }

            System.out.println("Total: " + mentorRatingResponses.size());
            return new WrapperResponse(mentorRatingResponses);
        } catch (Exception e) {
            return getTopRatingMentor(1, Constants.NUMBER_OF_SUGGESTION);
        }
    }

    private WrapperResponse getTopRatingMentor(int page, int size) {
        List<Mentor> mentors = mentorRepository.findAllActiveMentor(PageRequest.of(page - 1, size));
        return new WrapperResponse(
                mentors.stream()
                        .map(mentor -> {
                            MentorRatingResponse mentorRatingResponse = new MentorRatingResponse();
                            MentorShortDescriptionResponse mentorShortDescriptionResponse = MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString()));
                            mentorRatingResponse.setMentorInfo(mentorShortDescriptionResponse);
                            return mentorRatingResponse;
                        })
                        .collect(Collectors.toList())
        );
    }
}
