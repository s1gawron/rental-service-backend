package com.s1gawron.rentalservice.shared.helper;

import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;

public enum UserCreatorHelper {

    I;

    private static final String ENCODED_PASSWORD = "$2a$10$PCo2iC74Ge/tI05PfPS9DOPQUjwdDtrheGwGEkuyB8myslYC2kKuK";

    public User createCustomer() {
        final Address address = new Address("Poland", "Warsaw", "Test", "01-000");
        return new User(true, "customer@test.pl", ENCODED_PASSWORD, "John", "Kowalski", UserRole.CUSTOMER, address);
    }

    public User createWorker() {
        final Address address = new Address("Poland", "Warsaw", "Test", "01-000");
        return new User(true, "worker@test.pl", ENCODED_PASSWORD, "John", "Kowalski", UserRole.WORKER, address);
    }

    public User createDifferentCustomer() {
        final Address address = new Address("Poland", "Warsaw", "Przedmiescia", "01-100");
        return new User(true, "customer2@test.pl", ENCODED_PASSWORD, "George", "Happs", UserRole.CUSTOMER, address);
    }
}
