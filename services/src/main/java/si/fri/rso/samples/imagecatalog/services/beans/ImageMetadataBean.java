package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.models.converters.ImageMetadataConverter;
import si.fri.rso.samples.imagecatalog.models.entities.ImageMetadataEntity;
import si.fri.rso.samples.imagecatalog.services.config.IntegrationProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class ImageMetadataBean {

    private Logger log = Logger.getLogger(ImageMetadataBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;

    @Inject
    @DiscoverService("comments-service")
    private Optional<String> baseUrl;

    @Inject
    private IntegrationProperties integrationProperties;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }


    public List<ImageMetadata> getImageMetadata() {

        TypedQuery<ImageMetadataEntity> query = em.createNamedQuery("ImageMetadataEntity.getAll",
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
        if (integrationProperties.isIntegrateWithCommentsService()) {
            imageMetadata.setNumberOfComments(getCommentCount(id));
            imageMetadata.setNumberOfRatings(getRatingCount(id));
            imageMetadata.setAvergeRating(getAvergeRating(id));
        }

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

        ImageMetadataEntity updatedImageMetadataEntity = ImageMetadataConverter.toEntity(imageMetadata);

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
        if (baseUrl.isPresent()) {
            try {
                log.info("baseUrl:" +baseUrl.get());
                return httpClient
                        .target(baseUrl.get() + "/v1/comments/count?imageId=" + imageId)
                        .request().get(new GenericType<Integer>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    public Integer getRatingCount(Integer imageId) {
        if (baseUrl.isPresent()) {
            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/rating/count?imageId=" + imageId)
                        .request().get(new GenericType<Integer>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    public Double getAvergeRating(Integer imageId) {
        if (baseUrl.isPresent()) {
            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/rating/averge?imageId=" + imageId)
                        .request().get(new GenericType<Double>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
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
