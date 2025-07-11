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
        localStorage.setItem("token", res.data);
      });
  }

  function handleButton2Click() {
    localStorage.removeItem("token");
  }

  function handleButton4Click() {
    // get, post, put, delete 다 상관 없음
    axios.get("/api/learn/jwt/sub2");
  }

  function handleButton3Click() {
    // localStorage에서 token 얻기
    const token = localStorage.getItem("token");

    if (token) {
      // 있으면 토큰 들고 요청
      // Authorization 헤더에 "Bearer "를 앞에 붙이고
      axios.get("/api/learn/jwt/sub2", {
        headers: {
          Authorization: "Bearer " + token,
        },
      });
    } else {
      // 없으면 토큰 안들고 요청
      axios.get("/api/learn/jwt/sub2");
    }
  }

  function handleButton5Click() {
    // 로그인 한 상태로 버튼 누르면 200
    // 로그인 안 한 상태로 버튼 누르면 401

    // localStorage에서 token 얻기
    const token = localStorage.getItem("token");

    if (token) {
      // 있으면 토큰 들고 요청
      // Authorization 헤더에 "Bearer "를 앞에 붙이고
      axios.get("/api/learn/jwt/sub3", {
        headers: {
          Authorization: "Bearer " + token,
        },
      });
    } else {
      // 없으면 토큰 안들고 요청
      axios.get("/api/learn/jwt/sub3");
    }
  }

  function handleButton8Click() {
    axios.get("/api/learn/jwt/sub8").then((res) => {
      const token = res.data;
      localStorage.setItem("token", token);
    });
  }

  function handleButton7Click() {
    axios.get("/api/learn/jwt/sub7").then((res) => {
      const token = res.data;
      localStorage.setItem("token", token);
    });
  }

  function handleButton6Click() {
    axios.get("/api/learn/jwt/sub6").then((res) => {
      const token = res.data;
      localStorage.setItem("token", token);
    });
  }

  function handleButton10Click() {
    axios.get("/api/learn/jwt/sub10", {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    });
  }

  function handleButton9Click() {
    axios.get("/api/learn/jwt/sub9", {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    });
  }

  return (
    <div>
      <h3>jwt 로그인 연습</h3>
      <button onClick={handleButton10Click}>
        10. manager만 허용 경로로 요청
      </button>
      <button onClick={handleButton9Click}>9. admin만 허용 경로로 요청</button>
      <button onClick={handleButton8Click}>8. manager 유저 로그인</button>
      <button onClick={handleButton7Click}>7. admin 유저 로그인</button>
      <button onClick={handleButton6Click}>6. 일반 유저 로그인</button>
      <button onClick={handleButton5Click}>
        5. isAuthenticated() 설정된 request handler method에 요청
      </button>
      <button onClick={handleButton4Click}>4. token 안 들고 요청</button>
      <button onClick={handleButton3Click}>3. token 들고 요청</button>
      <button onClick={handleButton2Click}>2. token 지우기 (logout)</button>
      <button onClick={handleButton1Click}>1. token 얻기 (login)</button>
    </div>
  );
}

export default App;
