package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.response.CommentResponse;
import com.backend.apiserver.entity.Comment;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.utils.DateTimeUtils;

public class CommentMapper {

	public static Comment createCommentEntity(User user, Mentor mentor, int rating, String content) {
		Comment comment = new Comment();
		comment.setUser(user);
		comment.setMentor(mentor);
		comment.setRating(rating);
		comment.setContent(content);
		return comment;
	}

	public static CommentResponse convertToResponse(Comment comment) {
		CommentResponse commentResponse = new CommentResponse();
		UserDetail userDetail = comment.getUser().getUserDetail();
		commentResponse.setAvatar(userDetail.getAvatar());
		commentResponse.setContent(comment.getContent());
		commentResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(comment.getCreatedDate()));
		commentResponse.setFullName(userDetail.getFullName());
		commentResponse.setRating(comment.getRating());
		commentResponse.setId(comment.getId());
		commentResponse.setGender(userDetail.isGender());
		return commentResponse;
	}
}
