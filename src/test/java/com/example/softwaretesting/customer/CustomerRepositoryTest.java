package com.example.softwaretesting.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //Given
    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", "87007770852");

        //When
        underTest.save(customer);

        //Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer).isPresent()
        .hasValueSatisfying(c -> {
            /*assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo("Abel");
            assertThat(c.getPhoneNumber()).isEqualTo("87007770852");*/
            assertThat(c).isEqualToComparingFieldByField(customer);
        });
    }
}