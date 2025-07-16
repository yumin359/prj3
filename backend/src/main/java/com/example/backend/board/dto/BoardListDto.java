package com.example.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

// 게시물 목록보기 dto 이거로 바꿈
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardListDto {
    private Integer id;
    private String title;
    private String nickName;
    private LocalDateTime insertedAt;
    private Long countComment; // 게시물에 댓글 몇 개인지
    private Long countLike;
    private Long countFile;

    public String getTimesAgo() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime insertedAt = this.getInsertedAt();

        Duration duration = Duration.between(insertedAt, now);

        long seconds = duration.toSeconds();
        if (seconds < 60) {
            return "방금 전";
        } else if (seconds < 60 * 60) { // 1 hour
            long minutes = seconds / 60;
            return minutes + "분 전";
        } else if (seconds < 60 * 60 * 24) { // 1 day
            long hours = seconds / 3600;
            return hours + "시간 전";
        } else if (seconds < 60 * 60 * 24 * 7) { // 1 week
            long days = seconds / 3600 / 24;
            return days + "일 전";
        } else if (seconds < 60 * 60 * 24 * 7 * 4) { // 4 weeks
            long weeks = seconds / 3600 / 24 * 7;
            return weeks + "주 전";
        } else {
            long days = duration.toDays();
            long years = days / 365;
            return years + "년 전";
        }
    }
}
