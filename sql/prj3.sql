# 게시물 테이블
CREATE TABLE board
(
    id          INT AUTO_INCREMENT NOT NULL,
    title       VARCHAR(300)       NOT NULL,
    content     VARCHAR(10000)     NOT NULL,
    author      VARCHAR(255)       NOT NULL,
    inserted_at datetime           NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_board PRIMARY KEY (id),
    FOREIGN KEY (author) REFERENCES member (email)
);

DROP TABLE board;

# 회원 테이블
CREATE TABLE member
(
    email       VARCHAR(255)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,
    nick_name   VARCHAR(255)  NOT NULL UNIQUE,
    info        VARCHAR(3000) NULL,
    inserted_at datetime      NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_member PRIMARY KEY (email)
);
ALTER TABLE member
    ADD COLUMN provider    VARCHAR(255) NULL,
    ADD COLUMN provider_id VARCHAR(255) NULL;
-- 필요하다면 unique 제약조건 추가 (provider와 provider_id 조합으로)
-- ALTER TABLE member ADD CONSTRAINT UQ_member_provider UNIQUE (provider, provider_id);
ALTER TABLE member
    MODIFY password VARCHAR(255) NULL;
-- 또는 'nullable'로 변경하는 DB 문법 사용
ALTER TABLE member
    MODIFY nick_name VARCHAR(255) NULL;
-- nickname도 null 가능하게.. 일단 함
ALTER TABLE member
    ADD COLUMN scope VARCHAR(255) NULL; -- 기본값은 NULL로 설정하여 기존 레코드에 영향이 없도록

DROP TABLE member;
SELECT *
FROM member;

# 권한 테이블
CREATE TABLE auth
(
    member_email VARCHAR(255) NOT NULL,
    auth_name    VARCHAR(255) NOT NULL,
    PRIMARY KEY (member_email, auth_name),
    FOREIGN KEY (member_email) REFERENCES member (email)
);
# 원래 crud 만들어야 하는데 그냥 이렇게 하겠음
INSERT INTO auth
    (member_email, auth_name)
VALUES ('trump@abc.com', 'admin');
SELECT *
FROM auth;

# 검색 테스트용 데이터
INSERT INTO board
    (title, content, author)
VALUES ('qwe', 'asd', '99@99.com'),
       ('zxc', '123', '88@88.com'),
       ('456', 'rty', '99@99.com'),
       ('fgh', 'vbn', '88@88.com'),
       ('789', 'uio', '99@99.com'),
       ('jkl', 'nmp', '88@88.com');

# 페이지 테스트용 데이터
INSERT INTO board
    (title, content, author)
SELECT title, content, author
FROM board;
SELECT count(*)
FROM board;
# 1536 개 만들었ㄷ숨

# 댓글 테이블
CREATE TABLE comment
(
    id          INT AUTO_INCREMENT NOT NULL,
    board_id    INT                NOT NULL,
    author      VARCHAR(255)       NOT NULL,
    comment     VARCHAR(2000)      NOT NULL,
    inserted_at datetime           NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_comment PRIMARY KEY (id),
    FOREIGN KEY (author) REFERENCES member (email),
    FOREIGN KEY (board_id) REFERENCES board (id)
);

#좋아요 테이블
CREATE TABLE board_like
(
    board_id     INT          NOT NULL,
    member_email VARCHAR(255) NOT NULL,
    PRIMARY KEY (board_id, member_email),
    FOREIGN KEY (board_id) REFERENCES board (id),
    FOREIGN KEY (member_email) REFERENCES member (email)
);

#파일 테이블
CREATE TABLE board_file
(
    board_id INT          NOT NULL,
    name     VARCHAR(300) NOT NULL,
    PRIMARY KEY (board_id, name),
    FOREIGN KEY (board_id) REFERENCES board (id)
);

CREATE TABLE member_test
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY, -- id: bigint 타입, 자동 증가, 기본 키
    email     VARCHAR(255) UNIQUE NOT NULL,      -- email: varchar, 고유(UNIQUE), NULL 허용 안함
    password  VARCHAR(255),                      -- password: varchar, NULL 허용 (OAuth2 사용자를 위해)
    nick_name VARCHAR(255)        NOT NULL,      -- nick_name: varchar, NULL 허용 안함
    provider  VARCHAR(50)         NOT NULL,      -- provider: varchar, NULL 허용 안함
    scope     VARCHAR(255)                       -- scope: varchar, NULL 허용
);

SELECT *
FROM member_test;