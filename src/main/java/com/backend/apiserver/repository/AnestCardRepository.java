package com.backend.apiserver.repository;

import com.backend.apiserver.entity.AnestCard;
import com.backend.apiserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnestCardRepository extends JpaRepository<AnestCard, Long>, PagingAndSortingRepository<AnestCard, Long> {

	AnestCard findByIdAndStatusNot(Long id, Status status);
}
