import React, { createContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode"; // JWT 디코딩을 위한 라이브러리 (npm install jwt-decode)
import axios from "axios"; // Axios 라이브러리 (npm install axios)
import { toast } from "react-toastify"; // 토스트 알림 (npm install react-toastify)

// -------------------------------------------------------------
// Axios Interceptor 설정: 모든 Axios 요청에 JWT 토큰 자동 추가
// -------------------------------------------------------------
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// -------------------------------------------------------------
// AuthenticationContext 생성
// -------------------------------------------------------------
export const AuthenticationContext = createContext(null);

// -------------------------------------------------------------
// AuthenticationContextProvider 컴포넌트
// -------------------------------------------------------------
export function AuthenticationContextProvider({ children }) {
  const [user, setUser] = useState(null); // 로그인한 사용자 정보 (email, nickname, scope 등)
  const navigate = useNavigate();

  // 컴포넌트 마운트 시 로컬 스토리지에서 토큰 불러와 사용자 정보 설정
  useEffect(() => {
    console.log("AuthenticationContextProvider: 마운트 시 토큰 확인");
    const storedToken = localStorage.getItem("token");
    if (storedToken) {
      try {
        const payload = jwtDecode(storedToken);
        // 토큰 만료 여부 확인 (exp는 초 단위, Date.now()는 밀리초 단위)
        if (payload.exp * 1000 < Date.now()) {
          console.log("토큰 만료됨. 로그아웃 처리.");
          logout(); // 토큰 만료 시 로그아웃
          return;
        }

        // 토큰이 유효하면 사용자 정보 요청
        // 백엔드의 /api/member?email= 엔드포인트가 사용자 정보를 반환한다고 가정
        axios
          .get("/api/member?email=" + payload.sub)
          .then((res) => {
            setUser({
              email: res.data.email,
              nickName: res.data.nickName,
              scope: payload.scope ? payload.scope.split(" ") : [], // 'scope' 클레임 확인 (구글 클레임은 'scope'가 아니라 'scp'일 수 있음)
              provider: payload.provider, // OAuth 제공자 (google, local 등)
            });
            console.log("사용자 정보 로드 성공:", res.data);
          })
          .catch((err) => {
            console.error("사용자 정보 로딩 실패:", err);
            toast.error("사용자 정보를 가져오는 데 실패했습니다.");
            logout(); // 정보 로딩 실패 시 로그아웃
          });
      } catch (e) {
        console.error("JWT 디코딩 실패:", e);
        toast.error("유효하지 않은 로그인 정보입니다.");
        logout(); // 유효하지 않은 JWT면 로그아웃
      }
    }
  }, []); // 빈 배열: 컴포넌트가 처음 마운트될 때 한 번만 실행

  // 로그인 처리 함수 (JWT 토큰을 받아서 저장 및 사용자 정보 설정)
  const login = (jwtToken) => {
    localStorage.setItem("token", jwtToken);
    try {
      const payload = jwtDecode(jwtToken);
      // 로그인 시에도 토큰 유효성 재확인 (선택 사항이지만 안전)
      if (payload.exp * 1000 < Date.now()) {
        console.log("제공된 토큰이 이미 만료되었습니다.");
        toast.error("로그인 토큰이 만료되었습니다.");
        logout();
        return;
      }

      axios
        .get("/api/member?email=" + payload.sub)
        .then((res) => {
          setUser({
            email: res.data.email,
            nickName: res.data.nickName,
            scope: payload.scope ? payload.scope.split(" ") : [],
            provider: payload.provider,
          });
          toast.success("로그인 성공!");
        })
        .catch((err) => {
          console.error("로그인 후 사용자 정보 로딩 실패:", err);
          toast.error("로그인 후 사용자 정보를 가져오는 데 실패했습니다.");
          logout();
        });
    } catch (e) {
      console.error("로그인 시 JWT 디코딩 실패:", e);
      toast.error("로그인 정보가 유효하지 않습니다.");
      logout();
    }
  };

  // 로그아웃 처리 함수
  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
    toast.info("로그아웃 되었습니다.");
    navigate("/login"); // 로그아웃 후 로그인 페이지로 이동
  };

  // 특정 이메일과 현재 로그인한 사용자의 이메일이 같은지 확인
  const hasAccess = (email) => {
    return user && user.email === email;
  };

  // 현재 로그인한 사용자가 'admin' 권한을 가지고 있는지 확인
  const isAdmin = () => {
    return user && user.scope && user.scope.includes("admin");
  };

  // Context Provider가 제공할 값들
  const contextValue = {
    user, // 현재 로그인한 사용자 정보
    login, // 로그인 함수
    logout, // 로그아웃 함수
    hasAccess, // 접근 권한 확인 함수
    isAdmin, // 관리자 여부 확인 함수
    // 필요하다면 isLoading 등 다른 상태 추가 가능
  };

  return (
    <AuthenticationContext.Provider value={contextValue}>
      {children}
    </AuthenticationContext.Provider>
  );
}

// 이 컨텍스트를 다른 파일에서 import 할 수 있도록 export
// export { AuthenticationContext };
