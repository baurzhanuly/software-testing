package com.example.softwaretesting.customer;

import com.example.softwaretesting.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class CustomerRegistrationServiceTest {

    private CustomerRegistrationService underTest;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Bob", phoneNumber);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //Valid Phone Number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.registerNewCustomer(customerRegistrationRequest);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer result = customerArgumentCaptor.getValue();
        assertThat(result).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldNotSaveNewCustomerWhenPhoneNumberIsInvalid() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Bob", phoneNumber);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        //Valid Phone Number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        //When

        assertThatThrownBy(()-> underTest.registerNewCustomer(customerRegistrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Phone Number " + phoneNumber + "is not valid");
        //Then
        then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Bob", phoneNumber);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //Valid Phone Number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.registerNewCustomer(customerRegistrationRequest);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer result = customerArgumentCaptor.getValue();
        assertThat(result).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Bob", phoneNumber);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        //Valid Phone Number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        //When

        underTest.registerNewCustomer(customerRegistrationRequest);
        //Then

        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);//или мынау
        then(customerRepository).shouldHaveNoMoreInteractions();
        //then(customerRepository).should(never()).save(any());//или мынау
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Bob", phoneNumber);
        Customer customerTwo = new Customer(UUID.randomUUID(), "John", phoneNumber);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customerTwo));
        //When

        //Valid Phone Number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        assertThatThrownBy(()-> underTest.registerNewCustomer(customerRegistrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s]is taken", phoneNumber));

    }
}