package com.amazon.ata.unittesting.subscribeandsave.dao;


import com.amazon.ata.unittesting.subscribeandsave.test.util.SubscriptionRestorer;
import com.amazon.ata.unittesting.subscribeandsave.types.Subscription;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionFileStorageTest {

    private static final String TEST_FILE_PATH = "resources/unittesting/classroom/subscribeandsave/subscriptions.csv";
    private static final String ASIN = "B01BMDAVIY";
    private static final String CUSTOMER_ID = "amzn1.account.AEZI3A063427738YROOFT8WCXKDE";
    private static final String CUSTOMER_ID_2 = "amzn1.account.AEZI4A063427738YROOFT8WCXKDE";

    private SubscriptionFileStorage subscriptionFileStorage;

    @BeforeEach
    private void setupSubscriptionFileStorage() {
        subscriptionFileStorage = new SubscriptionFileStorage(new File(TEST_FILE_PATH));
    }


    @Test
    void writeSubscription_newSubscription_subscriptionReturnedWithId() {
        // GIVEN - a new subscription to save
        int frequency = 1;
        Subscription newSubscription = Subscription.builder()
                                                   .withAsin(ASIN)
                                                   .withCustomerId(CUSTOMER_ID)
                                                   .withFrequency(frequency)
                                                   .build();

        // WHEN - create a new subscription
        Subscription result = subscriptionFileStorage.createSubscription(newSubscription);

        // THEN
        // a subscription should be returned
        assertNotNull(result, "Writing subscription should return the subscription");
        // the id field should be populated
        assertNotNull(result.getId(), "Writing subscription should return a subscription with an id field");
        // the customer ID should match
        assertEquals(CUSTOMER_ID, result.getCustomerId(),
                     "Writing a subscription should return a subscription with matching customer ID");
        // the ASIN should match
        assertEquals(ASIN, result.getAsin(),
                     "Writing a subscription should return a subscription with matching ASIN");
        // the frequency should match
        assertEquals(frequency, result.getFrequency(),
                     "Writing a subscription should return a subscription with matching frequency");
    }

    @Test
    void writeSubscription_subsequentGetById_returnsCorrectFields() {
        // GIVEN - a new subscription to save
        int frequency = 1;
        Subscription newSubscription = Subscription.builder()
                                                   .withAsin(ASIN)
                                                   .withCustomerId(CUSTOMER_ID)
                                                   .withFrequency(frequency)
                                                   .build();

        // WHEN - create a new subscription
        Subscription result = subscriptionFileStorage.createSubscription(newSubscription);

        // THEN
        // subsequently fetching the subscription
        Subscription refreshedSubscription = subscriptionFileStorage.getSubscriptionById(result.getId());
        // the customer ID should match
        assertEquals(CUSTOMER_ID, refreshedSubscription.getCustomerId(),
                     "Reading a subscription after writing should result in matching customer ID");
        // the ASIN should match
        assertEquals(ASIN, refreshedSubscription.getAsin(),
                     "Reading a subscription after writing should result in matching customer ID");
        // the frequency should match
        assertEquals(frequency, refreshedSubscription.getFrequency(),
                     "Reading a subscription after writing should result in matching frequency");
    }

    @Test
    void getSubscriptionById_withExistingSubscriptionId_returnsCorrectFields() {
        // GIVEN
        // An existing Subscription, with its expected ASIN, customer ID,
        String subscriptionId = "81a9792e-9b4c-4090-aac8-28e733ac2f54";
        String expectedAsin = "B00006IEJB";
        String expectedCustomerId = "amzn1.account.AEZI3A027560538W420H09ACTDP2";
        int expectedFrequency = 3;

        // WHEN - Get the Subscription by ID
        Subscription result = subscriptionFileStorage.getSubscriptionById(subscriptionId);

        // THEN
        // the subscription ID should match
        assertEquals(subscriptionId, result.getId(),
                     "Getting a subscription should return a subscription with same subscription ID");
        // the customer ID should match
        assertEquals(expectedCustomerId, result.getCustomerId(),
                     "Getting a subscription should return a subscription with correct customer ID");
        // the ASIN should match
        assertEquals(expectedAsin, result.getAsin(),
                     "Getting a subscription should return a subscription with correct ASIN");
        // the frequency should match
        assertEquals(expectedFrequency, result.getFrequency(),
                     "Getting a subscription should return a subscription with correct frequency");
    }

    @Test
    void updateSubscription_withExistingSubscriptionAndUpdatedFrequency_returnsUpdatedFrequency() {
        // GIVEN - An existing subscription with frequency 1, updated to frequency 2
        int newFrequency = 2;
        Subscription updatedSubscription = Subscription.builder()
                                                       .withSubscriptionId("1fe240f4-3296-4827-8c0e-7fa571b6f49f")
                                                       .withCustomerId("amzn1.account.AEZI3A09486461G3DRR0VQPQHQ9I")
                                                       .withAsin("B01BMDAVIY")
                                                       .withFrequency(newFrequency)
                                                       .build();

        // WHEN - Update the subscription
        Subscription result = subscriptionFileStorage.updateSubscription(updatedSubscription);

        // THEN - returned subscription frequency matches new value
        assertEquals(newFrequency, result.getFrequency());
    }


    @Test
    public void updateSubscription_withExistingSubscriptionAndUpdatedCustomerId_returnsUpdatedCustomerId() {
        // GIVEN - An existing subscription with valid customer Id, updated to new customer
        //int newFrequency = 2;
        Subscription updatedSubscription = Subscription.builder()
                .withSubscriptionId("1fe240f4-3296-4827-8c0e-7fa571b6f49f")
                .withCustomerId(CUSTOMER_ID_2)
                .withAsin("B01BMDAVIY")
                .withFrequency(1)
                .build();

        // WHEN - Update the subscription
        Subscription result = subscriptionFileStorage.updateSubscription(updatedSubscription);

        // THEN - returned subscription customer Id matches new value
        assertEquals(CUSTOMER_ID_2, result.getCustomerId());
    }

    @Test
    public void updateSubscription_withExistingSubscriptionAndUpdatedASIN_returnsUpdatedASIN() {
        // GIVEN - An existing subscription with valid ASIN, updated to new valid ASIN
        String newASIN = "B00006IEJB";
        Subscription updatedSubscription = Subscription.builder()
                .withSubscriptionId("1fe240f4-3296-4827-8c0e-7fa571b6f49f")
                .withCustomerId("amzn1.account.AEZI3A09486461G3DRR0VQPQHQ9I")
                .withAsin(newASIN)
                .withFrequency(1)
                .build();

        // WHEN - Update the subscription
        Subscription result = subscriptionFileStorage.updateSubscription(updatedSubscription);

        // THEN - returned subscription ASIN matches new value
        assertEquals(newASIN, result.getAsin());

    }

    @Test
    public void updateSubscription_withNullSubscription_throwsIllegalArgumentException() {
        //GIVEN
        Subscription nullSubscription = null;

        // WHEN - attempt to  update the subscription
        // THEN - exception is thrown

        assertThrows(IllegalArgumentException.class,
                () -> subscriptionFileStorage.updateSubscription(nullSubscription),
                "Updating null subscription will result in IllegalArgumentException thrown");
    }

    @Test
    public void updateSubscription_withSubscriptionMissingId_throwsIllegalArgumentException() {
        //GIVEN
        //Subscription missingSubscriptionID = "";
        Subscription updatedSubscription = Subscription.builder()
                .withSubscriptionId("")
                .withCustomerId("amzn1.account.AEZI3A09486461G3DRR0VQPQHQ9I")
                .withAsin("B01BMDAVIY")
                .withFrequency(1)
                .build();

        // WHEN - attempt to  update the subscription
        // THEN - exception is thrown

        assertThrows(IllegalArgumentException.class,
                () -> subscriptionFileStorage.updateSubscription(updatedSubscription),
                "Updating subscription with missing Id will result in IllegalArgumentException thrown");

    }

    @Test
    public void updateSubscription_withNoSubscriptionFound_throwsIllegalArgumentException() {
        //GIVEN
        //Subscription missingSubscriptionID = invalid;
        Subscription updatedSubscription = Subscription.builder()
                .withSubscriptionId("amzn1.account.RandomLettersThatShouldMeanNothing")
                .withCustomerId("amzn1.account.AEZI3A09486461G3DRR0VQPQHQ9I")
                .withAsin("B01BMDAVIY")
                .withFrequency(1)
                .build();

        // WHEN - attempt to  update the subscription
        // THEN - exception is thrown

        assertThrows(IllegalArgumentException.class,
                () -> subscriptionFileStorage.updateSubscription(updatedSubscription),
                "Updating subscription with invalid Id will result in IllegalArgumentException thrown");


    }

    @BeforeEach
    @AfterEach
    private void restoreSubscriptions() {
        SubscriptionRestorer.restoreSubscriptions();
    }
}
