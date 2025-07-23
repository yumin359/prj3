import {
  Button,
  Col,
  FormControl,
  FormGroup,
  FormLabel,
  Row,
} from "react-bootstrap";
import { useContext, useState } from "react";
import { useNavigate } from "react-router";
import axios from "axios";
import { toast } from "react-toastify";
import { AuthenticationContext } from "../../common/AuthenticationContextProvider.jsx"; // 경로 확인!

export function MemberLogin() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false); // 일반 로그인 로딩 상태
  const { login } = useContext(AuthenticationContext);
  const navigate = useNavigate();

  function handleLogInButtonClick() {
    setIsLoading(true); // 요청 시작 시 로딩 상태 true

    axios
      .post("/api/member/login", {
        email: email,
        password: password,
      })
      .then((res) => {
        const token = res.data.token;
        login(token); // AuthenticationContextProvider의 login 메소드를 사용해 토큰 저장

        const message = res.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
        navigate("/"); // 로그인 성공 시 메인 페이지로 이동
      })
      .catch((err) => {
        const message = err.response.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        } else {
          toast.error("로그인 실패"); // 서버에서 메시지가 없을 경우 기본 에러 메시지
        }
      })
      .finally(() => {
        setIsLoading(false); // 요청 완료 시 로딩 상태 false
      });
  }

  function handleGoogleLogInButtonClick() {
    // 구글 로그인 버튼 클릭 시 백엔드의 Spring Security OAuth2Client 시작 엔드포인트로 리디렉션
    // 이 URL로 요청하면 Spring Security가 구글 인증 페이지로 사용자를 보냅니다.
    window.location.href = "/oauth2/authorization/google";
  }

  return (
    <Row className="justify-content-center">
      <Col xs={12} md={8} lg={6}>
        <h2 className="mb-4">로그인</h2>
        <FormGroup controlId="email1" className="mb-3">
          <FormLabel>이메일</FormLabel>
          <FormControl
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </FormGroup>
        <FormGroup controlId="password1" className="mb-3">
          <FormLabel>암호</FormLabel>
          <FormControl
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </FormGroup>
        <Button
          onClick={handleLogInButtonClick}
          disabled={isLoading}
          className="mb-2"
        >
          {isLoading ? "로그인 중..." : "로그인"}
        </Button>
        <br />
        <Button onClick={handleGoogleLogInButtonClick}>구글로 로그인</Button>
      </Col>
    </Row>
  );
}
