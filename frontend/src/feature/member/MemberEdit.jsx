import {
  Button,
  Col,
  FormControl,
  FormGroup,
  FormLabel,
  Row,
  Spinner,
} from "react-bootstrap";
import { useSearchParams } from "react-router";
import { useEffect, useState } from "react";
import axios from "axios";

export function MemberEdit() {
  const [member, setMember] = useState(null);

  const [params] = useSearchParams();

  useEffect(() => {
    axios
      .get(`/api/member?email=${params.get("email")}`)
      .then((res) => {
        console.log("good");
        setMember(res.data);
      })
      .catch((err) => {
        console.log("bad");
      })
      .finally(() => {
        console.log("always");
      });
  }, []);

  if (!member) {
    return <Spinner />;
  }

  return (
    <Row className="justify-content-center">
      <Col xs={12} md={8} lg={6}>
        <h2 className="mb-4">회원 정보 수정</h2>
        <div>
          <FormGroup controlId="email1" className="mb-3">
            <FormLabel>이메일</FormLabel>
            <FormControl readOnly value={member.email} />
          </FormGroup>
        </div>
        <div>
          <FormGroup controlId="nickName1" className="mb-3">
            <FormLabel>별명</FormLabel>
            <FormControl
              value={member.nickName}
              onChange={(e) =>
                setMember({ ...member, nickName: e.target.value })
              }
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup controlId="info1" className="mb-3">
            <FormLabel>자기소개</FormLabel>
            <FormControl
              as="textarea"
              value={member.info}
              onChange={(e) => setMember({ ...member, info: e.target.value })}
            />
          </FormGroup>
        </div>
        <div>
          <FormGroup controlId="inserted1" className="mb-3">
            <FormLabel>가입일시</FormLabel>
            <FormControl
              type="datetime-local"
              readOnly
              value={member.insertedAt}
            />
          </FormGroup>
        </div>
      </Col>
    </Row>
  );
}
