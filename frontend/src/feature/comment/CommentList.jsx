import { useEffect, useState } from "react";
import axios from "axios";
import { Button, Spinner } from "react-bootstrap";
import * as PropTypes from "prop-types";
import { toast } from "react-toastify";

function CommentItem({ comment, isProcessing, setIsProcessing }) {
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
      });
  }

  return (
    <div className="border m-3">
      <div className="d-flex justify-content-between m-3">
        <div>{comment.authorNickName}</div>
        <div>{comment.timesAgo}</div>
      </div>
      <div>{comment.comment}</div>
      <div>
        <Button disabled={isProcessing} onClick={handleDeleteButtonClick}>
          {isProcessing && <Spinner size="sm" />}삭제
        </Button>
        <Button>수정</Button>
      </div>
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
