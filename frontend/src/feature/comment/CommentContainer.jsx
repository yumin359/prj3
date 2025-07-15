import { Button, FormControl, Spinner } from "react-bootstrap";
import { useEffect, useState } from "react";
import axios from "axios";
import { CommentAdd } from "./CommentAdd.jsx";
import { CommentList } from "./CommentList.jsx";

export function CommentContainer({ boardId }) {
  const [isProcessing, setIsProcessing] = useState(false);
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
      <h3>댓글 ({commentList.length})</h3>

      <CommentAdd
        boardId={boardId}
        isProcessing={isProcessing}
        setIsProcessing={setIsProcessing}
      />
      <CommentList
        commentList={commentList}
        isProcessing={isProcessing}
        setIsProcessing={setIsProcessing}
      />
      {/* add에서 container로 올리고 container에서 list로 내려줘야함 */}
    </div>
  );
}
