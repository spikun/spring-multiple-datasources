
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration(
        exclude = {
                HypermediaAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan
@EnableTransactionManagement
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
