package si.fri.rso.samples.imagecatalog.api.v1.utils;

import java.util.List;

import com.amazonaws.services.rekognition.model.Label;

public class ImageCatalogUtils {


    public static String labelsToString(List<Label> labels) {
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Label label : labels) {
            if (count >= 5) {
                result.append(label.getName()).append(".");
                break;
            }
            else{
                result.append(label.getName()).append(", ");
                count++;
            }
        }

        return result.toString();
    }

}
