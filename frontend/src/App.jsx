import axios from "axios";

function App() {
  function handleButton1Click() {
    axios
      .post("/api/learn/jwt/sub1", {
        email: "son@son.com",
        password: "son",
      })
      .then((res) => {
        console.log(res.data);
        localStorage.setItem("token", res.data.token);
      });
  }

  function handleButton2Click() {
    localStorage.removeItem("token");
  }

  return (
    <div>
      <h3>jwt 로그인 연습</h3>
      <button onClick={handleButton2Click}>2. token 지우기 (logout.)</button>
      <button onClick={handleButton1Click}>1. token 얻기 (login)</button>
    </div>
  );
}

export default App;
