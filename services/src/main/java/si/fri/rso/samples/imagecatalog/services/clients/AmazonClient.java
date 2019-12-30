package si.fri.rso.samples.imagecatalog.services.clients;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClientBuilder;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;

import si.fri.rso.samples.imagecatalog.services.config.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.List;

@ApplicationScoped
public class AmazonClient {

    @Inject
    private AppProperties appProperties;

    private AmazonRekognition rekognitionClient;

    private AmazonTranslate translateClient;

    @PostConstruct
    private void init() {

        AWSCredentials credentials;
        try {
            credentials = new BasicAWSCredentials(
                    appProperties.getAmazonAccessKey(),
                    appProperties.getAmazonSecretKey());
        } catch (Exception e) {
            throw new AmazonClientException("Cannot initialise the credentials.", e);
        }

        rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        translateClient = AmazonTranslateClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public List<Label> getLabels (byte[] imageBytes) {

        Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));

        DetectLabelsRequest detectLabelsRequest =
                new DetectLabelsRequest().withImage(image);

        DetectLabelsResult detectLabelsResult = rekognitionClient.detectLabels(detectLabelsRequest);

        return detectLabelsResult.getLabels();

    }

    public String translate (String txt){

        TranslateTextRequest translateTextRequest = new TranslateTextRequest().withText(txt).withSourceLanguageCode("en").withTargetLanguageCode("sl");

        TranslateTextResult translateTextResult = translateClient.translateText(translateTextRequest);

        return translateTextResult.getTranslatedText();
    }

}