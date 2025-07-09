import { useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router";

export function BoardEdit() {
  const { id } = useParams();
  
  useEffect(() => {
    axios
      .put(`/api/board/edit/${id}`)
      .then((res) => {
        console.log("수정됨");
      })
      .catch((err) => {
        console.log("수정안됨");
      })
      .finally(() => {
        console.log("항상");
      });
  }, []);

  return <h1>게시물 수정</h1>;
}
