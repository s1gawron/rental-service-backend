package com.s1gawron.rentalservice.shared.usercontext;

import com.s1gawron.rentalservice.shared.exception.UserUnauthenticatedException;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum UserContextProvider {

    I;

    public User getLoggedInUser() {
        final Optional<Object> principal = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return principal.map(p -> (User) p).orElseThrow(UserUnauthenticatedException::create);
    }

    public Set<UserRole> getCurrentUserRoles() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities().isEmpty()) {
            return Set.of(UserRole.ANONYMOUS);
        }

        return authentication.getAuthorities().stream()
            .map(grantedAuthority -> UserRole.getUserRole(grantedAuthority.getAuthority()))
            .collect(Collectors.toSet());
    }

    public boolean hasUserRole(final UserRole userRole) {
        return !getCurrentUserRoles().stream().filter(userRole::equals).toList().isEmpty();
    }

    public void setLoggedInUser(final User user) {
        final TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, null, String.valueOf(user.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
    }

    public void clearLoggedInUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
