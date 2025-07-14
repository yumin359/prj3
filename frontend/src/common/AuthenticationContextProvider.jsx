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
    // 얘는 경로가 바뀌는 게 아니라 새로고침 할 때 한번 실행
    console.log("새로고침");
    // 마운트 될 때 실행
    // 각 브라우저마다 localStorage가 있는데 거기서 token을 가져옴
    const token = localStorage.getItem("token");
    if (token) {
      // 토큰을 가져오면
      // 디코더(복호화)를 통해 평문으로 만들어서
      const payload = jwtDecode(token);
      // 그 평문의 sub를 보내서 맞는지 확인하고? 값을 담음..?
      // 얘는 그냥 정보 하나보기 에서 값 받아온것!! 경로가 같아서용
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
    // 얘도 그냥 정보 하나보기 에서 값 받아온것!! 경로가 같아서용
    axios.get("/api/member?email=" + payload.sub).then((res) => {
      // 로그인 요청이 성공적으로 되면 user 라는 상태에
      // email
      // nickName
      // scope을 넣음
      setUser({
        email: res.data.email,
        nickName: res.data.nickName,
        scope: payload.scp.split(" "), // scope은 여러 값이 넘어올 수 있음
      });
    });
  }

  // logout
  function logout() {
    localStorage.removeItem("token");
    setUser(null);
  }

  // hasAccess
  // 로그인이 성공적으로 되면 그 정보에서
  // 본인 정보/글만 수정/삭제 등 가능하게 본인인지 확인하는 메소드
  function hasAccess(email) {
    return user && user.email === email;
  }

  // isAdmin
  // scope이 admin인지 확인하는 메소드
  function isAdmin() {
    return user && user.scope && user.scope.includes("admin");
  }

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
        hasAccess: hasAccess,
        isAdmin: isAdmin,
      }}
    >
      {children}
    </AuthenticationContext>
  );
}

export { AuthenticationContext };
