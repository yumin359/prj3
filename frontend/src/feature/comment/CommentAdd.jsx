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
      })
      .catch((err) => {
        const message = err.response.data.message;
        if (message) {
          toast(message.text, { type: message.type });
        }
      })
      .finally(() => {});
  }

  // TODO 댓글 비었을 때 저장 안 되게 -> 컨트롤러에서 막기

  // TODO 로그인 했을 때만 댓글 활성화
  // TODO 댓글 저장 버튼 여러 번 클릭되지 않게
  // TODO 댓글 저장 후에 textarea 비우기

  return (
    <div>
      <FormControl
        as="textarea"
        rows={3}
        value={comment}
        onChange={(e) => setComment(e.target.value)}
      />
      <Button onClick={handleCommentSaveClick}>댓글 저장</Button>
    </div>
  );
}
