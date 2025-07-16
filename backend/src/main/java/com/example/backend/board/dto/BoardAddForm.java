package com.example.backend.board.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardAddForm {
    // react에서 보내는 이름대로 변수이름도 해준것
    private String title;
    private String content;
    private List<MultipartFile> files;
}
