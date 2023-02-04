package com.s1gawron.rentalservice.address.model;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

@Entity
@Table(name = "address")
@DynamicUpdate
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

    public Address() {
    }

    private Address(final String country, final String city, final String street, final String postCode) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.postCode = postCode;
    }

    public static Address from(final AddressDTO addressDTO) {
        return new Address(addressDTO.country(), addressDTO.city(), addressDTO.street(), addressDTO.postCode());
    }

    public AddressDTO toAddressDTO() {
        return new AddressDTO(this.country, this.city, this.street, this.postCode);
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getPostCode() {
        return postCode;
    }
}
