package com.example.backend.board.service;

import com.example.backend.board.dto.*;
import com.example.backend.board.entity.Board;
import com.example.backend.board.entity.BoardFile;
import com.example.backend.board.entity.BoardFileId;
import com.example.backend.board.repository.BoardFileRepository;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.comment.repository.CommentRepository;
import com.example.backend.like.repository.BoardLikeRepository;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final BoardFileRepository boardFileRepository;
    private final BoardLikeRepository boardLikeRepository;

    // 게시물 작성 Create
    public void add(BoardAddForm dto, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("권한이 없습니다");
        }

        // entity에 dto의 값들 옮겨 담고
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        // 로그인 한 사용자의 email이 게시물 작성자에 들어감
//        board.setAuthor(authentication.getName());
        // author를 member 테이블과 외래키 해서 이것도 바꿔줌
        Member author = memberRepository.findById(authentication.getName()).get();
        board.setAuthor(author);

        // repository에 save 실행
        boardRepository.save(board);

        // file 저장하기
        saveFiles(board, dto.getFiles());
    }

    private void saveFiles(Board board, List<MultipartFile> files) {
//        List<MultipartFile> files = dto.getFiles();
        if (files != null && files.size() > 0) {
            for (MultipartFile file : files) {
                if (file != null && file.getSize() > 0) {
                    // board_file 테이블에 새 레코드 입력
                    BoardFile boardFile = new BoardFile();
                    // entity 내용 채우기
                    BoardFileId id = new BoardFileId();
                    id.setBoardId(board.getId());
                    id.setName(file.getOriginalFilename());
                    boardFile.setBoard(board);
                    boardFile.setId(id);

                    // repository로 저장
                    boardFileRepository.save(boardFile);

                    // 실제 파일 disk에 저장
                    // todo : aws s3 에 저장을 변경할 예정
                    // 1. C:/Temp/prj3/boardFile 에 게시물 번호 폴더 만들고
                    /// C:/Temp/prj3/boardFile/2002
                    File folder = new File("C:/Temp/prj3/boardFile/" + board.getId());
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }


                    // 2. 그 폴더에 파일 저장
                    /// C:/Temp/prj3/boardFile/2002/tiger.jpg
                    try {

                        BufferedInputStream bi = new BufferedInputStream(file.getInputStream());
                        BufferedOutputStream bo
                                = new BufferedOutputStream(new FileOutputStream(new File(folder, file.getOriginalFilename())));

                        try (bi; bo) {
                            byte[] b = new byte[1024];
                            int len;
                            while ((len = bi.read(b)) != -1) {
                                bo.write(b, 0, len);
                            }
                            bo.flush();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }


                }
            }
        }
    }

    // 제목, 본문, 작성자 중에 하나라도 안 써지면 false 를 리턴 : 값들이 유효한지 확인하는 메소드
    public boolean validate(BoardDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            return false;
        }
        if (dto.getContent() == null || dto.getContent().trim().isBlank()) {
            return false;
        }
        // 작성자 확인은 필요 없어서 지움
        return true;
    }

    // 게시물 목록 보기 Read(list) + 검색
//    public List<BoardListInfo> list() {
    public Map<String, Object> list(String keyword, Integer pageNumber) {
//        return boardRepository.findAllByOrderByIdDesc();
        Page<BoardListDto> boardListDtoPage
                = boardRepository.findAllBy(keyword, PageRequest.of(pageNumber - 1, 10));

        int totalPages = boardListDtoPage.getTotalPages(); // 마지막 페이지
        int rightPageNumber = ((pageNumber - 1) / 10 + 1) * 10; // 오른쪽 페이지
        int leftPageNumber = rightPageNumber - 9; // 왼쪽 페이지
        rightPageNumber = Math.min(rightPageNumber, totalPages); // 오른쪽 페이지는 마지막 페이지보다 클 수 없음
        leftPageNumber = Math.max(leftPageNumber, 1); // 왼쪽 페이지는 1페이지보다 작을 수 없음

        var pageInfo = Map.of("totalPages", totalPages,
                "rightPageNumber", rightPageNumber,
                "leftPageNumber", leftPageNumber,
                "currentPageNumber", pageNumber);

        return Map.of("pageInfo", pageInfo,
                "boardList", boardListDtoPage.getContent());
    }

    // 게시물 하나 보기 Read(one)
    public BoardDto getBoardById(Integer id) {
        BoardDto board = boardRepository.findBoardById(id);
        List<BoardFile> fileList = boardFileRepository.findByBoardId(id);
        List<BoardFileDto> files = new ArrayList<>();
        for (BoardFile boardFile : fileList) {
            BoardFileDto fileDto = new BoardFileDto();
            fileDto.setName(boardFile.getId().getName());
            // 강사님은 8081로 뜨셔서
            // 내일은 aws에 s3 거기서 하는거로 한대용 -> 경로는 바뀜
            fileDto.setPath("http://localhost:8080/boardFile/" + id + "/" + boardFile.getId().getName());
            files.add(fileDto);
        }

        board.setFiles(files);

//        BoardDto boardDto = new BoardDto();
//        boardDto.setId(board.getId());
//        boardDto.setTitle(board.getTitle());
//        boardDto.setContent(board.getContent());
//        boardDto.setAuthor(board.getAuthor());
//        boardDto.setInsertedAt(board.getInsertedAt());
        return board;
    }

    // 게시물 삭제 Delete + 거기에 있는 댓글들이 먼저 삭제되어야 함 + 좋아요도 지우고 파일도 지우고
    public void deleteById(Integer id, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("권한이 없습니다.");
        }

        Board db = boardRepository.findById(id).get();

        if (db.getAuthor().getEmail().equals(authentication.getName())) {
            // 좋아요 삭제
            boardLikeRepository.deleteByBoard(db);

            // 파일 삭제
            boardFileRepository.deleteByBoard(db);

            // 댓글들 먼저 삭제
            commentRepository.deleteByBoardId(id);

            boardRepository.deleteById(id);
        } else {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    // 게시물 수정(갱신) Update
    public void update(BoardUpdateForm boardDto, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 조회
        Board db = boardRepository.findById(boardDto.getId()).get();

        if (db.getAuthor().getEmail().equals(authentication.getName())) {
            // 변경
            db.setTitle(boardDto.getTitle());
            db.setContent(boardDto.getContent());
            // 작성자는 정해져있으니까(수정할 필요 없으니까) 지움
//            db.setAuthor(boardDto.getAuthor());

            // 파일 지우기
            deleteFiles(db, boardDto.getDeleteFiles());

            // 파일 추가
            saveFiles(db, boardDto.getFiles());

            // 저장
            boardRepository.save(db);
        } else {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    private void deleteFiles(Board db, String[] deleteFiles) {
        if (deleteFiles != null && deleteFiles.length > 0) {
            for (String file : deleteFiles) {
                // board_file table의 record 지우고
                BoardFileId boardFileId = new BoardFileId();
                boardFileId.setBoardId(db.getId());
                boardFileId.setName(file);
                boardFileRepository.deleteById(boardFileId);

                // C:/Temp/prj3/boardFile/2324/tiger.jpg 지우고
                File targetFile = new File("C:/Temp/prj3/boardFile/" + db.getId() + "/" + file);
                if (targetFile.exists()) {
                    targetFile.delete();
                }
            }
        }
    }

    // 제목이랑 본문 있는지 확인
    public boolean validateForAdd(BoardAddForm dto) {

        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            return false;
        }

        if (dto.getContent() == null || dto.getContent().trim().isBlank()) {
            return false;
        }

        return true;
    }

    public boolean validateForUpdate(BoardUpdateForm boardDto) {
        if (boardDto.getTitle() == null || boardDto.getTitle().trim().isBlank()) {
            return false;
        }

        if (boardDto.getContent() == null || boardDto.getContent().trim().isBlank()) {
            return false;
        }

        return true;
    }
}
