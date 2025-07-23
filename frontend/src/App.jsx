// import { BrowserRouter, Route, Routes } from "react-router";
// import { MainLayout } from "./common/MainLayout.jsx";
// import { BoardAdd } from "./feature/board/BoardAdd.jsx";
// import { BoardList } from "./feature/board/BoardList.jsx";
// import { BoardDetail } from "./feature/board/BoardDetail.jsx";
// import { BoardEdit } from "./feature/board/BoardEdit.jsx";
// import { MemberAdd } from "./feature/member/MemberAdd.jsx";
// import { MemberList } from "./feature/member/MemberList.jsx";
// import { MemberDetail } from "./feature/member/MemberDetail.jsx";
// import { MemberEdit } from "./feature/member/MemberEdit.jsx";
// import { MemberLogin } from "./feature/member/MemberLogin.jsx";
// import { MemberLogout } from "./feature/member/MemberLogout.jsx";
// import { AuthenticationContextProvider } from "./common/AuthenticationContextProvider.jsx";
// import { OAuth2RedirectHandler } from "./feature/member/OAuth2RedirectHandler.jsx";
//
// function App() {
//   return (
//     <AuthenticationContextProvider>
//       <BrowserRouter>
//         <Routes>
//           <Route path="/" element={<MainLayout />}>
//             <Route index element={<BoardList />} />
//             <Route path="board/add" element={<BoardAdd />} />
//             <Route path="board/:id" element={<BoardDetail />} />
//             <Route path="board/edit" element={<BoardEdit />} />
//             <Route path="signup" element={<MemberAdd />} />
//             {/* 일반 로그인 페이지 라우트 */}
//             <Route path="login" element={<MemberLogin />} />
//             {/* 구글 로그인 페이지 라우트 */}
//             <Route
//               path="/oauth2/redirect"
//               element={<OAuth2RedirectHandler />}
//             />
//             <Route path="logout" element={<MemberLogout />} />
//             <Route path="member/list" element={<MemberList />} />
//             <Route path="member" element={<MemberDetail />} />
//             <Route path="member/edit" element={<MemberEdit />} />
//             {/* 여기에 쓴 경로는 실제 브라우저에 보이는 url */}
//           </Route>
//         </Routes>
//       </BrowserRouter>
//     </AuthenticationContextProvider>
//   );
// }
//
// export default App;

import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthenticationContextProvider } from "./common/AuthenticationContextProvider.jsx"; // 경로 확인!
import { MemberLogin } from "./feature/member/MemberLogin.jsx"; // 또는 실제 경로
import { OAuth2RedirectHandler } from "./feature/member/OAuth2RedirectHandler.jsx"; // 또는 실제 경로
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css"; // 토스트 알림 CSS

function App() {
  return (
    <Router>
      {/* AuthenticationContextProvider로 전체 앱을 감싸서 전역 상태를 제공합니다. */}
      <AuthenticationContextProvider>
        <Routes>
          <Route path="/login" element={<MemberLogin />} />
          <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
          {/* 다른 라우트들 */}
          <Route path="/" element={<div>메인 페이지 (로그인 후 접근)</div>} />
          <Route path="*" element={<div>페이지를 찾을 수 없습니다.</div>} />
        </Routes>
      </AuthenticationContextProvider>
      <ToastContainer /> {/* 토스트 알림을 위한 컨테이너 */}
    </Router>
  );
}

export default App;
