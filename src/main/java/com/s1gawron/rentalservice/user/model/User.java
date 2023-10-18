package com.s1gawron.rentalservice.user.model;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @OneToOne
    @JoinColumn(name = "customer_address_id", referencedColumnName = "address_id")
    private Address customerAddress;

    @OneToMany(mappedBy = "customer")
    private List<Reservation> customerReservations;

    public User() {
    }

    private User(final boolean active, final String email, final String password, final String firstName, final String lastName, final UserRole userRole) {
        this.active = active;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
    }

    public User(final boolean active, final String email, final String password, final String firstName, final String lastName, final UserRole userRole,
        final Address customerAddress) {
        this.active = active;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
        this.customerAddress = customerAddress;
    }

    public static User createFrom(final UserRegisterDTO userRegisterDTO, final String encryptedPassword) {
        return new User(true, userRegisterDTO.email(), encryptedPassword, userRegisterDTO.firstName(), userRegisterDTO.lastName(), userRegisterDTO.userRole());
    }

    public UserDTO toUserDTO() {
        if (this.userRole == UserRole.CUSTOMER) {
            final AddressDTO userAddress = this.customerAddress.toAddressDTO();
            return new UserDTO(this.firstName, this.lastName, this.email, this.userRole.name(), userAddress);
        }

        return new UserDTO(this.firstName, this.lastName, this.email, this.userRole.name(), null);
    }

    public void setCustomerAddress(final Address customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public Address getCustomerAddress() {
        return customerAddress;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

}
