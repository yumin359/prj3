import { Link, NavLink, useNavigate } from "react-router";
import {
  Button,
  Container,
  FormControl,
  InputGroup,
  Nav,
  Navbar,
  Form,
} from "react-bootstrap";
import { useContext, useState } from "react";
import { AuthenticationContext } from "./AuthenticationContextProvider.jsx";

export function AppNavBar() {
  const [keyword, setKeyword] = useState("");
  const { user, isAdmin } = useContext(AuthenticationContext);

  const navigate = useNavigate();

  function handleSearchFormSubmit(e) {
    e.preventDefault();
    // console.log("조회 폼 서브밋");
    navigate("/?q=" + keyword);
  }

  return (
    <div>
      <Navbar expand="lg" className="bg-body-tertiary">
        <Container>
          <Navbar.Brand to="/" as={Link}>
            PRJ3
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          {/* aria 어쩌구는 읽을 때 제공해주는 거라 우리한텐 필요없음 */}
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav>
              <Nav.Link as={NavLink} to="/">
                Home
              </Nav.Link>
              {user !== null && (
                <Nav.Link as={NavLink} to="/board/add">
                  새글
                </Nav.Link>
              )}
            </Nav>

            <Nav className="order-lg-3">
              {user === null && (
                <Nav.Link as={NavLink} to="/signup">
                  가입
                </Nav.Link>
              )}
              {/*{user !== null && (*/}
              {/*어드민일때만 네브바에 회원목록 나오도록*/}
              {isAdmin() && (
                <Nav.Link as={NavLink} to="/member/list">
                  회원 목록
                </Nav.Link>
              )}
              {/* Link로 이동하면 브라우저 url을 변경함 */}
              {user === null && (
                <Nav.Link as={NavLink} to="/login">
                  로그인
                </Nav.Link>
              )}
              {user !== null && (
                <Nav.Link as={NavLink} to="/logout">
                  로그아웃
                </Nav.Link>
              )}
              {user !== null && (
                <Nav.Link as={NavLink} to={`/member?email=${user.email}`}>
                  {user.nickName}
                </Nav.Link>
              )}
            </Nav>

            <Form
              inline="true"
              onSubmit={handleSearchFormSubmit}
              className="order-lg-2 mx-lg-auto"
            >
              <InputGroup>
                <FormControl
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                ></FormControl>
                <Button type="submit">검색</Button>
              </InputGroup>
            </Form>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </div>
  );
}
