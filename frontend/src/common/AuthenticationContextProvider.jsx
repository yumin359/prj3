import { createContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

// 유효기간을 넘긴 토큰 삭제 -> 우리 거에선 실행될 일 없대용 토큰 유용하게 설정 해둬서,,
const token = localStorage.getItem("token");
if (token) {
  const decoded = jwtDecode(token);
  const exp = decoded.exp;
  if (exp * 1000 < Date.now()) {
    // Date.now가 밀리세컨드 단위라서
    localStorage.removeItem("token");
  }
}

// axios interceptor
// token이 있으면 Authorization 헤더에 'Bearer token' 붙이기
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});
// 이렇게 하면 axios 요청마다 일일이 Authorization 헤더 안 넣어도 됨
// 어렵당..

// step1. create context
// AuthenticationContext 라는 context 객체 생성
// 얘는 단순히 전역 공유할 공간(통로)을 만든 것
const AuthenticationContext = createContext(null);

export function AuthenticationContextProvider({ children }) {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      const payload = jwtDecode(token);
      axios.get("/api/member?email=" + payload.sub).then((res) => {
        // email
        // nickName
        setUser({
          email: res.data.email,
          nickName: res.data.nickName,
        });
      });
    }
  }, []);

  // login
  function login(token) {
    localStorage.setItem("token", token);
    const payload = jwtDecode(token);
    axios.get("/api/member?email=" + payload.sub).then((res) => {
      // email
      // nickName
      setUser({
        email: res.data.email,
        nickName: res.data.nickName,
      });
    });
  }

  // logout
  function logout() {
    localStorage.removeItem("token");
    setUser(null);
  }

  // hasAccess
  // isAdmin

  // step3. provide context
  return (
    // AuthenticationContextProvider 컴포넌트 내부에서
    // 여러 메소드들을 만들고
    // AuthenticationContext(context 객체)라는 넘어갈 수 있는 통로를 통해
    // AuthenticationContext가 값을 보내줌
    <AuthenticationContext
      value={{
        user: user,
        login: login,
        logout: logout,
      }}
    >
      {children}
    </AuthenticationContext>
  );
}

export { AuthenticationContext };
