package rewards;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import config.RewardsConfig;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

/**
 * Test configuration class that sets up an in-memory database for testing the rewards system.
 * 
 * <p>This configuration:
 * <ul>
 *   <li>Imports the main {@link RewardsConfig} to provide the application beans</li>
 *   <li>Configures an embedded HSQL database for testing</li>
 *   <li>Loads database schema and test data from SQL scripts</li>
 * </ul>
 * 
 * <p>The embedded database is populated with test data from the following scripts:
 * <ul>
 *   <li>schema.sql - Creates the database schema (tables, constraints)</li>
 *   <li>data.sql - Inserts test data into the database</li>
 * </ul>
 * 
 * <p>These scripts are located in the classpath under 'rewards/testdb/' directory.
 */
@Configuration
@Import(RewardsConfig.class)
public class TestInfrastructureConfig {

	/**
	 * Creates and configures an in-memory HSQL database for testing purposes.
	 * 
	 * <p>The database is initialized with:
	 * <ul>
	 *   <li>Schema defined in 'classpath:rewards/testdb/schema.sql'</li>
	 *   <li>Test data from 'classpath:rewards/testdb/data.sql'</li>
	 * </ul>
     *
	 * @return A fully configured {@link DataSource} for the in-memory database
	 */
	@Bean
	public DataSource dataSource() {
		return (new EmbeddedDatabaseBuilder()) //
				.addScript("classpath:rewards/testdb/schema.sql") //
				.addScript("classpath:rewards/testdb/data.sql") //
				.build();
	}
}
