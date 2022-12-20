package com.s1gawron.rentalservice.user.controller;

import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class UserManagementController extends UserErrorHandlerController {

    private final UserService userService;

    @GetMapping("details")
    public UserDTO getUserDetails() {
        return userService.getUserDetails();
    }

}
