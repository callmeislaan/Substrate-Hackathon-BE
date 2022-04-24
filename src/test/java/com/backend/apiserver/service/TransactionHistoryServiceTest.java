package com.backend.apiserver.service;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MoneyExchangeHistory;
import com.backend.apiserver.entity.MoneyInHistory;
import com.backend.apiserver.entity.MoneyOutHistory;
import com.backend.apiserver.entity.PaymentMethod;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.MoneyInHistoryRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.service.impl.TransactionHistoryServiceImpl;
import com.backend.apiserver.utils.JwtUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionHistoryServiceTest {
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private MoneyInHistoryRepository moneyInHistoryRepository;
    @Mock
    private MoneyOutHistoryRepository moneyOutHistoryRepository;
    @Mock
    private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;

    @InjectMocks
    TransactionHistoryService transactionHistoryService = new TransactionHistoryServiceImpl(
            jwtUtils,
            moneyInHistoryRepository,
            moneyOutHistoryRepository,
            moneyExchangeHistoryRepository
    );

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMoneyInHistories() {
        MoneyInHistory moneyInHistory = new MoneyInHistory();
        moneyInHistory.setId(1L);
        moneyInHistory.setStatus(Status.ACTIVE);
        moneyInHistory.setCreatedDate(LocalDateTime.now());
        moneyInHistory.setAmount(10);
        User user = new User();
        user.setUsername("Dung");
        moneyInHistory.setUser(user);
        moneyInHistory.setPaymentMethod(PaymentMethod.findById(1));
        Page<MoneyInHistory> moneyInHistories = new PageImpl(Arrays.asList(moneyInHistory));

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(moneyInHistoryRepository.findAllByUserId(1L, PageRequest.of(0, 10, Sort.by("id").descending()))).thenReturn(moneyInHistories);
        PagingWrapperResponse wrapperResponse = transactionHistoryService.getMoneyInHistories(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getMoneyOutHistories() {
        MoneyOutHistory moneyOutHistory = new MoneyOutHistory();
        moneyOutHistory.setId(1L);
        moneyOutHistory.setStatus(Status.ACTIVE);
        moneyOutHistory.setCreatedDate(LocalDateTime.now());
        moneyOutHistory.setAmount(10);
        User user = new User();
        user.setUsername("Dung");
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        moneyOutHistory.setMentor(mentor);
        moneyOutHistory.setPaymentMethod(PaymentMethod.findById(1));
        Page<MoneyOutHistory> moneyOutHistories = new PageImpl(Arrays.asList(moneyOutHistory));

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(moneyOutHistoryRepository.findAllByMentorId(1L, PageRequest.of(0, 10, Sort.by("id").descending()))).thenReturn(moneyOutHistories);
        PagingWrapperResponse wrapperResponse = transactionHistoryService.getMoneyOutHistories(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getMoneyExchangeHistories() {
        MoneyExchangeHistory moneyExchangeHistory = new MoneyExchangeHistory();
        moneyExchangeHistory.setId(1L);
        moneyExchangeHistory.setStatus(Status.ACTIVE);
        moneyExchangeHistory.setCreatedDate(LocalDateTime.now());
        moneyExchangeHistory.setAmount(10);
        User user = new User();
        user.setId(1L);
        user.setUsername("Dung");
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        moneyExchangeHistory.setMentor(mentor);
        moneyExchangeHistory.setUser(user);
        Page<MoneyExchangeHistory> moneyExchangeHistories = new PageImpl(Arrays.asList(moneyExchangeHistory));

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(moneyExchangeHistoryRepository.findAllByMentorIdOrUserId(1L, 1L, PageRequest.of(0, 10, Sort.by("id").descending()))).thenReturn(moneyExchangeHistories);
        PagingWrapperResponse wrapperResponse = transactionHistoryService.getMoneyExchangeHistories(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }
}