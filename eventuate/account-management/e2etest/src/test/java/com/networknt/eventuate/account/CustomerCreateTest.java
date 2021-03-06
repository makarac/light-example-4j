package com.networknt.eventuate.account;

import com.networknt.eventuate.account.command.customer.CustomerService;
import com.networknt.eventuate.account.common.model.customer.*;
import com.networknt.eventuate.common.AggregateRepository;
import com.networknt.eventuate.common.EventuateAggregateStore;


import com.networknt.service.SingletonServiceFactory;
import org.h2.tools.RunScript;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import com.networknt.eventuate.account.command.customer.Customer;


/**
* Generated by swagger-codegen
*/
public class CustomerCreateTest {

    static final Logger logger = LoggerFactory.getLogger(CustomerCreateTest.class);

    public static DataSource ds;

    static {
        ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);
        try (Connection connection = ds.getConnection()) {
            // Runscript doesn't work need to execute batch here.
            String schemaResourceName = "/embedded-event-store-schema.sql";
            InputStream in = CustomerCreateTest.class.getResourceAsStream(schemaResourceName);

            if (in == null) {
                throw new RuntimeException("Failed to load resource: " + schemaResourceName);
            }
            InputStreamReader reader = new InputStreamReader(in);
            RunScript.execute(connection, reader);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private EventuateAggregateStore eventStore  = (EventuateAggregateStore)SingletonServiceFactory.getBean(EventuateAggregateStore.class);

    private AggregateRepository customerRepository = new AggregateRepository(Customer.class, eventStore);


    private CustomerService service = new CustomerService(customerRepository);

    @Test
    public void testAddCustomer() throws Exception {

        Name name = new Name("Google", "Com");
        Address address = new Address("Yonge St" , "2556 unit", "toronto", "ON", "Canada", "L3R, 5F5");
        UserCredentials userCredentials = new UserCredentials("aaa.bbb@google.com", "password");
        CustomerInfo customerInfo = new CustomerInfo(name, userCredentials, "999999999", "4166666666", address);


        CompletableFuture<CustomerInfo> result = service.createCustomer(customerInfo).thenApply((e) -> {
            CustomerInfo m = e.getAggregate().getCustomerInfo();
            System.out.println("m = " + m);
            System.out.println("m = " + e.getEntityId());
            return m;
        });

        System.out.println("result = " + result.get());
    }

    @Test
    public void testToAccount() throws Exception {

        Name name = new Name("Google2", "Com2");
        Address address = new Address("Yonge St" , "2556 unit", "toronto", "ON", "Canada", "L3R, 5F5");
        UserCredentials userCredentials = new UserCredentials("aaa.bbb@google.com", "password");
        CustomerInfo customerInfo = new CustomerInfo(name, userCredentials, "999999999", "4166666666", address);

        CompletableFuture<String> result = service.createCustomer(customerInfo).thenApply((e) -> {
            String m =  e.getEntityId();
            return m;
        });


        ToAccountInfo toAccountInfo = new ToAccountInfo("1234567777", "TFSA", "Google" , "test");


        CompletableFuture<CustomerInfo> result2 = service.addToAccount(result.get(), toAccountInfo).thenApply((e) -> {
            CustomerInfo m = e.getAggregate().getCustomerInfo();
            System.out.println("m = " + m);
            System.out.println("m = " + e.getEntityId());
            return m;
        });

        System.out.println("result = " + result.get());
    }
}
