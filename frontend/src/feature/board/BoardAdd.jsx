import { useContext, useState } from "react";
import { useNavigate } from "react-router";
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
import { AuthenticationContext } from "../../common/AuthenticationContextProvider.jsx";

export function BoardAdd() {
  // 제목, 본문, 작성자는 입력값에 따라 바뀌니까 state로 써줌
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  // const [author, setAuthor] = useState("");
  const { user } = useContext(AuthenticationContext);

  // 중복 저장 되는 것(버튼 여러번 눌리는 거)을 막기 위한 state
  const [isProcessing, setIsProcessing] = useState(false);

  // 링크 안 쓰고 코드로 경로 변경하기 위해 navigate 써줌
  const navigate = useNavigate();

  // 새로운 글 작성하고 저장 버튼 눌렀을 때
  function handleSaveButtonClick() {
    // 중복 저장 state true 하고
    setIsProcessing(true);
    axios
      .post("/api/board/add", {
        title: title,
        content: content,
        // author: author,
      })
      .then((res) => {
        // 응답 데이터 받아서 출력하기 (정상 응답 시 출력)
        const message = res.data.message;
        if (message) {
          // toast 띄우기 (alert 같은 거임)
          toast(message.text, { type: message.type });
        }
        // "/"로 이동 -> 글 작성하면 home 화면으로 이동
        navigate("/");
      })
      .catch((err) => {
        // 응답 데이터 받아서 출력하기 (비정상 응답 시 출력)
        const message = err.response.data.message;
        if (message) {
          // toast 띄우기 (alert 같은 거임)
          toast(message.text, { type: message.type });
        }
      })
      .finally(() => {
        console.log("항상 실행되는 코드");
        // 여기서 false로 바뀌면 중복 저장(중복 클릭) 안 됨.
        setIsProcessing(false);
      });
  }

  // 제목, 본문, 작성자, 썼는지 확인해서 -> 아래 버튼 주석 보기
  // 작성자는 이제 필요없으니까 지움
  let validate = true;
  if (title.trim() === "") {
    validate = false;
  }
  if (content.trim() === "") {
    validate = false;
  }

  return (
    <Row className="justify-content-center">
      <Col xs={12} md={8} lg={6}>
        <h2 className="mb-4">글 작성</h2>
        <div>
          <FormGroup className="mb-3" controlId="title1">
            <FormLabel>제목</FormLabel>
            <FormControl
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="content1">
            <FormLabel>본문</FormLabel>
            <FormControl
              as="textarea"
              rows={6}
              value={content}
              onChange={(e) => setContent(e.target.value)}
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="author1">
            <FormLabel>작성자</FormLabel>
            <FormControl
              value={user.nickName}
              disabled
              // value={author}
              // onChange={(e) => setAuthor(e.target.value)}
            />
          </FormGroup>
        </div>
        <div className="mb-3">
          <Button
            onClick={handleSaveButtonClick}
            disabled={isProcessing || !validate}
          >
            {/* !validate 즉 validate가 false 면 안 보이게 */}
            {/* 또는 isProcessing이 true이면(즉 저장되는 중인거지) 안 보이게 -> 중복 저장 방지가 됨 */}
            {isProcessing && <Spinner size="sm" />}
            {isProcessing || "저장"}
          </Button>
        </div>
      </Col>
    </Row>
  );
}
