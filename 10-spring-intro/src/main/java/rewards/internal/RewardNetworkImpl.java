package rewards.internal;

import common.money.MonetaryAmount;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.internal.account.Account;
import rewards.internal.account.AccountRepository;
import rewards.internal.restaurant.Restaurant;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

/**
 * Rewards an Account for Dining at a Restaurant.
 * 
 * The sole Reward Network implementation. This object is an application-layer service responsible for coordinating with
 * the domain-layer to carry out the process of rewarding benefits to accounts for dining.
 * 
 * Said in other words, this class implements the "reward account for dining" use case.
 */
public class RewardNetworkImpl implements RewardNetwork {

	private AccountRepository accountRepository;

	private RestaurantRepository restaurantRepository;

	private RewardRepository rewardRepository;

	/**
	 * Creates a new reward network.
	 * @param accountRepository the repository for loading accounts to reward
	 * @param restaurantRepository the repository for loading restaurants that determine how much to reward
	 * @param rewardRepository the repository for recording a record of successful reward transactions
	 */
	public RewardNetworkImpl(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
			RewardRepository rewardRepository) {
		this.accountRepository = accountRepository;
		this.restaurantRepository = restaurantRepository;
		this.rewardRepository = rewardRepository;
	}

	public RewardConfirmation rewardAccountFor(Dining dining) {
		// 1. Look up account by credit card number
		Account account = accountRepository.findByCreditCard(dining.getCreditCardNumber());
		
		// 2. Look up restaurant by merchant number
		Restaurant restaurant = restaurantRepository.findByMerchantNumber(dining.getMerchantNumber());
		
		// 3. Calculate benefit amount
		MonetaryAmount amount = restaurant.calculateBenefitFor(account, dining);
		
		// 4. Create account contribution
		AccountContribution contribution = account.makeContribution(amount);
		
		// 5. Update beneficiaries
		accountRepository.updateBeneficiaries(account);
		
		// 6. Confirm and return the reward
		return rewardRepository.confirmReward(contribution, dining);
	}
}