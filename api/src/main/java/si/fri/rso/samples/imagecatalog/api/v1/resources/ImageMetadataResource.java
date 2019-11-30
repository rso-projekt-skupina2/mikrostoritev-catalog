package si.fri.rso.samples.imagecatalog.api.v1.resources;

import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.services.beans.ImageMetadataBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import com.kumuluz.ee.logs.cdi.Log;

@Log
@ApplicationScoped
@Path("/images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImageMetadataResource {

    @Inject
    private ImageMetadataBean imageMetadataBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getImageMetadata() {
        List<ImageMetadata> imageMetadata = imageMetadataBean.getImageMetadataFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(imageMetadata).build();
    }

    @GET
    @Timed
    @Path("/{imageMetadataId}")
    public Response getImageMetadata(@PathParam("imageMetadataId") Integer imageMetadataId) {
        ImageMetadata imageMetadata = imageMetadataBean.getImageMetadata(imageMetadataId);

        if (imageMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(imageMetadata).build();
    }

    @POST
    @Timed
    public Response createImageMetadata(ImageMetadata imageMetadata) {
        if ((imageMetadata.getTitle() == null || imageMetadata.getDescription() == null || imageMetadata.getUri() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            imageMetadata = imageMetadataBean.createImageMetadata(imageMetadata);
        }

        return Response.status(Response.Status.CONFLICT).entity(imageMetadata).build();

    }

    @PUT
    @Timed
    @Path("{imageMetadataId}")
    public Response putImageMetadata(@PathParam("imageMetadataId") Integer imageMetadataId,
                                     ImageMetadata imageMetadata) {
        imageMetadata = imageMetadataBean.putImageMetadata(imageMetadataId, imageMetadata);

        if (imageMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @DELETE
    @Timed
    @Path("{imageMetadataId}")
    public Response deleteImageMetadata(@PathParam("imageMetadataId") Integer imageMetadataId) {
        boolean deleted = imageMetadataBean.deleteImageMetadata(imageMetadataId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
