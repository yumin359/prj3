import { useNavigate, useParams } from "react-router";
import { useContext, useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import {
  Button,
  Col,
  FormControl,
  FormGroup,
  FormLabel,
  Modal,
  Row,
  Spinner,
} from "react-bootstrap";
import { AuthenticationContext } from "../../common/AuthenticationContextProvider.jsx";
import { CommentContainer } from "../comment/CommentContainer.jsx";

export function BoardDetail() {
  // 게시물 하나는 바뀔 수 있으므로(삭제, 수정) state
  const [board, setBoard] = useState(null);
  // 삭제를 바로 하지 않게 modal을 띄울거라 state
  const [modalShow, setModalShow] = useState(false);

  const { hasAccess } = useContext(AuthenticationContext);

  // 게시물 하나보기는 /board/:id 즉 id 값만 바뀌는 경로이므로 useParams를 활용해서
  // 경로중에서 일부만 바뀌는 값 즉 id를 가져옴
  const { id } = useParams();

  // 코드로 경로 이동하기 위해 useNavigate 활용
  const navigate = useNavigate();

  useEffect(() => {
    // axios로 해당 게시물 가져오기 -> 마운트 될 때 실행
    axios
      .get(`/api/board/${id}`)
      .then((res) => {
        console.log("잘됨");
        // 정상적으로 받으면 게시물 하나에 대한 데이터들을 가져옴
        setBoard(res.data);
      })
      .catch((err) => {
        console.log("안됨");
        toast("해당 게시물이 없습니다.", { type: "warning" });
      })
      .finally(() => {
        console.log("항상");
      });
  }, []);

  // 모달안에 있는 삭제버튼 눌렀을 때 실행되는 메소드
  function handleDeleteButtonClick() {
    // axios로 해당 게시물 삭제 요청 보냄
    axios
      .delete(`/api/board/${id}`)
      .then((res) => {
        console.log("잘됨");
        const message = res.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
        navigate("/"); // 여기!!
      })
      .catch((err) => {
        console.log("안됨");
        toast("게시물이 삭제되지 않았습니다.", { type: "warning" });
      })
      .finally(() => {
        console.log("항상");
        // 위의 경로(여기!! 부분)로 이동하기 때문에 모달이 닫힌것처럼 보이지만
        // 아래 코드 작성 안 해주면 실제론 안 닫히므로 이것도 해줘야 함
        setModalShow(false);
      });
  }

  // 해당 게시물 하나가 없다면 스피너 돌아가도록
  if (!board) {
    return <Spinner />;
  }

  return (
    <Row className="justify-content-center">
      <Col xs={12} md={8} lg={6}>
        <h2 className="mb-4">{board.id}번 게시물</h2>
        <div>
          <FormGroup className="mb-3" controlId="title1">
            <FormLabel>제목</FormLabel>
            <FormControl readOnly={true} value={board.title} />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="content1">
            <FormLabel>본문</FormLabel>
            <FormControl
              as="textarea"
              rows={6}
              readOnly={true}
              value={board.content}
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="author1">
            <FormLabel>작성자</FormLabel>
            <FormControl
              readOnly={true}
              // value={board.author}
              value={board.authorNickName}
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="insertedAt1">
            <FormLabel>작성일시</FormLabel>
            <FormControl
              type="datetime-local"
              readOnly={true}
              value={board.insertedAt}
            />
          </FormGroup>
        </div>
        {hasAccess(board.authorEmail) && (
          <div>
            <Button
              className="me-2"
              variant="outline-danger"
              onClick={() => setModalShow(true)}
            >
              {/* 게시물 하나보기에서 삭제버튼 누르면 모달이 열리도록 state 변경 */}
              삭제
            </Button>
            <Button
              variant="outline-info"
              onClick={() => navigate(`/board/edit?id=${board.id}`)}
            >
              {/* 게시물 하나보기에서 수정버튼 누르면 수정화면 경로로 이동하도록 navigate 활용 */}
              수정
            </Button>
          </div>
        )}

        {/* 댓글 컴포넌트 */}
        <CommentContainer boardId={board.id} />
      </Col>

      {/* 위의 삭제 버튼에 따라 모달이 열림, onHide는 X 버튼 누르면 실행될 거 쓰는 거 -> 모달 닫히게 함 */}
      <Modal show={modalShow} onHide={() => setModalShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>게시물 삭제 확인</Modal.Title>
        </Modal.Header>
        <Modal.Body>{board.id}번 게시물을 삭제하겠습니까?</Modal.Body>
        <Modal.Footer>
          <Button variant="outline-dark" onClick={() => setModalShow(false)}>
            {/* 모달이 닫힘 */}
            취소
          </Button>
          <Button variant="danger" onClick={handleDeleteButtonClick}>
            {/* 위의 메소드 실행되어 최종 삭제됨 */}
            삭제
          </Button>
        </Modal.Footer>
      </Modal>
    </Row>
  );
}
