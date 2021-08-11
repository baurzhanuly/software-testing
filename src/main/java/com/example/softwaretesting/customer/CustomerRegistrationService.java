package com.example.softwaretesting.customer;

import com.example.softwaretesting.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository, PhoneNumberValidator phoneNumberValidator) {
        this.customerRepository = customerRepository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request){
        String phoneNumber = request.getCustomer().getPhoneNumber();

        if (!phoneNumberValidator.test(phoneNumber)){
            throw new IllegalStateException("Phone Number " + phoneNumber + "is not valid");
        }
        Optional<Customer> customer = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        if (customer.isPresent()){
            Customer newCustomer = customer.get();
            if (newCustomer.getName().equals(request.getCustomer().getName())){
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s]is taken", phoneNumber));
        }

        if (request.getCustomer().getId() == null){
            request.getCustomer().setId(UUID.randomUUID());
        }
        customerRepository.save(request.getCustomer());
    }
}
