package com.example.backend.board.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardUpdateForm {
    private Integer id;
    private String title;
    private String content;

    private List<MultipartFile> files; // files[]로 넘어오니까 List로 받은 거
    //    private List<String> deleteFiles; // spring에서 어쩌구.. 아무튼 아래로 받겠대요
    private String[] deleteFiles;
}
