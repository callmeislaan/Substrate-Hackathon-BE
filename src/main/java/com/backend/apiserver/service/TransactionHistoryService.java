package com.backend.apiserver.service;

import com.backend.apiserver.bean.response.PagingWrapperResponse;

public interface TransactionHistoryService {

    /**
     * Get list money in history
     *
     * @return ResponseWrapper
     */
    PagingWrapperResponse getMoneyInHistories(Integer page, Integer size);

    /**
     * Get list money out history
     *
     * @return ResponseWrapper
     */
    PagingWrapperResponse getMoneyOutHistories(Integer page, Integer size);

    /**
     * Get list money exchange history
     *
     * @return ResponseWrapper
     */
    PagingWrapperResponse getMoneyExchangeHistories(Integer page, Integer size);
}
