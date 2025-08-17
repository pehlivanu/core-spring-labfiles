package rewards;

import common.money.MonetaryAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the {@link RewardNetwork} implementation using Spring's dependency injection.
 * 
 * <p>This test verifies that all components are correctly wired together through Spring's
 * application context and that the reward network functions as expected with a real database.
 * 
 * <p>The test uses an in-memory HSQL database populated with test data from SQL scripts.
 * 
 * @see TestInfrastructureConfig
 * @see config.RewardsConfig
 */
public class RewardNetworkTests {

    private RewardNetwork rewardNetwork;
    
    /**
     * Sets up the test environment before each test method execution.
     * 
     * <p>This method:
     * <ul>
     *   <li>Creates a new Spring application context using {@link TestInfrastructureConfig}</li>
     *   <li>Retrieves the {@link RewardNetwork} bean from the context</li>
     *   <li>Stores the bean in an instance variable for test methods to use</li>
     * </ul>
     * 
     * <p>The application context is automatically closed after each test method completes.
     */
    @BeforeEach
    public void setUp() {
        // Create application context using TestInfrastructureConfig
        ConfigurableApplicationContext context = SpringApplication.run(TestInfrastructureConfig.class);
        // Get the rewardNetwork bean from the application context
        rewardNetwork = context.getBean(RewardNetwork.class);
    }

    /**
     * Tests the complete reward process for a dining transaction.
     * 
     * <p>This test verifies that:
     * <ul>
     *   <li>A dining transaction is processed successfully</li>
     *   <li>A reward confirmation is generated with a confirmation number</li>
     *   <li>The correct account is credited with rewards</li>
     *   <li>The reward amount is calculated correctly (8% of dining amount)</li>
     *   <li>The reward is distributed correctly among beneficiaries</li>
     * </ul>
     * 
     * @see Dining#createDining(String, String, String)
     * @see RewardNetwork#rewardAccountFor(Dining)
     * @see RewardConfirmation
     * @see AccountContribution
     */
    @Test
    public void testRewardForDining() {
        // create a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        // this fails if you have selected an account without beneficiaries!
        RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

		// assert the expected reward confirmation results
		assertNotNull(confirmation);
		assertNotNull(confirmation.getConfirmationNumber());

		// assert an account contribution was made
		AccountContribution contribution = confirmation.getAccountContribution();
		assertNotNull(contribution);

		// the contribution account number should be '123456789'
		assertEquals("123456789", contribution.getAccountNumber());

		// the total contribution amount should be 8.00 (8% of 100.00)
		assertEquals(MonetaryAmount.valueOf("8.00"), contribution.getAmount());

		// the total contribution amount should have been split into 2 distributions
		assertEquals(2, contribution.getDistributions().size());

		// each distribution should be 4.00 (as both have a 50% allocation)
		assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Annabelle").getAmount());
		assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Corgan").getAmount());
	}
}
