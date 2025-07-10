import { Col, Row, Spinner, Table } from "react-bootstrap";
import { useEffect, useState } from "react";
import axios from "axios";

export function MemberList() {
  const [memberList, setMemberList] = useState(null);

  useEffect(() => {
    axios
      .get("/api/member/list")
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
        <h1>회원 목록</h1>
        <Table>
          <thead>
            <tr>
              <th>별명</th>
              <th>자기소개</th>
            </tr>
          </thead>
          <tbody>
            {memberList.map((member) => (
              <tr key={member.nickName}>
                <td>{member.nickName}</td>
                <td>{member.info}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      </Col>
    </Row>
  );
}
