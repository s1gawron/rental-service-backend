package com.s1gawron.rentalservice.user.model;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@DynamicUpdate
@NoArgsConstructor
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
    @Column(name = "user_type")
    private UserType userType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_address_id", referencedColumnName = "address_id")
    private Address customerAddress;

    @OneToMany(mappedBy = "customer")
    private List<Reservation> customerReservations;

    private User(final boolean active, final String email, final String password, final String firstName, final String lastName, final UserType userType,
        final Address customerAddress) {
        this.active = active;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.customerAddress = customerAddress;
    }

    private User(final boolean active, final String email, final String password, final String firstName, final String lastName, final UserType userType) {
        this.active = active;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }

    public static User createUser(final UserRegisterDTO userRegisterDTO, final String encryptedPassword) {
        final AddressDTO customerAddress = userRegisterDTO.getAddress();

        if (userRegisterDTO.getUserType() == UserType.CUSTOMER) {
            return new User(true, userRegisterDTO.getEmail(), encryptedPassword, userRegisterDTO.getFirstName(), userRegisterDTO.getLastName(),
                userRegisterDTO.getUserType(), Address.from(customerAddress));
        }

        return new User(true, userRegisterDTO.getEmail(), encryptedPassword, userRegisterDTO.getFirstName(), userRegisterDTO.getLastName(),
            userRegisterDTO.getUserType());
    }

    public UserDTO toUserDTO() {
        if (this.userType == UserType.CUSTOMER) {
            final AddressDTO userAddress = this.customerAddress.toAddressDTO();
            return new UserDTO(this.firstName, this.lastName, this.email, userAddress);
        }

        return new UserDTO(this.firstName, this.lastName, this.email, null);
    }
}
