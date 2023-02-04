package com.s1gawron.rentalservice.user.model;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@DynamicUpdate
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "active")
    private boolean active;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @OneToOne(cascade = CascadeType.ALL)
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

    public static User createUser(final UserRegisterRequest userRegisterRequest, final UserRole userRole, final String encryptedPassword) {
        return new User(true, userRegisterRequest.email(), encryptedPassword, userRegisterRequest.firstName(), userRegisterRequest.lastName(), userRole);
    }

    public UserDTO toUserDTO() {
        if (this.userRole == UserRole.CUSTOMER) {
            final AddressDTO userAddress = this.customerAddress.toAddressDTO();
            return new UserDTO(this.firstName, this.lastName, this.email, this.userRole.name(), userAddress);
        }

        return new UserDTO(this.firstName, this.lastName, this.email, this.userRole.name(), null);
    }

    public boolean isWorker() {
        return this.userRole == UserRole.WORKER;
    }

    public boolean isCustomer() {
        return this.userRole == UserRole.CUSTOMER;
    }

    public void setCustomerAddress(final Address customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void addReservation(final Reservation reservation) {
        if (this.customerReservations == null) {
            this.customerReservations = new ArrayList<>();
        }

        this.customerReservations.add(reservation);
    }

    public void doesReservationBelongToUser(final Long reservationId) {
        if (isWorker()) {
            throw NoAccessForUserRoleException.create("CUSTOMER RESERVATIONS");
        }

        final long doesReservationBelongToUser = this.customerReservations.stream()
            .filter(reservation -> reservation.getReservationId().equals(reservationId))
            .count();

        if (doesReservationBelongToUser == 0) {
            throw ReservationNotFoundException.create(reservationId);
        }
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

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() {
        return true;
    }

    @Override public boolean isAccountNonLocked() {
        return true;
    }

    @Override public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override public boolean isEnabled() {
        return active;
    }

}
