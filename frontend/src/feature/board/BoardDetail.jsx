import { useNavigate, useParams } from "react-router";
import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import {
  Button,
  Col,
  FormControl,
  FormGroup,
  FormLabel,
  Row,
  Spinner,
} from "react-bootstrap";

export function BoardDetail() {
  const [board, setBoard] = useState(null);

  const { id } = useParams();

  const navigate = useNavigate();

  useEffect(() => {
    // axios로 해당 게시물 가져오기
    axios
      .get(`/api/board/${id}`)
      .then((res) => {
        console.log("잘됨");
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

  if (!board) {
    return <Spinner />;
  }

  function handleDeleteButtonClick(id) {
    axios
      .delete(`/api/board/${id}`)
      .then((res) => {
        console.log("삭제됨");
        const message = res.data.message;
        // toast
        toast(message.text, { type: message.type });
        // "/"로 이동
        navigate("/");
      })
      .catch((err) => {
        console.log("삭제안됨");
      })
      .finally(() => {
        console.log("항상");
      });
  }

  function handleUpdateButtonClick(id) {
    // 게시물 수정 화면으로 이동
    navigate(`/board/edit/${id}`);
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
            <FormControl readOnly={true} value={board.author} />
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
        <div>
          <Button
            className="me-2"
            variant="outline-danger"
            onClick={() => handleDeleteButtonClick(board.id)}
          >
            삭제
          </Button>
          <Button
            variant="outline-info"
            onClick={() => handleUpdateButtonClick(board.id)}
          >
            수정
          </Button>
        </div>
      </Col>
    </Row>
  );
}
