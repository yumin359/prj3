import {
  Button,
  Col,
  FormControl,
  FormGroup,
  FormLabel,
  FormText,
  Modal,
  Row,
  Spinner,
} from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router";
import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";

export function MemberEdit() {
  const [member, setMember] = useState(null);
  const [modalShow, setModalShow] = useState(false);
  const [password, setPassword] = useState("");
  const [passwordModalShow, setPasswordModalShow] = useState(false);
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword1, setNewPassword1] = useState("");
  const [newPassword2, setNewPassword2] = useState("");

  const [params] = useSearchParams();

  const navigate = useNavigate();

  useEffect(() => {
    axios
      .get(`/api/member?email=${params.get("email")}`)
      // 얘네는 요청 경로로, 실제 브라우저 경로(url)에 영향을 미치지 않음
      .then((res) => {
        console.log("good");
        setMember(res.data);
      })
      .catch((err) => {
        console.log("bad");
      })
      .finally(() => {
        console.log("always");
        setModalShow(false);
      });
  }, []);

  function handleSaveButtonClick() {
    axios
      .put(`/api/member`, { ...member, password: password })
      .then((res) => {
        console.log("success");
        const message = res.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
        navigate(`/member?email=${member.email}`);
      })
      .catch((err) => {
        console.log("error");
        const message = err.response.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
      })
      .finally(() => {
        console.log("always");
        setModalShow(false);
        setPassword("");
      });
  }

  // 암호 변경 버튼 활성화 여부
  let changePasswordButtonDisable = false;
  let passwordConfirm = true;

  if (oldPassword === "") {
    changePasswordButtonDisable = true;
  }
  if (newPassword1 === "") {
    changePasswordButtonDisable = true;
  }
  if (newPassword2 === "") {
    changePasswordButtonDisable = true;
  }
  if (newPassword1 !== newPassword2) {
    changePasswordButtonDisable = true;
    passwordConfirm = false;
  }

  function handleChangePasswordButtonClick() {
    axios
      .put("/api/member/changePassword", {
        email: member.email,
        oldPassword: oldPassword,
        newPassword: newPassword1,
      })
      .then((res) => {
        const message = res.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
      })
      .catch((err) => {
        const message = err.response.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
      })
      .finally(() => {
        setOldPassword("");
        setNewPassword1("");
        setNewPassword2("");
        setPasswordModalShow(false);
      });
  }

  if (!member) {
    return <Spinner />;
  }

  return (
    <Row className="justify-content-center">
      <Col xs={12} md={8} lg={6}>
        <h2 className="mb-4">회원 정보 수정</h2>
        <div>
          <FormGroup controlId="email1" className="mb-3">
            <FormLabel>이메일</FormLabel>
            <FormControl disabled value={member.email} />
          </FormGroup>
        </div>
        <div className="mb-4">
          <Button
            variant="outline-info"
            onClick={() => setPasswordModalShow(true)}
          >
            암호 변경
          </Button>
        </div>
        <div>
          <FormGroup controlId="nickName1" className="mb-3">
            <FormLabel>별명</FormLabel>
            <FormControl
              value={member.nickName}
              onChange={(e) =>
                setMember({ ...member, nickName: e.target.value })
              }
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup controlId="info1" className="mb-3">
            <FormLabel>자기소개</FormLabel>
            <FormControl
              as="textarea"
              value={member.info}
              onChange={(e) => setMember({ ...member, info: e.target.value })}
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup controlId="inserted1" className="mb-3">
            <FormLabel>가입일시</FormLabel>
            <FormControl
              type="datetime-local"
              disabled
              value={member.insertedAt}
            />
          </FormGroup>
        </div>
        <div>
          <Button
            className="me-2"
            variant="outline-secondary"
            onClick={() => navigate(-1)}
          >
            취소
          </Button>
          <Button variant="primary" onClick={() => setModalShow(true)}>
            저장
          </Button>
        </div>
      </Col>

      {/* 수정 확인 모달 */}
      <Modal show={modalShow} onHide={() => setModalShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>회원 정보 수정 확인</Modal.Title>
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
          {/*수정하시겠습니까?*/}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-dark" onClick={() => setModalShow(false)}>
            {/* 모달이 닫힘 */}
            취소
          </Button>
          <Button variant="primary" onClick={handleSaveButtonClick}>
            {/* 위의 메소드 실행되어 최종 탈퇴됨 */}
            저장
          </Button>
        </Modal.Footer>

        {/* 암호 변경 모달 */}
        {/*모달로 했지만 다른 페이지로 빼도 괜찮을 것 같아여*/}
      </Modal>
      <Modal
        show={passwordModalShow}
        onHide={() => setPasswordModalShow(false)}
      >
        <Modal.Header closeButton>
          <Modal.Title>암호 변경</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <FormGroup className="mb-3" controlId="password1">
            <FormLabel>현재 암호</FormLabel>
            <FormControl
              type="password"
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
            />
          </FormGroup>
          <FormGroup className="mb-3" controlId="password2">
            <FormLabel>변경할 암호</FormLabel>
            <FormControl
              type="password"
              value={newPassword1}
              onChange={(e) => setNewPassword1(e.target.value)}
            />
          </FormGroup>
          <FormGroup className="mb-3" controlId="password3">
            <FormLabel>변경할 암호 확인</FormLabel>
            <FormControl
              type="password"
              value={newPassword2}
              onChange={(e) => setNewPassword2(e.target.value)}
            />
            {passwordConfirm || (
              <FormText className="text-danger">
                패스워드가 일치하지 않습니다.
              </FormText>
            )}
          </FormGroup>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="outline-dark"
            onClick={() => setPasswordModalShow(false)}
          >
            {/* 모달이 닫힘 */}
            취소
          </Button>
          <Button
            disabled={changePasswordButtonDisable}
            variant="primary"
            onClick={handleChangePasswordButtonClick}
          >
            {/* 위의 메소드 실행되어 최종 탈퇴됨 */}
            변경
          </Button>
        </Modal.Footer>
      </Modal>
    </Row>
  );
}
