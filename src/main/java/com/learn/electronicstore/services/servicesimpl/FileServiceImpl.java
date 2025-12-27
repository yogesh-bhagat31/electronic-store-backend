package com.learn.electronicstore.services.servicesimpl;

import com.learn.electronicstore.exceptions.BadApiRequestException;
import com.learn.electronicstore.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(MultipartFile file, String pathName) throws IOException {

        String originalFileName = file.getOriginalFilename();
        log.info("FileName :{}", originalFileName);

        String fileName = UUID.randomUUID().toString();

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String fileNameWithExtension = fileName + extension;

        String fullPathWithFileName = pathName + fileNameWithExtension;

        log.info("full image path: " + fullPathWithFileName);

        if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".gif")) {

            File folder = new File(pathName);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));

            return fileNameWithExtension;

        } else {
            throw new BadApiRequestException("File with this gv " + extension + " is not supported");
        }

    }

    /**
     * Retrieves a file as an {@link InputStream} from the specified directory.
     * <p>
     * This method takes a directory path and a file name as input, constructs the full path to the file,
     * and returns an {@link InputStream} to read the file's content. If the file does not exist, it throws
     * a {@link FileNotFoundException}.
     * </p>
     *
     * @param pathName the directory path where the file is stored.
     *                 <ul>
     *                   <li>Must be a valid directory path.</li>
     *                   <li>Can include absolute or relative paths.</li>
     *                 </ul>
     * @param imageName     the name of the file to be retrieved.
     *                 <ul>
     *                   <li>Must include the file's name with its extension (e.g., `example.txt`).</li>
     *                   <li>Should match the file stored in the specified directory.</li>
     *                 </ul>
     * @return an {@link InputStream} for the requested file, which can be used to read its content.
     * @throws FileNotFoundException if the file is not found at the constructed path.
     */
    @Override
    public InputStream getResource(String pathName, String imageName) throws FileNotFoundException {
        String fullPath = pathName + imageName;
        InputStream inputStream = new FileInputStream(fullPath);
        return inputStream;
    }
}

