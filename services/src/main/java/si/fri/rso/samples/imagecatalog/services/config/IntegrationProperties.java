package si.fri.rso.samples.imagecatalog.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("configuration-properties")
@ApplicationScoped
public class IntegrationProperties {

    @ConfigValue(value = "comments-service.enabled", watch = true)
    private boolean integrateWithCommentsService;

    public boolean isIntegrateWithCommentsService() {
        return integrateWithCommentsService;
    }

    public void setIntegrateWithCommentsService(boolean integrateWithCommentsService) {
        this.integrateWithCommentsService = integrateWithCommentsService;
    }
}