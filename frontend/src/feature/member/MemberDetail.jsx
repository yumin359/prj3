import {
  Button,
  Col,
  FormControl,
  FormGroup,
  FormLabel,
  Row,
  Spinner,
} from "react-bootstrap";
import { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useSearchParams } from "react-router";

export function MemberDetail() {
  const [member, setMember] = useState(null);
  const [params] = useSearchParams();

  useEffect(() => {
    axios
      .get(`api/member?email=${params.get("email")}`)
      .then((res) => {
        console.log("good");
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
        <h2 className="mb-4">회원 정보</h2>
        <div>
          <FormGroup className="mb-3" controlId="email1">
            <FormLabel>이메일</FormLabel>
            <FormControl readOnly value={member.email} />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="nickName1">
            <FormLabel>별명</FormLabel>
            <FormControl readOnly value={member.nickName} />
          </FormGroup>
        </div>
        <div>
          <FormGroup className="mb-3" controlId="info1">
            <FormLabel>자기소개</FormLabel>
            <FormControl as="textarea" rows={6} readOnly value={member.info} />
          </FormGroup>
        </div>
        <div>
          <Button variant="outline-danger" size="sm" className="me-2">
            회원 탈퇴
          </Button>
          <Button variant="outline-info">수정</Button>
        </div>
      </Col>
    </Row>
  );
}
