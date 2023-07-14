package com.fyen.restfull.controller;

import com.fyen.restfull.entity.User;
import com.fyen.restfull.model.RegisterUserRequest;
import com.fyen.restfull.model.UpdateUserRequest;
import com.fyen.restfull.model.UserResponse;
import com.fyen.restfull.model.WebResponse;
import com.fyen.restfull.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
  @Autowired private UserService userService;

  @PostMapping(
      path = "/api/users",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
    userService.register(request);
    return WebResponse.<String>builder().data("OK").build();
  }

  @GetMapping(path = "/api/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> get(User user) {
    UserResponse userResponse = userService.get(user);
    return WebResponse.<UserResponse>builder().data(userResponse).build();
  }

  @PatchMapping(
      path = "/api/users/current",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request) {
    UserResponse userResponse = userService.update(user, request);
    return WebResponse.<UserResponse>builder().data(userResponse).build();
  }
}
