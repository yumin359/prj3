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
import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams, useSearchParams } from "react-router";
import { toast } from "react-toastify";

export function MemberDetail() {
  // 회원 정보는 수정, 삭제에 따라 바뀔 수 있으므로 state
  const [member, setMember] = useState(null);
  const [modalShow, setModalShow] = useState(false);
  const [password, setPassword] = useState("");

  // 쿼리스트링을 통해 경로를 요청하므로 useSearchParams
  const [params] = useSearchParams();

  const navigate = useNavigate();

  useEffect(() => {
    axios
      .get(`api/member?email=${params.get("email")}`)
      // .get(`api/member/{params.get("email")}`)
      // 이렇게 경로로 보내도 되지만 얘는 이메일을 보내는 거라
      // 특수 기호 등 뭐가 많은 문자열 이라서 위처럼 보내는 걸 추천
      // board edit 는 숫자만 보내느 거라서 경로로 보냈던 것!!
      .then((res) => {
        console.log("good");
        setMember(res.data);
      })
      .catch((err) => {
        console.log("bad");
      })
      .finally(() => {
        console.log("always");
      });
  }, []);

  function handleDeleteButtonClick() {
    // 원래 delete는 ㅁㅁ
    axios
      .delete("/api/member", {
        data: { email: member.email, password: password },
      })
      .then((res) => {
        console.log("good");
        const message = res.data.message;
        toast(message.text, { type: message.type });
        navigate("/");
      })
      .catch((err) => {
        console.log("bad");
        const message = err.response.data.message;
        toast(message.text, { type: message.type });
      })
      .finally(() => {
        console.log("always");
        setModalShow(false);
        setPassword("");
      });
  }

  if (!member) {
    return <Spinner />;
  }

  return (
    <Row className="justify-content-center">
      <Col xs={12} md={8} lg={6}>
        <h2 className="mb-4">회원 정보</h2>
        <div>
          <FormGroup className="mb-3" controlId="email1">
            <FormLabel>이메일</FormLabel>
            <FormControl readOnly value={member.email} />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="nickName1">
            <FormLabel>별명</FormLabel>
            <FormControl readOnly value={member.nickName} />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="info1">
            <FormLabel>자기소개</FormLabel>
            <FormControl as="textarea" rows={6} readOnly value={member.info} />
          </FormGroup>
        </div>
        <div>
          <Button
            variant="outline-danger"
            size="sm"
            className="me-2"
            onClick={() => setModalShow(true)}
          >
            회원 탈퇴
          </Button>
          <Button variant="outline-info">수정</Button>
        </div>
      </Col>

      {/* 삭제 확인 모달 */}
      <Modal show={modalShow} onHide={() => setModalShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>회원 삭제 확인</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <FormGroup controlId="password1">
            <FormLabel>암호</FormLabel>
            <FormControl
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </FormGroup>
          {/*탈퇴하시겠습니까?*/}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-dark" onClick={() => setModalShow(false)}>
            {/* 모달이 닫힘 */}
            취소
          </Button>
          <Button variant="danger" onClick={handleDeleteButtonClick}>
            {/* 위의 메소드 실행되어 최종 탈퇴됨 */}
            탈퇴
          </Button>
        </Modal.Footer>
      </Modal>
    </Row>
  );
}
