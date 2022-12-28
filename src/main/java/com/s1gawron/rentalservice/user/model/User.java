package com.s1gawron.rentalservice.user.model;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@DynamicUpdate
@NoArgsConstructor
@Getter
public class User {

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

    private User(final boolean active, final String email, final String password, final String firstName, final String lastName, final UserRole userRole) {
        this.active = active;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
    }

    public static User createUser(final UserRegisterDTO userRegisterDTO, final UserRole userRole, final String encryptedPassword) {
        return new User(true, userRegisterDTO.getEmail(), encryptedPassword, userRegisterDTO.getFirstName(), userRegisterDTO.getLastName(), userRole);
    }

    public UserDTO toUserDTO() {
        if (this.userRole == UserRole.CUSTOMER) {
            final AddressDTO userAddress = this.customerAddress.toAddressDTO();
            return new UserDTO(this.firstName, this.lastName, this.email, this.userRole.getName(), userAddress);
        }

        return new UserDTO(this.firstName, this.lastName, this.email, this.userRole.getName(), null);
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
}
