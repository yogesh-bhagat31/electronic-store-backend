package com.learn.electronicstore.helper;

import com.learn.electronicstore.dtos.PageableResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class Helper {

    ModelMapper modelMapper;
    PageableResponse response;
    public <U, V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type) {
        List<U> users = page.getContent();
        List<V> dtoList = users.stream().map(object -> modelMapper.map(object, type)).collect(Collectors.toList());
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }
}
