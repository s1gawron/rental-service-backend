package com.s1gawron.rentalservice.user.controller;

import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserLoginRequest;
import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import com.s1gawron.rentalservice.user.service.AuthenticationService;
import com.s1gawron.rentalservice.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/public/user")
public class UserController extends UserErrorHandlerController {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    public UserController(final UserService userService, final AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public AuthenticationResponse loginUser(@RequestBody final UserLoginRequest userLoginRequest) {
        return authenticationService.loginUser(userLoginRequest);
    }

    @PostMapping("register")
    public UserDTO registerUser(@RequestBody final UserRegisterRequest userRegisterRequest) {
        return userService.validateAndRegisterUser(userRegisterRequest);
    }

}
