package com.marketplace.user.infrastructure;

import com.marketplace.user.domain.User;
import com.marketplace.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
