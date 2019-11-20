package si.fri.rso.samples.imagecatalog.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("configuration-properties")
@ApplicationScoped
public class IntegrationProperties {

    @ConfigValue(value = "recomender-service.enabled", watch = true)
    private boolean integrateWithRecomenderService;

    public boolean isIntegrateWithRecomenderService() {
        return integrateWithRecomenderService;
    }


    public void setIntegrateWithRecomenderService(boolean integrateWithRecomenderService) {
        this.integrateWithRecomenderService = integrateWithRecomenderService;
    }
}