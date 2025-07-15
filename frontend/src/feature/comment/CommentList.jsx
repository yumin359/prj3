import { useEffect, useState } from "react";
import axios from "axios";
import { Button, Spinner } from "react-bootstrap";
import * as PropTypes from "prop-types";
import { toast } from "react-toastify";

function CommentItem({ comment }) {
  function handleDeleteButtonClick() {
    axios
      .delete(`/api/comment/${comment.id}`)
      .then((res) => {
        toast("댓글이 삭제 되었습니다.", { type: "success" });
      })
      .catch((err) => {
        toast("댓글 삭제 중 문제가 발생하였습니다.", { type: "error" });
      })
      .finally(() => {});
  }

  return (
    <div className="border m-3">
      <div className="d-flex justify-content-between m-3">
        <div>{comment.authorNickName}</div>
        <div>{comment.timesAgo}</div>
      </div>
      <div>{comment.comment}</div>
      <div>
        <Button onClick={handleDeleteButtonClick}>삭제</Button>
        <Button>수정</Button>
      </div>
    </div>
  );
}

export function CommentList({ boardId, isProcessing }) {
  const [commentList, setCommentList] = useState(null);

  useEffect(() => {
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

  if (commentList === null) {
    return <Spinner />;
  }

  return (
    <div>
      {commentList.map((comment) => (
        <CommentItem comment={comment} key={comment.id} />
      ))}
    </div>
  );
}
