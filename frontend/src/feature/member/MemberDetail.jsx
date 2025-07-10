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
  // 회원 정보는 수정, 삭제에 따라 바뀔 수 있으므로 state
  const [member, setMember] = useState(null);
  // 쿼리스트링을 통해 경로를 요청하므로 useSearchParams
  // Board랑 비교해서 다시 보기
  const [params] = useSearchParams();

  useEffect(() => {
    axios
      .get(`api/member?email=${params.get("email")}`)
      // .get(`api/member/{params.get("email")}`)
      // 이렇게 경로로 보내도 되지만 얘는 이메일을 보내는 거라
      // 특수 기호 등 뭐가 많은 문자열 이라서 위처럼 보내는 걸 추천
      // board edit 는 숫자만 보내느 거라서 경로로 보냈던 것!!
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
