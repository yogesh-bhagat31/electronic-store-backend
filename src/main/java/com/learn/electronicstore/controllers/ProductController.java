package com.learn.electronicstore.controllers;

import com.learn.electronicstore.dtos.*;
import com.learn.electronicstore.services.FileService;
import com.learn.electronicstore.services.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final FileService fileService;
    private final HttpServletResponse httpServletResponse;

    @Value("${product.profile.image.path}")
    private String imageUploadPath;


    public ProductController(ProductService productService, FileService fileService, HttpServletResponse httpServletResponse) {
        this.productService = productService;
        this.fileService = fileService;
        this.httpServletResponse = httpServletResponse;
    }


    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto productDto1 = productService.createProduct(productDto);
        return new ResponseEntity<>(productDto1, HttpStatus.CREATED);
    }


    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto, @PathVariable String productId) {
        ProductDto updateProduct = productService.updateProduct(productDto, productId);
        return new ResponseEntity<>(updateProduct, HttpStatus.OK);
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        ApiResponseMessage apiResponseMessage = ApiResponseMessage
                .builder()
                .message("Product deleted")
                .success(true)
                .httpStatus(HttpStatus.OK).build();
        return new ResponseEntity<>(apiResponseMessage, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<ApiResponseMessage> deleteAllProducts() {
        productService.deleteAllProducts();
        ApiResponseMessage apiResponseMessage = ApiResponseMessage
                .builder()
                .message("all products deleted")
                .success(true)
                .httpStatus(HttpStatus.OK).build();
        return new ResponseEntity<>(apiResponseMessage, HttpStatus.OK);
    }


    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String productId) {
        ProductDto productDto = productService.getProduct(productId);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                                       @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
                                                                       @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return new ResponseEntity<>(productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }


    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLiveProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                                           @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
                                                                           @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return new ResponseEntity<>(productService.getAllLiveProducts(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }


    @GetMapping("/title/{subTitle}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                                      @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
                                                                      @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir, @PathVariable String subTitle) {
        return new ResponseEntity<>(productService.searchByTitle(subTitle, pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);

    }

    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(@RequestParam("productImage") MultipartFile productImage, @PathVariable String productId) throws IOException {
        String imageName = fileService.uploadFile(productImage, imageUploadPath);
        ProductDto productDto = productService.getProduct(productId);
        productDto.setProductImageName(imageName);
        productService.updateProduct(productDto, productId);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).message("product image uploaded successfully").success(true).httpStatus(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    @GetMapping("/image/{productId}")
    public void serveProductImage(@PathVariable String productId, HttpServletResponse response) throws IOException {
        ProductDto productDto = productService.getProduct(productId);
        log.info("product image name: " + productDto.getProductImageName());
        InputStream resource = fileService.getResource(imageUploadPath, productDto.getProductImageName());
        httpServletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }


}
