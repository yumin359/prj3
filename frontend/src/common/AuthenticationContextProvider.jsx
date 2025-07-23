import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import { toast } from "react-toastify";
import { AuthenticationContext } from "./AuthenticationContext.jsx"; // <-- 이 줄이 추가되어야 합니다.

// Axios Interceptor 설정
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

export function AuthenticationContextProvider({ children }) {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  // -------------------------------------------------------------------
  // 1. logout 함수를 loadUserFromToken 함수보다 먼저 정의합니다.
  // -------------------------------------------------------------------
  // 로그아웃 처리 함수
  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
    toast.info("로그아웃 되었습니다.");
    navigate("/login");
  };

  // -------------------------------------------------------------------
  // 2. 그 다음 loadUserFromToken 함수를 정의합니다.
  // -------------------------------------------------------------------
  // 사용자 정보를 로컬 스토리지에서 불러와 설정하는 내부 함수
  // 중복 코드를 줄이기 위해 분리
  const loadUserFromToken = (token) => {
    try {
      const payload = jwtDecode(token);

      if (payload.exp * 1000 < Date.now()) {
        console.log("토큰 만료됨. 로그아웃 처리.");
        toast.error("로그인 토큰이 만료되었습니다.");
        logout(); // 이제 logout 함수를 호출할 수 있습니다.
        return;
      }

      const userEmail = payload.sub; // JWT 'sub' 클레임을 이메일로 가정

      axios
        .get("/api/member?email=" + userEmail)
        .then((res) => {
          setUser({
            email: res.data.email,
            nickName: res.data.nickName,
            scope: res.data.scope ? res.data.scope.split(" ") : [],
            provider: res.data.provider,
          });
          console.log("사용자 정보 로드 성공:", res.data);
          toast.success("로그인 성공!");
        })
        .catch((err) => {
          console.error(
            "사용자 정보 로딩 실패:",
            err.response ? err.response.data : err.message,
          );
          toast.error("사용자 정보를 가져오는 데 실패했습니다.");
          logout(); // 이제 logout 함수를 호출할 수 있습니다.
        });
    } catch (e) {
      console.error("JWT 디코딩 또는 토큰 처리 실패:", e);
      toast.error("유효하지 않은 로그인 정보입니다.");
      logout(); // 이제 logout 함수를 호출할 수 있습니다.
    }
  };

  useEffect(() => {
    console.log("AuthenticationContextProvider: 마운트 시 토큰 확인");
    const storedToken = localStorage.getItem("token");
    if (storedToken) {
      loadUserFromToken(storedToken);
    }
  }, []);

  const login = (jwtToken) => {
    localStorage.setItem("token", jwtToken);
    loadUserFromToken(jwtToken); // 로그인 시에도 토큰으로부터 사용자 정보 로드
  };

  const hasAccess = (email) => {
    return user && user.email === email;
  };

  const isAdmin = () => {
    return (
      user &&
      user.scope &&
      (user.scope.includes("ROLE_ADMIN") || user.scope.includes("admin"))
    );
  };

  const contextValue = {
    user,
    login,
    logout, // Context value에 logout 함수도 포함
    hasAccess,
    isAdmin,
  };

  return (
    <AuthenticationContext.Provider value={contextValue}>
      {children}
    </AuthenticationContext.Provider>
  );
}
