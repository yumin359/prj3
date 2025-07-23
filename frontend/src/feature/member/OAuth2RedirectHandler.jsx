import React, { useEffect, useContext } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { AuthenticationContext } from "../../common/AuthenticationContextProvider.jsx"; // 경로 확인!
import { toast } from "react-toastify";

export function OAuth2RedirectHandler() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useContext(AuthenticationContext);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token"); // URL 파라미터에서 'token' 값을 추출
    const error = params.get("error"); // 혹시 모를 에러 메시지 추출

    if (token) {
      login(token); // AuthenticationContext의 login 함수를 사용해 토큰을 저장
      // toast.success('구글 로그인 성공!'); // AuthenticationContextProvider에서 toast를 띄우므로 여기서는 제거
      navigate("/"); // 로그인 성공 후 메인 페이지로 이동
    } else if (error) {
      toast.error(`구글 로그인 실패: ${error}`);
      navigate("/login"); // 로그인 실패 시 로그인 페이지로 돌아가기
    } else {
      toast.warn("알 수 없는 구글 로그인 응답입니다.");
      navigate("/login");
    }
  }, [location, navigate, login]); // 의존성 배열에 location, navigate, login 포함

  return (
    <div>
      <p>구글 로그인 처리 중...</p>
    </div>
  );
}
