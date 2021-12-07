package com.epam.esm.repository;

import com.epam.esm.entity.UsersOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersOrderRepository extends PagingAndSortingRepository<UsersOrder, Integer> {

    Page<UsersOrder> findAllByUserId(Integer id, Pageable pageable);

    Optional<UsersOrder> findUsersOrderByUserIdAndOrderId(Integer userId, Integer orderId);
}
