package com.learn.electronicstore.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The {@code FileService} interface defines methods for handling file-related operations,
 * such as uploading files and retrieving resources from a specific path.
 * <p>
 * This interface is designed to abstract file storage operations, making it easier
 * to implement file handling in different storage mechanisms (e.g., local storage, cloud storage).
 */
public interface FileService {
    /**
     * Uploads a file to the specified path.
     *
     * @param file     the {@link MultipartFile} object to be uploaded.
     * @param pathName the path where the file will be stored.
     * @return a {@link String} representing the complete path or name of the stored file.
     * @throws IOException if an I/O error occurs during file upload.
     */
    public String uploadFile(MultipartFile file, String pathName) throws IOException;

    /**
     * Retrieves a resource (file) as an {@link InputStream} from the specified path.
     *
     * @param pathName the path where the resource is stored.
     * @param imageName name of the file/resource to be retrieved.
     * @return an {@link InputStream} to access the resource.
     * @throws FileNotFoundException if the specified file/resource is not found at the given path.
     */
    public InputStream getResource(String pathName, String imageName) throws FileNotFoundException;

}
