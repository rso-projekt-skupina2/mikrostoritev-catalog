package si.fri.rso.samples.imagecatalog.api.v1.resources;

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@ApplicationScoped
@Path("/info")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectInfoResource {
    private Logger log = Logger.getLogger(ImageMetadataResource.class.getName());

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getImageMetadata() {
        log.info("GET /images called ");
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        String jsonString = factory.createObjectBuilder()
                .add("clani", factory.createArrayBuilder().add("js8649"))
                .add("opis_projekta", "Ta projekt implementira pregled kataloga slik in priporocilnega sistema za te slike.")
                .add("mikrostoritve", factory.createArrayBuilder().add("http://52.224.140.76:8080/v1/images")
                                                                     .add("http://52.190.25.22:8080/v1/rating"))
                .add("github", factory.createArrayBuilder().add("https://github.com/rso-projekt-skupina2/mikrostoritev-recomender")
                                                              .add("https://github.com/rso-projekt-skupina2/mikrostoritev-catalog"))
                .add("travis", factory.createArrayBuilder().add("https://travis-ci.org/rso-projekt-skupina2/mikrostoritev-catalog")
                                                              .add("https://travis-ci.org/rso-projekt-skupina2/mikrostoritev-recomender"))
                .add("dockerhub", factory.createArrayBuilder().add("https://hub.docker.com/r/baraba123/rso-image-catalog/")
                                                                 .add("https://hub.docker.com/r/baraba123/rso-recomender/"))
                .build()
                .toString();

        return Response.status(Response.Status.OK).entity(jsonString).build();
    }
}
