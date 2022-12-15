package learn.petgallery.data;

import learn.petgallery.exceptions.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface FileRepository {
    String upload(MultipartFile file) throws FileUploadException;
}
