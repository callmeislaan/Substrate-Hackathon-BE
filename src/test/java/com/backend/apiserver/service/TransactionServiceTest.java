package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.AnestCardRequest;
import com.backend.apiserver.bean.request.MoneyDetailRequest;
import com.backend.apiserver.bean.request.MoneyRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.entity.AnestCard;
import com.backend.apiserver.entity.BankCard;
import com.backend.apiserver.entity.EWallet;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MoneyExchangeHistory;
import com.backend.apiserver.entity.MoneyInHistory;
import com.backend.apiserver.entity.MoneyOutHistory;
import com.backend.apiserver.entity.PaymentMethod;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.AnestCardRepository;
import com.backend.apiserver.repository.BankCardRepository;
import com.backend.apiserver.repository.EWalletRepository;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.MoneyInHistoryRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.TransactionServiceImpl;
import com.backend.apiserver.utils.Constants;
import freemarker.template.TemplateException;
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

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailRepository userDetailRepository;
    @Mock
    private MoneyInHistoryRepository moneyInHistoryRepository;
    @Mock
    private MoneyOutHistoryRepository moneyOutHistoryRepository;
    @Mock
    private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;
    @Mock
    private BankCardRepository bankCardRepository;
    @Mock
    private EWalletRepository eWalletRepository;
    @Mock
    private AnestCardRepository anestCardRepository;
    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    TransactionService transactionService = new TransactionServiceImpl(
            userRepository,
            userDetailRepository,
            moneyInHistoryRepository,
            moneyOutHistoryRepository,
            moneyExchangeHistoryRepository,
            bankCardRepository,
            eWalletRepository,
            anestCardRepository,
            emailSenderService
    );

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createMoneyIn() throws MessagingException, TemplateException, NotFoundException, IOException {
        MoneyDetailRequest moneyDetailRequest = new MoneyDetailRequest();
        moneyDetailRequest.setUsername("account2");
        moneyDetailRequest.setAmount(20);
        moneyDetailRequest.setMethodId(1);
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setMoneyDetailRequests(Arrays.asList(moneyDetailRequest));

        User user = new User();
        user.setId(1L);
        user.setUsername("account2");
        UserDetail userDetail = new UserDetail();
        userDetail.setTotalBudgetCurrent(10);
        userDetail.setTotalBudgetIn(20);
        userDetail.setUser(user);
        user.setUserDetail(userDetail);

        MoneyInHistory moneyInHistory = new MoneyInHistory();
        moneyInHistory.setAmount(20);
        moneyInHistory.setStatus(Status.ACTIVE);
        moneyInHistory.setUser(userDetail.getUser());
        moneyInHistory.setPaymentMethod(PaymentMethod.findById(1));
        moneyInHistory.setCreatedDate(LocalDateTime.now());

        when(userRepository.findByUsernameAndStatus("account2", Status.ACTIVE)).thenReturn(user);
        when(moneyInHistoryRepository.saveAndFlush(moneyInHistory)).thenReturn(moneyInHistory);

        transactionService.createMoneyIn(moneyRequest);
    }

    @Test
    public void generateAnestCard() {
        AnestCardRequest anestCardRequest = new AnestCardRequest();
        anestCardRequest.setValue(1);
        anestCardRequest.setStatus(true);

        AnestCard anestCard = new AnestCard();
        anestCard.setId(1L);
        anestCard.setStatus(Status.ACTIVE);
        anestCard.setCode(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        anestCard.setValue(1);

        when(anestCardRepository.saveAndFlush(any())).thenReturn(anestCard);

        transactionService.generateAnestCard(anestCardRequest);
    }

    @Test
    public void updateAnestCard() throws NotFoundException {
        AnestCardRequest anestCardRequest = new AnestCardRequest();
        anestCardRequest.setValue(1);
        anestCardRequest.setStatus(true);

        AnestCard anestCard = new AnestCard();
        anestCard.setId(1L);
        anestCard.setStatus(Status.ACTIVE);
        anestCard.setCode(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        anestCard.setValue(10);

        when(anestCardRepository.findByIdAndStatusNot(1L, Status.USED)).thenReturn(anestCard);
        when(anestCardRepository.saveAndFlush(anestCard)).thenReturn(anestCard);

        transactionService.updateAnestCard(1L, anestCardRequest);
    }

    @Test
    public void findAnestCards() {
        AnestCard anestCard = new AnestCard();
        anestCard.setId(1L);
        anestCard.setStatus(Status.ACTIVE);
        anestCard.setCode(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        anestCard.setValue(10);
        Page<AnestCard> anestCards = new PageImpl(Arrays.asList(anestCard));
        when(anestCardRepository.findAll(PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(anestCards);
        PagingWrapperResponse wrapperResponse = transactionService.findAnestCards(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void disableAnestCard() throws NotFoundException {
        AnestCard anestCard = new AnestCard();
        anestCard.setId(1L);
        anestCard.setStatus(Status.ACTIVE);
        anestCard.setCode(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        anestCard.setValue(10);

        when(anestCardRepository.findByIdAndStatusNot(1L, Status.USED)).thenReturn(anestCard);

        transactionService.disableAnestCard(1L);
    }

    @Test
    public void getMoneyInTransactions() {
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
        when(moneyInHistoryRepository.findAll(PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(moneyInHistories);
        PagingWrapperResponse wrapperResponse = transactionService.getMoneyInTransactions(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getMoneyOutTransactions() {
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
        when(moneyOutHistoryRepository.findAll(PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(moneyOutHistories);
        PagingWrapperResponse wrapperResponse = transactionService.getMoneyOutTransactions(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getMoneyExchangeTransactions() {
        MoneyExchangeHistory moneyExchangeHistory = new MoneyExchangeHistory();
        moneyExchangeHistory.setId(1L);
        moneyExchangeHistory.setStatus(Status.ACTIVE);
        moneyExchangeHistory.setCreatedDate(LocalDateTime.now());
        moneyExchangeHistory.setAmount(10);
        User user = new User();
        user.setUsername("Dung");
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        moneyExchangeHistory.setMentor(mentor);
        moneyExchangeHistory.setUser(user);
        Page<MoneyExchangeHistory> moneyExchangeHistories = new PageImpl(Arrays.asList(moneyExchangeHistory));
        when(moneyExchangeHistoryRepository.findAll(PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(moneyExchangeHistories);
        PagingWrapperResponse wrapperResponse = transactionService.getMoneyExchangeTransactions(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getPaymentMethodInfo_BankCard() throws NotFoundException {
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
        moneyOutHistory.setPaymentMethod(PaymentMethod.findById(5));
        Optional<MoneyOutHistory> moneyOutHistoryOptional = Optional.of(moneyOutHistory);

        BankCard bankCard = new BankCard();
        bankCard.setId(1L);
        bankCard.setStatus(Status.ACTIVE);
        bankCard.setAccountNumber("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        bankCard.setBank("TP Bank");
        Optional<BankCard> bankCardOptional = Optional.of(bankCard);

        when(moneyOutHistoryRepository.findById(1L)).thenReturn(moneyOutHistoryOptional);

        transactionService.getPaymentMethodInfo(1L);
    }

    @Test
    public void getPaymentMethodInfo_Ewallet() throws NotFoundException {
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
        moneyOutHistory.setPaymentMethod(PaymentMethod.findById(3));
        Optional<MoneyOutHistory> moneyOutHistoryOptional = Optional.of(moneyOutHistory);

        EWallet eWallet = new EWallet();
        eWallet.setId(1L);
        eWallet.setStatus(Status.ACTIVE);
        eWallet.setPhone("38400000");
        eWallet.setEWalletName("Momo");
        Optional<EWallet> eWalletOptional = Optional.of(eWallet);

        when(moneyOutHistoryRepository.findById(1L)).thenReturn(moneyOutHistoryOptional);

        transactionService.getPaymentMethodInfo(1L);
    }

    @Test
    public void changeMoneyOutStatus_BankCard() throws MessagingException, TemplateException, NotFoundException, IOException {
        MoneyOutHistory moneyOutHistory = new MoneyOutHistory();
        moneyOutHistory.setId(1L);
        moneyOutHistory.setStatus(Status.PENDING);
        moneyOutHistory.setCreatedDate(LocalDateTime.now());
        moneyOutHistory.setAmount(10);
        User user = new User();
        user.setId(1L);
        user.setUsername("Dung");
        UserDetail userDetail = new UserDetail();
        userDetail.setFullName("Dung");
        user.setUserDetail(userDetail);
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        user.setMentor(mentor);
        moneyOutHistory.setMentor(mentor);
        moneyOutHistory.setPaymentMethod(PaymentMethod.findById(5));
        moneyOutHistory.setPaymentMethodId(1L);

        BankCard bankCard = new BankCard();
        bankCard.setId(1L);
        bankCard.setStatus(Status.ACTIVE);
        bankCard.setAccountNumber("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        bankCard.setBank("TP Bank");
        bankCard.setHolderName("Đỗ Mạnh Dũng");

        when(moneyOutHistoryRepository.findByIdAndStatus(1L, Status.PENDING)).thenReturn(moneyOutHistory);
        when(bankCardRepository.findByIdAndMentorIdAndStatus(1L, 1L, Status.ACTIVE)).thenReturn(bankCard);

        transactionService.changeMoneyOutStatus(1L);
    }

    @Test
    public void changeMoneyOutStatus_Ewallet() throws MessagingException, TemplateException, NotFoundException, IOException {
        MoneyOutHistory moneyOutHistory = new MoneyOutHistory();
        moneyOutHistory.setId(1L);
        moneyOutHistory.setStatus(Status.ACTIVE);
        moneyOutHistory.setCreatedDate(LocalDateTime.now());
        moneyOutHistory.setAmount(10);
        User user = new User();
        user.setId(1L);
        user.setUsername("Dung");
        UserDetail userDetail = new UserDetail();
        userDetail.setFullName("Dung");
        user.setUserDetail(userDetail);
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        user.setMentor(mentor);
        moneyOutHistory.setMentor(mentor);
        moneyOutHistory.setPaymentMethod(PaymentMethod.findById(3));
        moneyOutHistory.setPaymentMethodId(1L);

        EWallet eWallet = new EWallet();
        eWallet.setId(1L);
        eWallet.setStatus(Status.ACTIVE);
        eWallet.setPhone("38400000");
        eWallet.setEWalletName("Momo");
        eWallet.setHolderName("Đỗ Mạnh Dũng");

        when(moneyOutHistoryRepository.findByIdAndStatus(1L, Status.PENDING)).thenReturn(moneyOutHistory);
        when(eWalletRepository.findByIdAndMentorIdAndStatus(1L, 1L, Status.ACTIVE)).thenReturn(eWallet);

        transactionService.changeMoneyOutStatus(1L);
    }
}