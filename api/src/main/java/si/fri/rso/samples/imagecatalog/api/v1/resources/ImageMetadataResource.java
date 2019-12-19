package si.fri.rso.samples.imagecatalog.api.v1.resources;

import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.services.beans.ImageMetadataBean;
import si.fri.rso.samples.imagecatalog.services.streaming.EventProducerImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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

    private Logger log = Logger.getLogger(ImageMetadataResource.class.getName());

    @Inject
    private EventProducerImpl eventProducer;

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
    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadImage( InputStream uploadedInputStream) {

        String imageId = UUID.randomUUID().toString();
        String imageLocation = UUID.randomUUID().toString();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        try {
            while ((nRead = uploadedInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        } catch (IOException e) {
            log.severe("Napaka pri pretvarjanju slike v byte[]!" + e);
            return null;
        }

        // Upload image to storage
        // Generate event for image processing
        eventProducer.produceMessage(imageId, imageLocation);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
