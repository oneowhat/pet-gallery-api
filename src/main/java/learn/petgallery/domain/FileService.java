package learn.petgallery.domain;

import learn.petgallery.data.FileRepository;
import learn.petgallery.exceptions.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Result<String> uploadFile(MultipartFile file) {
        Result<String> result = new Result<>();
        try {
            result.setPayload(fileRepository.upload(file));
        } catch (FileUploadException ex) {
            ex.printStackTrace();
            result.addMessage("File not uploaded.", ResultType.INVALID);
        }
        return result;
    }
}
