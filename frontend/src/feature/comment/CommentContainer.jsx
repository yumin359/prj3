import { Button, FormControl } from "react-bootstrap";
import { useState } from "react";
import axios from "axios";
import { CommentAdd } from "./CommentAdd.jsx";
import { CommentList } from "./CommentList.jsx";

export function CommentContainer({ boardId }) {
  const [isProcessing, setIsProcessing] = useState(false);

  return (
    <div>
      <h3>댓글 창</h3>

      <CommentAdd
        boardId={boardId}
        isProcessing={isProcessing}
        setIsProcessing={setIsProcessing}
      />
      <CommentList
        boardId={boardId}
        isProcessing={isProcessing}
        setIsProcessing={setIsProcessing}
      />
      {/* add에서 container로 올리고 container에서 list로 내려줘야함 */}
    </div>
  );
}
