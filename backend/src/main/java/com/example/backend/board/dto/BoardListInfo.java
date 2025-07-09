package com.example.backend.board.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

// 게시물 목록보기에서 보이게 할 것들을 인터페이스로
public interface BoardListInfo {
    Integer getId();

    String getTitle();

    String getAuthor();

    LocalDateTime getInsertedAt();

    // 원래는 위에 getInsertedAt으로 받아와서 날짜랑 시간이 다 보였는데
    // 아래와 같이 코드를 작성해서 작성 시간에 따라 다르게 보이게 함
    default String getTimesAgo() {
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
