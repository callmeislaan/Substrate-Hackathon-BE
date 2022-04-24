package com.backend.apiserver.bean.response;

import com.backend.apiserver.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    InvalidAccessError("99", "api.error.response.invalid.access")
    , DefaultInternalServerMessageError("100", "api.error.response.default.internal.message.error")
    , DuplicatedUsername("101", "api.error.response.duplicated.username")
    , DuplicatedEmail("98", "api.error.response.duplicated.email")
    , UserRoleNotFound("102", "api.error.response.user.role.not.found")
    , CardIdNotFound("103", "api.error.response.card.id.not.found")
    , AuthenticateUserFailed("104", "api.error.response.authenticate.user.failed")
    , CreateCardSuccess("105", "api.success.response.create.card.success")
    , UpdateCardSuccess("106", "api.success.response.update.card.success")
    , DeleteCardSuccess("107", "api.success.response.delete.card.success")
    , RegisterUserSuccess("108", "api.success.response.create.user.success")
    , UserUpdatePasswordSuccess("109", "api.success.response.user.update.password.success")
    , UserIdNotFound("110", "api.error.response.user.id.not.found")
    , UpdatePasswordNotMatch("111", "api.error.response.password.not.match")
    , UpdatePasswordSuccess("112", "api.success.response.update.password.success")
    , RegisterMentorSuccess("113", "api.success.response.register.mentor.success")
    , RemoveMentorSuccess("114", "api.success.response.remove.mentor.success")
    , UnauthorizedExceptionMessage("115", "api.error.response.unauthorized.exception.message")
    , ForbiddenExceptionMessage("116", "api.error.response.forbidden.exception.message")
    , RequestWithdrawCreatedSuccess("117", "api.success.response.request.withdraw.created.success")
    , RequestMoneyWithdrawExceedBalance("118", "api.error.response.request.money.is.exceed.balance")
    , AlreadyUpdatedByAnotherTransaction("119", "api.error.response.table.has.been.updated.by.another.transaction")
    , RequestWithdrawalSuccess("120", "api.success.response.request.withdrawal.success")
    , RequestPayInSuccess("121", "api.success.response.request.pay.in.success")
    , MentorRoleNotFound("122", "api.error.response.mentor.role.not.found")
    , RequestNotFound("123", "api.error.response.request.not.found")
    , RequestHasBeenCreated("124", "api.success.response.request.has.been.created")
    , RequestHasInvalidSkill("127", "api.error.response.request.has.invalid.skill")
    , RequestHasBeenUpdated("125", "api.success.response.request.has.been.updated")
    , RequestHasBeenDeleted("126", "api.success.response.request.has.been.deleted")
    , FollowOrUnfollowSuccess("127", "api.success.response.follow.or.unfollow.success")
    , UpdateSkillSuccess("128", "api.success.response.update.skill.success")
    , DeleteSkillSuccess("129", "api.success.response.delete.skill.success")
    , MentorNotFound("130", "api.error.response.mentor.not.found")
    , InviteMentorSuccess("131", "api.success.response.invite.mentor.success")
    , ReserveWrongRequestException("132", "api.error.response.mentor.reserve.wrong.request")
    , ReserveRequestSuccess("133", "api.error.response.mentor.reserve.request.success")
    , UndoReserveRequestSuccess("133", "api.error.response.mentor.undo.reserve.request.success")
    , ConfirmChooseMentorSuccess("135", "api.success.response.confirm.choose.mentor.success")
    , MentorRequestNotFound("134", "api.error.response.mentor.request.not.found")
    , AcceptRequestInvitationSuccess("136", "api.success.response.accept.invitation.success")
    , ConfirmRentMentorSuccess("137", "api.success.response.confirm.rent.mentor.success")
    , DeleteMentorRequestSuccess("138", "api.success.response.confirm.delete.mentor.request.success")
    , ConfirmMentorFinishRequestSuccess("140", "api.success.response.confirm.mentor.finish.request.success")
    , UserRejectRequestSuccess("141", "api.success.response.reject.request.success")
    , MentorConfirmRejectRequestSuccess("142", "api.success.response.confirm.reject.request.success")
    , MentorDenyRejectRequestSuccess("143", "api.success.response.deny.reject.request.success")
    , PerformOperationSuccess("150", "api.success.response.perform.operation.success")
    , PerformOperationFail("151", "api.error.response.perform.operation.fail")
    , InvitationNotFound("152", "api.error.response.invitation.not.found")
    , ConstraintViolationException("153", "api.error.response.constraint.violation.exception")
    , DataNotFoundException("154", "api.error.response.data.not.found.exception")
    , TopUpNotFoundException("155", "api.error.response.top.up.not.found.exception")
    , TopUpAlreadyUsedException("156", "api.error.response.top.up.is.already.used.exception")
    , TopUpCodeIsWrongException("157", "api.error.response.top.up.code.is.wrong.exception")
    , SendMailException("158", "api.error.response.sending.mail.exception")
    , NotFoundPendingRegisterUser("159", "api.error.response.not.found.pending.user.exception")
    , RequestActiveUserExpired("160", "api.error.response.request.active.user.expired.exception")
    , RequestActiveUserSuccess("161", "api.success.response.request.active.user.success")
    , NotAnestMentorException("166", "api.error.response.not.anest.mentor.exception")
    , PerformOperationYourSelf("167", "api.error.response.perform.operation.on.yourself.exception")
    , TokenExpiredException("168", "api.error.response.token.expired.exception")
    , MentorHaveDoingRequestException("169", "api.error.response.mentor.have.doing.request")
    , WithdrawMoneyExceedCurrentValueException("170", "api.error.response.mentor.money.exceed.current.value")

//    Mail content notification
    , MailResponseRequestConflict("161", "api.mail.response.request.conflict")
    , MailResponseRequestSuccess("162", "api.mail.response.request.success")
    , MailResponseWithdrawalPending("163", "api.mail.response.withdrawal.pending")
    , MailResponseWithdrawalSuccess("164", "api.mail.response.withdrawal.success")
    , MailResponseRequestRentMentor("165", "api.mail.response.hire.mentor")

    ;
    private String code;
    private String messageId;

    public String getMessage(Object... params) {
        String message = MessageUtils.getMessage(this.messageId, params);
        return message;
    }
}