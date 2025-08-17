package config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewards.RewardNetwork;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

/**
 * Spring configuration class for the rewards application.
 * 
 * This class defines the following beans:
 * - rewardNetwork: The main service for processing reward transactions
 * - accountRepository: Repository for accessing account data
 * - restaurantRepository: Repository for accessing restaurant data
 * - rewardRepository: Repository for recording reward transactions
 * 
 * The DataSource is injected through constructor injection and is used by all repository beans.
 * All beans are singleton-scoped by default and are eagerly instantiated.
 */
@Configuration
public class RewardsConfig {

	/**
	 * The data source used by all repository beans for database access.
	 * Injected through the constructor.
	 */
	private final DataSource dataSource;

	/**
	 * Creates a new RewardsConfig with the specified data source.
     *
     * @param dataSource the data source to be used by repository beans
     */
	public RewardsConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
     * Creates and configures the main reward network service.
     * 
     * @return the configured RewardNetwork implementation
     */
	@Bean
	public RewardNetwork rewardNetwork() {
		return new RewardNetworkImpl(accountRepository(), restaurantRepository(), rewardRepository());
	}

	/**
     * Creates and configures the account repository.
     * 
     * @return the configured AccountRepository implementation
     */
	@Bean
	public AccountRepository accountRepository() {
		JdbcAccountRepository accountRepository = new JdbcAccountRepository();
		accountRepository.setDataSource(dataSource);
		return accountRepository;
	}

	/**
     * Creates and configures the restaurant repository.
     * 
     * @return the configured RestaurantRepository implementation
     */
	@Bean
	public RestaurantRepository restaurantRepository() {
		JdbcRestaurantRepository restaurantRepository = new JdbcRestaurantRepository();
		restaurantRepository.setDataSource(dataSource);
		return restaurantRepository;
	}

	/**
     * Creates and configures the reward repository.
     * 
     * @return the configured RewardRepository implementation
     */
	@Bean
	public RewardRepository rewardRepository() {
		JdbcRewardRepository rewardRepository = new JdbcRewardRepository();
		rewardRepository.setDataSource(dataSource);
		return rewardRepository;
	}

}
