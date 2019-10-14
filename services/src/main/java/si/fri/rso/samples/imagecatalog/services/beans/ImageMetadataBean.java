package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.models.converters.ImageMetadataConverter;
import si.fri.rso.samples.imagecatalog.models.entities.ImageMetadataEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@ApplicationScoped
public class ImageMetadataBean {

    private Logger log = Logger.getLogger(ImageMetadataBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;

    private String baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://localhost:8081"; // only for demonstration
    }


    public List<ImageMetadata> getImageMetadata() {

        TypedQuery<ImageMetadataEntity> query = em.createNamedQuery("ImageMetadataEntiry.getAll",
                ImageMetadataEntity.class);

        return query.getResultList().stream().map(ImageMetadataConverter::toDto).collect(Collectors.toList());

    }

    public List<ImageMetadata> getImageMetadataFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ImageMetadataEntity.class, queryParameters).stream()
                .map(ImageMetadataConverter::toDto).collect(Collectors.toList());
    }

    public ImageMetadata getImageMetadata(Integer id) {

        ImageMetadataEntity imageMetadataEntity = em.find(ImageMetadataEntity.class, id);

        if (imageMetadataEntity == null) {
            throw new NotFoundException();
        }

        ImageMetadata imageMetadata = ImageMetadataConverter.toDto(imageMetadataEntity);
        imageMetadata.setNumberOfComments(getCommentCount(id));

        return imageMetadata;
    }

    public ImageMetadata createImageMetadata(ImageMetadata imageMetadata) {

        ImageMetadataEntity imageMetadataEntity = ImageMetadataConverter.toEntity(imageMetadata);

        try {
            beginTx();
            em.persist(imageMetadataEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (imageMetadataEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ImageMetadataConverter.toDto(imageMetadataEntity);
    }

    public ImageMetadata putImageMetadata(Integer id, ImageMetadata imageMetadata) {

        ImageMetadataEntity c = em.find(ImageMetadataEntity.class, id);

        if (c == null) {
            return null;
        }

        ImageMetadataEntity updatedImageMetadataEntity = new ImageMetadataEntity();

        try {
            beginTx();
            updatedImageMetadataEntity.setId(c.getId());
            updatedImageMetadataEntity = em.merge(updatedImageMetadataEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return ImageMetadataConverter.toDto(updatedImageMetadataEntity);
    }

    public boolean deleteImageMetadata(Integer id) {

        ImageMetadataEntity imageMetadata = em.find(ImageMetadataEntity.class, id);

        if (imageMetadata != null) {
            try {
                beginTx();
                em.remove(imageMetadata);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }


    public Integer getCommentCount(Integer imageId) {

        try {
            return httpClient
                    .target(baseUrl + "/v1/comments/count?imageId=" + imageId)
                    .request().get(new GenericType<Integer>() {
                    });
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

    }


    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public void loadOrder(Integer n) {


    }
}
