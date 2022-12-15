package learn.petgallery.data;

import learn.petgallery.exceptions.FileUploadException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class S3FileRepository implements FileRepository {

    public final static int MAX_IMAGE_WIDTH = 1200;

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String bucketName;
    private final Region bucketRegion;

    public S3FileRepository(
            @Value("${aws.access.key.id}") String accessKeyId,
            @Value("${aws.secret.access.key}") String secretAccessKey,
            @Value("${aws.s3.bucket.name}") String bucketName,
            @Value("${aws.s3.bucket.region}") String bucketRegion) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.bucketName = bucketName;
        this.bucketRegion = Region.of(bucketRegion);
    }

    @Override
    public String upload(MultipartFile file) throws FileUploadException {
        try {
            return upload(ImageIO.read(file.getInputStream()), file.getOriginalFilename(), file.getContentType());
        } catch (IOException | S3Exception ex) {
            throw new FileUploadException(ex.getMessage(), ex);
        }
    }

    private String upload(BufferedImage image, String filename, String contentType) throws FileUploadException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        try (S3Client s3 = S3Client.builder()
                .region(bucketRegion)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build()) {

            if (image.getWidth() > MAX_IMAGE_WIDTH) {
                image = resizeImage(image);
            }

            PutObjectRequest request = buildS3PutRequest(filename, contentType);
            RequestBody requestBody = convertImageToRequestBody(filename, image);

            PutObjectResponse response = s3.putObject(request, requestBody);

            return s3.utilities().getUrl(builder -> builder.bucket(bucketName).key(filename)).toString();
        } catch (IOException | S3Exception ex) {
            throw new FileUploadException(ex.getMessage(), ex);
        }
    }

    private PutObjectRequest buildS3PutRequest(String filename, String contentType) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType(contentType)
                .build();
    }

    private RequestBody convertImageToRequestBody(String filename, BufferedImage image) throws IOException {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, extension, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return RequestBody.fromBytes(bytes);
    }

    private BufferedImage resizeImage(BufferedImage original) {
        return Scalr.resize(original, MAX_IMAGE_WIDTH);
    }
}