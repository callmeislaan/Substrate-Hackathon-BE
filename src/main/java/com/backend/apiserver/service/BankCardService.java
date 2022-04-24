package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.BankCardRequest;
import com.backend.apiserver.bean.response.BankCardResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.NotFoundException;

public interface BankCardService {

    /**
     * Get list bank cards of a mentor
     *
     * @return ResponseWrapper
     */
    WrapperResponse getBankCards();

    /**
     * Create new bank card
     *
     * @param bankCardRequest
     * @return
     */
    BankCardResponse createBankCard(BankCardRequest bankCardRequest);

    /**
     * Delete bank card with given id
     *
     * @param id
     * @throws NotFoundException
     */
    void deleteBankCard(Long id) throws NotFoundException;

    /**
     * Update bank card information
     *
     * @param id
     * @param bankCardRequest
     * @throws NotFoundException
     */
    void updateBankCard(Long id, BankCardRequest bankCardRequest) throws NotFoundException;

    /**
     * Find bank card by given id
     *
     * @param id
     * @return
     * @throws NotFoundException
     */
    BankCardResponse findBankCard(Long id) throws NotFoundException;
}
