package com.example.oauthjwt.redis.repository;

import com.example.oauthjwt.redis.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findById(String userKey);

    Boolean existsByAccessToken(String accessToken);
}
