package si.fri.rso.samples.imagecatalog.api.v1.resources;

import si.fri.rso.samples.imagecatalog.api.v1.utils.ImageCatalogUtils;
import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.services.beans.ImageMetadataBean;
import si.fri.rso.samples.imagecatalog.services.clients.AmazonClient;
import si.fri.rso.samples.imagecatalog.services.streaming.EventProducerImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.eclipse.microprofile.metrics.annotation.Timed;

import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;
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

    @Inject
    private AmazonClient amazonClient;

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

        imageMetadata.setTranslatedDescription(amazonClient.translate(imageMetadata.getDescription()));

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
    public Response uploadImage( InputStream uploadedInputStream, @QueryParam("title") String title) {

        String imageId = UUID.randomUUID().toString();

        byte[] data;
        try {
            data = IOUtils.toByteArray(uploadedInputStream);
        } catch (IOException e) {
            log.severe("Napaka pri pretvarjanju slike v byte[]!" + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        ImageMetadata imageMetadata = new ImageMetadata();
        List<Label> labels = amazonClient.getLabels(data);
        if (labels.size() >= 1) {
            imageMetadata.setTitle(title);
            imageMetadata.setDescription(ImageCatalogUtils.labelsToString(labels));
            imageMetadata.setUri(" ");
            imageMetadata = imageMetadataBean.createImageMetadata(imageMetadata);
        }

        eventProducer.produceMessage(imageId, imageMetadata.getDescription());

        return Response.status(Response.Status.CREATED).entity(imageMetadata).build();
    }

}
