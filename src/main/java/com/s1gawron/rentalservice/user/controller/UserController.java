package com.s1gawron.rentalservice.user.controller;

import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
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
    public AuthenticationResponse loginUser(@RequestBody final UserLoginDTO userLoginDTO) {
        return authenticationService.loginUser(userLoginDTO);
    }

    @PostMapping("register")
    public UserDTO registerUser(@RequestBody final UserRegisterDTO userRegisterDTO) {
        return userService.validateAndRegisterUser(userRegisterDTO);
    }

}
