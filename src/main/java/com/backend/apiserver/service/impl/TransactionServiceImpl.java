package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.AnestCardRequest;
import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.bean.request.MoneyDetailRequest;
import com.backend.apiserver.bean.request.MoneyRequest;
import com.backend.apiserver.bean.response.AnestCardResponse;
import com.backend.apiserver.bean.response.MoneyExchangeResponse;
import com.backend.apiserver.bean.response.MoneyFlowResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.PaymentInfoResponse;
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
import com.backend.apiserver.mapper.MoneyFlowMapper;
import com.backend.apiserver.repository.AnestCardRepository;
import com.backend.apiserver.repository.BankCardRepository;
import com.backend.apiserver.repository.EWalletRepository;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.MoneyInHistoryRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.EmailSenderService;
import com.backend.apiserver.service.TransactionService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.FormatUtils;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    /**
     * UserDetailRepository
     */
    private UserRepository userRepository;

    /**
     * UserDetailRepository
     */
    private UserDetailRepository userDetailRepository;

    /**
     * MoneyInHistoryRepository
     */
    private MoneyInHistoryRepository moneyInHistoryRepository;

    private MoneyOutHistoryRepository moneyOutHistoryRepository;

    private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;

    private BankCardRepository bankCardRepository;

    private EWalletRepository eWalletRepository;

    private AnestCardRepository anestCardRepository;

    private EmailSenderService emailSenderService;

    /**
     * {@inheritDoc}
     */
    @Override
    @AnestTransactional
    public MoneyFlowResponse createMoneyIn(MoneyRequest moneyRequest) throws NotFoundException, MessagingException, IOException, TemplateException {
        MoneyDetailRequest firstItem = moneyRequest.getMoneyDetailRequests().get(0);
        User user = userRepository.findByUsernameAndStatus(firstItem.getUsername(), Status.ACTIVE);
        if (Objects.isNull(user)) throw new NotFoundException("User does not exists");
        UserDetail userDetail = user.getUserDetail();
        userDetail.setTotalBudgetCurrent(userDetail.getTotalBudgetCurrent() + firstItem.getAmount());
        userDetail.setTotalBudgetIn(userDetail.getTotalBudgetIn() + firstItem.getAmount());
        userDetailRepository.saveAndFlush(userDetail);
        MoneyInHistory moneyInHistory = new MoneyInHistory();
        moneyInHistory.setAmount(firstItem.getAmount());
        moneyInHistory.setStatus(Status.ACTIVE);
        moneyInHistory.setUser(userDetail.getUser());
        moneyInHistory.setPaymentMethod(PaymentMethod.findById(firstItem.getMethodId()));

        Map<String, Object> params = new HashMap<>();
        params.put("amount", firstItem.getAmount());
        params.put("fullName", userDetail.getFullName());

        MailRequest mailRequest = new MailRequest(
                FormatUtils.makeArray(user.getEmail()),
                params,
                MailRequest.TEMPLATE_MONEY_IN,
                MailRequest.TITLE_MONEY_IN
        );

        emailSenderService.sendEmailTemplate(mailRequest);

        return MoneyFlowMapper.moneyInToResponse(
                moneyInHistoryRepository.saveAndFlush(moneyInHistory),
                userDetail.getUser().getUsername()
        );
    }

    @Override
    @AnestTransactional
    public AnestCardResponse generateAnestCard(AnestCardRequest anestCardRequest) {
        AnestCard anestCard = new AnestCard();
        anestCard.setValue(anestCardRequest.getValue());
        anestCard.setStatus(Status.ACTIVE);
        return convertToResponse(anestCardRepository.saveAndFlush(anestCard));
    }

    @Override
    @AnestTransactional
    public AnestCardResponse updateAnestCard(Long id, AnestCardRequest anestCardRequest) throws NotFoundException {
        AnestCard anestCard = anestCardRepository.findByIdAndStatusNot(id, Status.USED);
        if (Objects.isNull(anestCard)) {
            throw new NotFoundException("Not found anest card with this id");
        }
        anestCard.setValue(anestCardRequest.getValue());
        anestCard.setStatus(anestCardRequest.isStatus() ? Status.ACTIVE : Status.DELETE);
        return convertToResponse(anestCardRepository.saveAndFlush(anestCard));
    }

    @Override
    public PagingWrapperResponse findAnestCards(Integer page, Integer size) {
        Page<AnestCard> anestCards = anestCardRepository.findAll(PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
        return new PagingWrapperResponse(anestCards.get().map(this::convertToResponse).collect(Collectors.toList()), anestCards.getTotalElements());
    }

    @Override
    @AnestTransactional
    public void disableAnestCard(Long id) throws NotFoundException {
        AnestCard anestCard = anestCardRepository.findByIdAndStatusNot(id, Status.USED);
        if (Objects.isNull(anestCard)) throw new NotFoundException("Top up not found or is disabled by another mentor");
        anestCard.setStatus(Status.DELETE);
        anestCardRepository.saveAndFlush(anestCard);
    }

    @Override
    public PagingWrapperResponse getMoneyInTransactions(Integer page, Integer size) {
        Page<MoneyInHistory> moneyInHistories = moneyInHistoryRepository.findAll(PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
        return new PagingWrapperResponse(
                moneyInHistories.stream()
                        .map(moneyInHistory -> MoneyFlowMapper.moneyInToResponse(
                                moneyInHistory,
                                moneyInHistory.getUser().getUsername()
                                )
                        )
                        .collect(Collectors.toList()),
                moneyInHistories.getTotalElements()
        );
    }

    @Override
    public PagingWrapperResponse getMoneyOutTransactions(Integer page, Integer size) {
        Page<MoneyOutHistory> moneyOutHistories = moneyOutHistoryRepository.findAll(PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
        return new PagingWrapperResponse(
                moneyOutHistories.stream()
                        .map(moneyOutHistory -> moneyOutToResponse(
                                moneyOutHistory,
                                moneyOutHistory.getMentor().getUser().getUsername()
                                )
                        ).collect(Collectors.toList()),
                moneyOutHistories.getTotalElements()
        );
    }

    @Override
    public PagingWrapperResponse getMoneyExchangeTransactions(Integer page, Integer size) {
        Page<MoneyExchangeHistory> moneyExchangeHistories = moneyExchangeHistoryRepository.findAll(PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
        return new PagingWrapperResponse(
                moneyExchangeHistories.stream()
                        .map(moneyExchangeHistory -> convertExchangeToResponse(
                                moneyExchangeHistory,
                                moneyExchangeHistory.getMentor().getUser().getUsername(),
                                moneyExchangeHistory.getUser().getUsername()
                                )
                        ).collect(Collectors.toList()),
                moneyExchangeHistories.getTotalElements()
        );
    }

    @Override
    public PaymentInfoResponse getPaymentMethodInfo(Long transactionId) throws NotFoundException {
        Optional<MoneyOutHistory> moneyOutHistoryOptional = moneyOutHistoryRepository.findById(transactionId);
        if (!moneyOutHistoryOptional.isPresent()) throw new NotFoundException("not found with current id");
        PaymentInfoResponse paymentInfoResponse = new PaymentInfoResponse();
        MoneyOutHistory moneyOutHistory = moneyOutHistoryOptional.get();
        if (moneyOutHistory.getPaymentMethod().equals(PaymentMethod.BANK_CARD)) {
            Optional<BankCard> bankCardOptional = bankCardRepository.findById(moneyOutHistory.getPaymentMethodId());
            if (bankCardOptional.isPresent()) {
                paymentInfoResponse.setAccountNumber(bankCardOptional.get().getAccountNumber());
                paymentInfoResponse.setBank(bankCardOptional.get().getBank());
                paymentInfoResponse.setBranch(bankCardOptional.get().getBranch());
                paymentInfoResponse.setHolderName(bankCardOptional.get().getHolderName());
                paymentInfoResponse.setEWallet(false);
            }
        } else {
            Optional<EWallet> eWalletOptional = eWalletRepository.findById(moneyOutHistory.getPaymentMethodId());
            if (eWalletOptional.isPresent()) {
                paymentInfoResponse.setEWalletName(eWalletOptional.get().getEWalletName());
                paymentInfoResponse.setHolderName(eWalletOptional.get().getHolderName());
                paymentInfoResponse.setPhone(eWalletOptional.get().getPhone());
                paymentInfoResponse.setEWallet(true);
            }
        }
        return paymentInfoResponse;
    }

    @Override
    @AnestTransactional
    public void changeMoneyOutStatus(Long transactionId) throws NotFoundException, MessagingException, IOException, TemplateException {
        MoneyOutHistory moneyOutHistory = moneyOutHistoryRepository.findByIdAndStatus(transactionId, Status.PENDING);
        if (Objects.isNull(moneyOutHistory)) {
            throw new NotFoundException("Not found data of money out history");
        }
        moneyOutHistory.setStatus(Status.ACTIVE);
        moneyOutHistoryRepository.saveAndFlush(moneyOutHistory);

        Mentor mentor = moneyOutHistory.getMentor();
        User user = mentor.getUser();
        UserDetail userDetail = user.getUserDetail();

        //send mail
        Map<String, Object> params = new HashMap<>();
        params.put("fullName", userDetail.getFullName());
        params.put("status", Constants.SUCCESS_STATUS);
        params.put("announcement", Constants.SUCCESS_ANNOUNCEMENT);
        params.put("pendingMessage", "");
        params.put("amount", moneyOutHistory.getAmount());
        params.put("color", Constants.SUCCESS_COLOR);
        MailRequest mailRequest;

        if (moneyOutHistory.getPaymentMethod().equals(PaymentMethod.BANK_CARD)) {

            BankCard bankCard = bankCardRepository.findByIdAndMentorIdAndStatus(moneyOutHistory.getPaymentMethodId(), user.getId(), Status.ACTIVE);

            params.put("holderName", bankCard.getHolderName());
            params.put("accountNumber", bankCard.getAccountNumber());
            params.put("bank", bankCard.getBank());
            params.put("branch", bankCard.getBranch());

            //send mail user
            mailRequest = new MailRequest(
                    FormatUtils.makeArray(user.getEmail()),
                    params,
                    MailRequest.TEMPLATE_BANK_CARD,
                    MailRequest.TITLE_MONEY_OUT
            );
        } else {

            EWallet eWallet = eWalletRepository.findByIdAndMentorIdAndStatus(moneyOutHistory.getPaymentMethodId(), user.getId(), Status.ACTIVE);

            params.put("holderName", eWallet.getHolderName());
            params.put("eWallet", eWallet.getEWalletName());
            params.put("phone", eWallet.getPhone());

            //send mail user
            mailRequest = new MailRequest(
                    FormatUtils.makeArray(user.getEmail()),
                    params,
                    MailRequest.TEMPLATE_E_WALLET,
                    MailRequest.TITLE_MONEY_OUT
            );
        }

        emailSenderService.sendEmailTemplate(mailRequest);
    }

    private AnestCardResponse convertToResponse(AnestCard anestCard) {
        AnestCardResponse anestCardResponse = new AnestCardResponse();
        anestCardResponse.setSerial(Constants.ANEST + anestCard.getId());
        anestCardResponse.setValue(anestCard.getValue());
        anestCardResponse.setCode(anestCard.getCode().toString());
        anestCardResponse.setStatus(anestCard.getStatus().toString());
        return anestCardResponse;
    }

    private MoneyFlowResponse moneyOutToResponse(MoneyOutHistory moneyOutHistory, String username) {
        MoneyFlowResponse response = new MoneyFlowResponse();
        response.setAmount(moneyOutHistory.getAmount());
        response.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(moneyOutHistory.getCreatedDate()));
        response.setPaymentMethod(moneyOutHistory.getPaymentMethod().toString());
        response.setAmount(moneyOutHistory.getAmount());
        response.setTransactionId(moneyOutHistory.getId());
        response.setStatus(moneyOutHistory.getStatus().toString());
        response.setUsername(username);
        return response;
    }

    private MoneyExchangeResponse convertExchangeToResponse(MoneyExchangeHistory moneyExchangeHistory, String sender, String receiver) {
        MoneyExchangeResponse moneyExchangeResponse = new MoneyExchangeResponse();
        moneyExchangeResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(moneyExchangeHistory.getCreatedDate()));
        moneyExchangeResponse.setAmount(moneyExchangeHistory.getAmount());
        moneyExchangeResponse.setTransactionId(moneyExchangeHistory.getId());
        moneyExchangeResponse.setSender(sender);
        moneyExchangeResponse.setReceiver(receiver);
        moneyExchangeResponse.setStatus(moneyExchangeHistory.getStatus().toString());
        return moneyExchangeResponse;
    }
}
