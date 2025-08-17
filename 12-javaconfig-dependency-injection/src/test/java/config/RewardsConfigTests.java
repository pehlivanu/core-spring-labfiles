package config;

import org.assertj.core.api.Fail;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import rewards.RewardNetwork;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

import javax.sql.DataSource;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the {@link RewardsConfig} class to ensure it is creating the right
 * beans with the correct dependencies and configurations.
 * 
 * <p>This test verifies that all expected beans are properly instantiated and that
 * the data source is correctly injected into repository beans.</p>
 */
@SuppressWarnings("unused")
public class RewardsConfigTests {
	// Provide a mock object for testing
	private DataSource dataSource = Mockito.mock(DataSource.class);
	
	private RewardsConfig rewardsConfig = new RewardsConfig(dataSource);

	/**
	 * Tests that the Spring configuration correctly creates and configures all expected beans.
	 * 
	 * <p>Verifies that:
	 * <ul>
	 *   <li>A {@link RewardNetwork} bean of type {@link RewardNetworkImpl} is created</li>
	 *   <li>An {@link AccountRepository} bean of type {@link JdbcAccountRepository} is created with a data source</li>
	 *   <li>A {@link RestaurantRepository} bean of type {@link JdbcRestaurantRepository} is created with a data source</li>
	 *   <li>A {@link RewardRepository} bean of type {@link JdbcRewardRepository} is created with a data source</li>
	 * </ul>
	 * </p>
	 */
	@Test
	public void getBeans() {
		RewardNetwork rewardNetwork = rewardsConfig.rewardNetwork();
		assertTrue(rewardNetwork instanceof RewardNetworkImpl);

		AccountRepository accountRepository = rewardsConfig.accountRepository();
		assertTrue(accountRepository instanceof JdbcAccountRepository);
		checkDataSource(accountRepository);

		RestaurantRepository restaurantRepository = rewardsConfig.restaurantRepository();
		assertTrue(restaurantRepository instanceof JdbcRestaurantRepository);
		checkDataSource(restaurantRepository);

		RewardRepository rewardsRepository = rewardsConfig.rewardRepository();
		assertTrue(rewardsRepository instanceof JdbcRewardRepository);
		checkDataSource(rewardsRepository);
	}
	

	/**
	 * Ensure the data-source is set for the repository. Uses reflection as we do
	 * not wish to provide a getDataSource() method.
	 * 
	 * @param repository One of our three repositories.
	 *
	 */
	private void checkDataSource(Object repository) {
		Class<? extends Object> repositoryClass = repository.getClass();

		try {
			Field dataSource = repositoryClass.getDeclaredField("dataSource");
			dataSource.setAccessible(true);
			assertNotNull(dataSource.get(repository));
		} catch (Exception e) {
			String failureMessage = "Unable to validate dataSource in " + repositoryClass.getSimpleName();
			System.out.println(failureMessage);
			e.printStackTrace();
			Fail.fail(failureMessage);
		}
	}
}
