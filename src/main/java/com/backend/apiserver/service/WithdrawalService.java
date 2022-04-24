package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.WithdrawRequest;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface WithdrawalService {
    /**
     * Create request withdraw for user
     *
     * @param eWalletId
     * @param withdrawRequest
     */
    void withdrawWithEWallet(Long eWalletId, WithdrawRequest withdrawRequest) throws NotFoundException, MoneyRelatedException, MessagingException, IOException, TemplateException;

    /**
     * Create request withdraw for user
     *
     * @param bankCardId
     * @param withdrawRequest
     */
    void withdrawWithBankCard(Long bankCardId, WithdrawRequest withdrawRequest) throws NotFoundException, MoneyRelatedException, MessagingException, IOException, TemplateException;
}
