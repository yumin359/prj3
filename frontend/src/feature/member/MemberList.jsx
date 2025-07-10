import { useEffect, useState } from "react";
import { Col, Row, Spinner, Table } from "react-bootstrap";
import axios from "axios";
import { useNavigate } from "react-router";

export function MemberList() {
  // 회원 목록은 삭제, 수정, 추가 등으로 변경될 수 있으니 state
  const [memberList, setMemberList] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    axios
      .get("/api/member/list")
      // /api 서버(실제 요청 서버 주소는 vite.config에 있음)로 요청을 보내는 거
      // 얘네는 요청 경로로, 실제 브라우저 경로(url)에 영향을 미치지 않음
      .then((res) => {
        console.log("good");
        setMemberList(res.data);
      })
      .catch((err) => {
        console.log("bad");
      })
      .finally(() => {
        console.log("always");
      });
  }, []);

  if (!memberList) {
    return <Spinner />;
  }

  return (
    <Row>
      <Col>
        <h2 className="mb-4">회원 목록</h2>
        {memberList.length > 0 ? (
          <Table striped hover>
            <thead>
              <tr>
                <th>이메일</th>
                <th>별명</th>
                <th>가입일시</th>
              </tr>
            </thead>
            <tbody>
              {memberList.map((member) => (
                <tr
                  key={member.email}
                  style={{ cursor: "pointer" }}
                  onClick={() => navigate(`/member?email=${member.email}`)}
                >
                  {/* navigate나 Link 로 이동하면 브라우저 url이 변경됨 */}
                  {/*회원 정보 보기 경로는 쿼리스트링을 이용함*/}
                  <td>{member.email}</td>
                  <td>{member.nickName}</td>
                  <td>{member.insertedAt}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <p>회원이 없습니다.</p>
        )}
      </Col>
    </Row>
  );
}
