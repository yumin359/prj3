import { Button, FloatingLabel, FormControl, Spinner } from "react-bootstrap";
import { useContext, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { AuthenticationContext } from "../../common/AuthenticationContextProvider.jsx";

export function CommentAdd({ boardId }) {
  const [comment, setComment] = useState("");
  const [isProcessing, setIsProcessing] = useState(false);

  const { user } = useContext(AuthenticationContext);

  function handleCommentSaveClick() {
    // 댓글 저장 버튼 여러 번 클릭되지 않게
    setIsProcessing(true);
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
      .finally(() => {
        // 댓글 저장 버튼 여러 번 클릭되지 않게
        setIsProcessing(false);
      });
  }

  // 내용없는 댓글 작성시 저장 버튼 비활성화
  let saveButtonDisabled = false;
  if (comment.trim().length === 0) {
    saveButtonDisabled = true;
  }

  return (
    <div>
      <FloatingLabel
        controlId="commentTextarea1"
        label={
          user === null
            ? "댓글을 작성하려면 로그인하세요."
            : "댓글을 작성해보세요."
        }
      >
        <FormControl
          placeholder={
            user === null
              ? "댓글을 작성하려면 로그인하세요."
              : "댓글을 작성해보세요."
          }
          as="textarea"
          style={{ height: "150px" }}
          value={comment}
          // 로그인 했을 때만 댓글창 활성화
          disabled={user === null}
          onChange={(e) => setComment(e.target.value)}
        />
      </FloatingLabel>
      {/*댓글 저장 버튼 여러 번 클릭되지 않게*/}
      <Button
        disabled={saveButtonDisabled || isProcessing}
        onClick={handleCommentSaveClick}
      >
        {isProcessing && <Spinner size="sm" />}
        댓글 저장
      </Button>
    </div>
  );
}
