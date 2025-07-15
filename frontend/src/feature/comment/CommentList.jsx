import { useEffect, useState } from "react";
import axios from "axios";
import {
  Button,
  FormControl,
  FormGroup,
  FormLabel,
  Modal,
  Spinner,
} from "react-bootstrap";
import * as PropTypes from "prop-types";
import { toast } from "react-toastify";

function CommentItem({ comment, isProcessing, setIsProcessing }) {
  const [deleteModalShow, setDeleteModalShow] = useState(false);
  const [editModalShow, setEditModalShow] = useState(false);
  const [nextComment, setNextComment] = useState(comment.comment);

  function handleDeleteButtonClick() {
    setIsProcessing(true);
    axios
      .delete(`/api/comment/${comment.id}`)
      .then((res) => {
        toast("댓글이 삭제 되었습니다.", { type: "success" });
      })
      .catch((err) => {
        toast("댓글 삭제 중 문제가 발생하였습니다.", { type: "error" });
      })
      .finally(() => {
        setIsProcessing(false);
        setDeleteModalShow(false);
      });
  }

  function handleUpdateButtonClick() {
    // TODO 댓글 수정
  }

  return (
    <div className="border m-3">
      <div className="d-flex justify-content-between m-3">
        <div>{comment.authorNickName}</div>
        <div>{comment.timesAgo}</div>
      </div>
      <div>{comment.comment}</div>
      <div>
        <Button
          disabled={isProcessing}
          onClick={() => setDeleteModalShow(true)}
        >
          {isProcessing && <Spinner size="sm" />}삭제
        </Button>
        <Button disabled={isProcessing} onClick={() => setEditModalShow(true)}>
          {isProcessing && <Spinner size="sm" />}수정
        </Button>
      </div>

      {/* 댓글 삭제 모달 */}
      <Modal show={deleteModalShow} onHide={() => setDeleteModalShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>댓글 삭제 확인</Modal.Title>
        </Modal.Header>
        <Modal.Body>댓글을 삭제하시겠습니까?</Modal.Body>
        <Modal.Footer>
          <Button
            variant="outline-dark"
            onClick={() => setDeleteModalShow(false)}
          >
            취소
          </Button>
          <Button
            disabled={isProcessing}
            variant="danger"
            onClick={handleDeleteButtonClick}
          >
            {isProcessing && <Spinner size="sm" />}
            삭제
          </Button>
        </Modal.Footer>
      </Modal>
      {/* 댓글 수정 모달 */}
      <Modal show={editModalShow} onHide={() => setEditModalShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>댓글 수정</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <FormGroup controlId={"commentTextarea1" + comment.id}>
            <FormLabel>수정할 댓글</FormLabel>
            <FormControl
              as="textarea"
              rows={5}
              value={nextComment}
              onChange={(e) => setNextComment(e.target.value)}
            />
          </FormGroup>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="outline-dark"
            onClick={() => {
              setNextComment(comment.comment); // 원문으로 돌아감
              setEditModalShow(false);
            }}
          >
            취소
          </Button>
          <Button variant="info" onClick={handleUpdateButtonClick}>
            수정
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}

export function CommentList({ boardId, isProcessing, setIsProcessing }) {
  const [commentList, setCommentList] = useState(null);

  // 브라우저에서 내가 스스로 새로고침 하는게 아니라
  // 처리가 끝났을 때 댓글 목록이 알아서 새로고침 되도록 함
  useEffect(() => {
    // useEffect는 isProcessing의 값이 바뀔 때마다 실행되지만
    // 조건문 때문에 axios는
    // !isProcessing => false 일 때만 실행됨
    if (!isProcessing) {
      axios
        .get(`/api/comment/board/${boardId}`)
        .then((res) => {
          setCommentList(res.data);
        })
        .catch((err) => {})
        .finally(() => {});
    }
  }, [isProcessing]);
  // 댓글 추가/수정/삭제 와 같이 어떤 일이 일어나고 있는지 상태를 dependency로 넣어서
  // 상태가 바뀔 때마다 실행됨
  // 비어있으면 새로고침해야 실행되고, 아예 []가 없으면 계속 실행됨
  // 계속 실행되면 요청을 계속 해서 나중에 아마존 거기서 요금 청구할 수도 있대용 ^^ 조심하기

  if (commentList === null) {
    return <Spinner />;
  }

  return (
    <div>
      {commentList.map((comment) => (
        <CommentItem
          setIsProcessing={setIsProcessing}
          isProcessing={isProcessing}
          comment={comment}
          key={comment.id}
        />
      ))}
    </div>
  );
}
