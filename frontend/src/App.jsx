import axios from "axios";

function App() {
  function handleButton1Click() {
    axios.post("/api/learn/jwt/sub1", {
      email: "son@son.com",
      password: "son",
    });
  }

  return (
    <div>
      <h3>jwt 로그인 연습</h3>
      <button onClick={handleButton1Click}>1. token 얻기 (login)</button>
    </div>
  );
}

export default App;
