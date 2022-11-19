package com.s1gawron.rentalservice.address.model;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "address")
@DynamicUpdate
@NoArgsConstructor
@Getter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "post_code")
    private String postCode;

    private Address(final String country, final String city, final String street, final String postCode) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.postCode = postCode;
    }

    public static Address from(final AddressDTO addressDTO) {
        return new Address(addressDTO.getCountry(), addressDTO.getCity(), addressDTO.getStreet(), addressDTO.getPostCode());
    }

    public AddressDTO toAddressDTO() {
        return new AddressDTO(this.country, this.city, this.street, this.postCode);
    }
}
