import { Link, NavLink } from "react-router";
import { Container, Nav, Navbar } from "react-bootstrap";

export function AppNavBar() {
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
            <Nav className="me-auto">
              <Nav.Link as={NavLink} to="/">
                Home
              </Nav.Link>
              <Nav.Link as={NavLink} to="/board/add">
                새글
              </Nav.Link>
              <Nav.Link as={NavLink} to="/signup">
                가입
              </Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </div>
  );
}
