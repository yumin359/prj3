import React, { useEffect, useContext } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { AuthenticationContext } from "../../common/AuthenticationContext.jsx";
import { toast } from "react-toastify";
import { Spinner, Container, Row, Col } from "react-bootstrap"; // 로딩 스피너를 위한 react-bootstrap 컴포넌트 추가

export function OAuth2RedirectHandler() {
  const navigate = useNavigate();
  const location = useLocation(); // 현재 URL의 정보를 담고 있습니다.
  const { login, logout } = useContext(AuthenticationContext); // logout 함수도 함께 가져옵니다.

  useEffect(() => {
    const params = new URLSearchParams(location.search); // URL 쿼리 파라미터를 파싱합니다.
    const token = params.get("token"); // 백엔드에서 전달하는 JWT 토큰
    const error = params.get("error"); // 백엔드에서 전달하는 에러 메시지 (선택 사항)

    if (token) {
      console.log("OAuth2RedirectHandler: JWT 토큰 수신 확인.");
      login(token); // AuthenticationContext의 login 함수를 호출하여 토큰 저장 및 사용자 정보 설정
      // `AuthenticationContextProvider` 내부의 `loadUserFromToken`에서 `toast.success`를 띄우므로 여기서는 별도로 띄우지 않습니다.
      navigate("/"); // 로그인 성공 후 메인 페이지로 이동
    } else if (error) {
      // 오류 메시지를 사용자에게 더 친숙하게 보여주기 위해 replace(/_/g, ' ') 적용
      console.error("OAuth2RedirectHandler: 오류 수신:", error);
      toast.error(`구글 로그인 실패: ${error.replace(/_/g, " ")}`);
      logout(); // 에러 발생 시 현재 로그인 상태를 확실히 클리어합니다. (예: 로컬 스토리지 토큰 삭제)
      navigate("/login"); // 로그인 실패 시 로그인 페이지로 돌아가기
    } else {
      // 토큰도 없고 에러도 없는 예상치 못한 상황
      console.warn(
        "OAuth2RedirectHandler: 토큰이나 에러 파라미터가 없습니다. 예상치 못한 응답.",
      );
      toast.warn("로그인 정보가 유효하지 않습니다. 다시 시도해 주세요.");
      navigate("/login"); // 이런 경우도 로그인 페이지로 돌려보냅니다.
    }
  }, [location, navigate, login, logout]); // 의존성 배열에 login, logout 포함

  return (
    // 로딩 상태를 사용자에게 시각적으로 보여주는 UI 추가
    <Container className="d-flex justify-content-center align-items-center min-vh-100">
      <Row>
        <Col className="text-center">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
          </Spinner>
          <p className="mt-3">
            구글 로그인 처리 중입니다. 잠시만 기다려 주세요...
          </p>
        </Col>
      </Row>
    </Container>
  );
}
