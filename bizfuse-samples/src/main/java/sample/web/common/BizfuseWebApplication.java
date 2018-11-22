package sample.web.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import com.leadingsoft.bizfuse.common.web.annotation.EnableBizfuseWebMVC;
import com.leadingsoft.bizfuse.common.web.config.BizfuseWebProperties;

@EnableBizfuseWebMVC
@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
@EnableConfigurationProperties({BizfuseWebProperties.class })
public class BizfuseWebApplication {

    private static final Logger log = LoggerFactory.getLogger(BizfuseWebApplication.class);

    private static final String SPRING_PROFILE_ACTIVE = "spring.profiles.active";
    private static final String BIZFUSE_WEB_PROFILE = "bizfuseweb,swagger";

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved
     *         into an address
     */
    public static void main(final String[] args) throws UnknownHostException {
        final SpringApplication app = new SpringApplication(BizfuseWebApplication.class);

        // 手动添加默认profiles
        final Map<String, Object> defProperties = new HashMap<>();
        defProperties.put(BizfuseWebApplication.SPRING_PROFILE_ACTIVE, BizfuseWebApplication.BIZFUSE_WEB_PROFILE);
        app.setDefaultProperties(defProperties);

        final Environment env = app.run(args).getEnvironment();
        BizfuseWebApplication.log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\thttp://127.0.0.1:{}\n\t" +
                "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));

        final String configServerStatus = env.getProperty("configserver.status");
        BizfuseWebApplication.log.info("\n----------------------------------------------------------\n\t" +
                "Config Server: \t{}\n----------------------------------------------------------",
                configServerStatus == null ? "Not found or not setup for this application" : configServerStatus);
    }
}
