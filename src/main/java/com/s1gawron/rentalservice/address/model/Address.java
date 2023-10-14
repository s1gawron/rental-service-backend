package com.s1gawron.rentalservice.address.model;

import com.s1gawron.rentalservice.address.dto.AddressDTO;

import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false, unique = true)
    private Long addressId;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "post_code", nullable = false)
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
