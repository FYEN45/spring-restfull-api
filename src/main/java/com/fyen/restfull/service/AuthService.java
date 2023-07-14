package com.fyen.restfull.service;

import com.fyen.restfull.entity.User;
import com.fyen.restfull.model.LoginUserRequest;
import com.fyen.restfull.model.TokenResponse;
import com.fyen.restfull.model.WebResponse;
import com.fyen.restfull.repository.UserRepository;
import com.fyen.restfull.security.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

  @Autowired private UserRepository userRepository;

  @Autowired private ValidationService validationService;

  @Transactional
  public TokenResponse login(LoginUserRequest request) {
    validationService.validate(request);

    User user =
        userRepository
            .findById(request.getUsername())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Username or Password wrong"));

    if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(next30Days());
      userRepository.save(user);

      return TokenResponse.builder()
          .token(user.getToken())
          .expiredAt(user.getTokenExpiredAt())
          .build();
    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong");
    }
  }

  private Long next30Days() {
    return System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
  }

  @Transactional
  public void logout(User user) {
    user.setToken(null);
    user.setTokenExpiredAt(null);

    userRepository.save(user);
  }
}
