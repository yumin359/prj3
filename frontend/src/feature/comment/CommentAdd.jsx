import { Button, FormControl } from "react-bootstrap";
import { useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";

export function CommentAdd({ boardId }) {
  const [comment, setComment] = useState("");

  function handleCommentSaveClick() {
    axios
      .post("/api/comment", {
        boardId: boardId,
        comment: comment,
      })
      .then((res) => {
        const message = res.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
        // 댓글 저장 후에 textarea 비우기
        setComment("");
      })
      .catch((err) => {
        const message = err.response.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
      })
      .finally(() => {});
  }

  // TODO 로그인 했을 때만 댓글 활성화
  // TODO 댓글 저장 버튼 여러 번 클릭되지 않게

  // 내용없는 댓글 작성시 저장 버튼 비활성화
  let saveButtonDisabled = false;
  if (comment.trim().length === 0) {
    saveButtonDisabled = true;
  }

  return (
    <div>
      <FormControl
        as="textarea"
        rows={3}
        value={comment}
        onChange={(e) => setComment(e.target.value)}
      />
      <Button disabled={saveButtonDisabled} onClick={handleCommentSaveClick}>
        댓글 저장
      </Button>
    </div>
  );
}
