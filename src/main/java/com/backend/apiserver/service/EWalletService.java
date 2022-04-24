package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.EWalletRequest;
import com.backend.apiserver.bean.response.EWalletResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.NotFoundException;

public interface EWalletService {

    /**
     * Get all e-wallets of mentor
     *
     * @return ResponseWrapper
     */
    WrapperResponse getEWallets();

    /**
     * Create new e-wallet information
     *
     * @param eWalletRequest
     * @return
     */
    EWalletResponse createEWallet(EWalletRequest eWalletRequest);

    /**
     * Delete e-wallet with given id
     *
     * @param id
     * @throws NotFoundException
     */
    void deleteEWallet(Long id) throws NotFoundException;

    /**
     * Update e-wallet information
     *
     * @param id
     * @param eWalletRequest
     * @throws NotFoundException
     */
    void updateEWallet(Long id, EWalletRequest eWalletRequest) throws NotFoundException;

    /**
     * Find e-wallet by given id
     *
     * @param id
     * @return
     * @throws NotFoundException
     */
    EWalletResponse findEWallet(Long id) throws NotFoundException;
}
