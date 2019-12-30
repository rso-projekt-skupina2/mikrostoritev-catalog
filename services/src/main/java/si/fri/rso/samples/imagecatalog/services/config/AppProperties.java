package si.fri.rso.samples.imagecatalog.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("app-properties")
public class AppProperties {

    @ConfigValue(value = "external-services.enabled", watch = true)
    private boolean externalServicesEnabled;

    @ConfigValue(watch = true)
    private boolean healthy;

    @ConfigValue("amazon.access-key")
    private String amazonAccessKey;

    @ConfigValue("amazon.secret-key")
    private String amazonSecretKey;

    public boolean isExternalServicesEnabled() {
        return externalServicesEnabled;
    }

    public void setExternalServicesEnabled(boolean externalServicesEnabled) {
        this.externalServicesEnabled = externalServicesEnabled;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public String getAmazonSecretKey() {
        return amazonSecretKey;
    }

    public void setAmazonSecretKey(String amazonSecretKey) {
        this.amazonSecretKey = amazonSecretKey;
    }

    public String getAmazonAccessKey() {
        return amazonAccessKey;
    }

    public void setAmazonAccessKey(String amazonAccessKey) {
        this.amazonAccessKey = amazonAccessKey;
    }
}