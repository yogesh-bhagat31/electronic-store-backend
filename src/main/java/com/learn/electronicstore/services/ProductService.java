package com.learn.electronicstore.services;

import com.learn.electronicstore.dtos.PageableResponse;
import com.learn.electronicstore.dtos.ProductDto;


public interface ProductService {

    //create
    ProductDto createProduct(ProductDto productDto);

    //update
    ProductDto updateProduct(ProductDto productDto, String productId);

    //delete
    void deleteProduct(String productId);

    //deleteAll
    void deleteAllProducts();

    //get single
    ProductDto getProduct(String productId);

    //ge all
    PageableResponse<ProductDto> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get all : live
    PageableResponse<ProductDto> getAllLiveProducts(int pageNumber, int pageSize, String sortBy, String sortDir);

    //search product
    PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir);
    //other methods


    // Create product with category
    ProductDto createWithCategory(ProductDto productDto, String categoryId);

    //Update prodcut with category this cateogory is already exist in db
    ProductDto updateProductWithCategory(String productId, String categoryId);

    PageableResponse<ProductDto> getAllOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);


}
