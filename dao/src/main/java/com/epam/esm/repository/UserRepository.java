package com.epam.esm.repository;

import com.epam.esm.entity.UserProfile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserProfile, Integer> {

    Optional<UserProfile> findUserProfileByLogin(String login);
}
