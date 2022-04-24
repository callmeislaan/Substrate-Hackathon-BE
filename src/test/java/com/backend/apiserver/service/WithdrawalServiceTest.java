package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.WithdrawRequest;
import com.backend.apiserver.entity.BankCard;
import com.backend.apiserver.entity.EWallet;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.BankCardRepository;
import com.backend.apiserver.repository.EWalletRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.service.impl.WithdrawalServiceImpl;
import com.backend.apiserver.utils.JwtUtils;
import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WithdrawalServiceTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private MoneyOutHistoryRepository moneyOutHistoryRepository;

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private EWalletRepository eWalletRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    WithdrawalService withdrawalService = new WithdrawalServiceImpl(
            jwtUtils,
            mentorRepository,
            moneyOutHistoryRepository,
            bankCardRepository,
            eWalletRepository,
            emailSenderService
    );

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void withdrawWithEWallet() throws Exception {
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAmount(50);

        Mentor mentor = new Mentor();
        mentor.setTotalMoneyCurrent(100);

        User user = new User();

        UserDetail userDetail = new UserDetail();
        userDetail.setFullName("A");

        user.setUserDetail(userDetail);
        mentor.setUser(user);


        when(jwtUtils.getUserId()).thenReturn(1L);

        when(mentorRepository.findMentorByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(mentor);
        when(eWalletRepository.findByIdAndMentorIdAndStatus(1L, 1L, Status.ACTIVE)).thenReturn(new EWallet());

        withdrawalService.withdrawWithEWallet(1L, withdrawRequest);
    }

    @Test
    public void withdrawWithBankCard() throws MessagingException, TemplateException, MoneyRelatedException, NotFoundException, IOException {
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAmount(50);

        Mentor mentor = new Mentor();
        mentor.setTotalMoneyCurrent(100);

        User user = new User();

        UserDetail userDetail = new UserDetail();
        userDetail.setFullName("A");

        user.setUserDetail(userDetail);
        mentor.setUser(user);


        when(jwtUtils.getUserId()).thenReturn(1L);

        when(mentorRepository.findMentorByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(mentor);
        when(bankCardRepository.findByIdAndMentorIdAndStatus(1L, 1L, Status.ACTIVE)).thenReturn(new BankCard());

        withdrawalService.withdrawWithBankCard(1L, withdrawRequest);
    }
}