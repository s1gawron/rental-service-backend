package com.s1gawron.rentalservice.user.controller;

import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/public/user")
@AllArgsConstructor
public class UserController extends UserErrorHandlerController {

    private final UserService userService;

    @PostMapping("login")
    public void loginUser(@RequestBody final UserLoginDTO userLoginDTO) {

    }

    @PostMapping("register")
    public UserDTO registerUser(@RequestBody final UserRegisterDTO userRegisterDTO) {
        return userService.validateAndRegisterUser(userRegisterDTO);
    }

}
