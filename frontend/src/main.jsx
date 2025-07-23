import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import "bootstrap/dist/css/bootstrap.min.css";
import { ToastContainer } from "react-toastify";
import { BrowserRouter } from "react-router";
import { AuthenticationContextProvider } from "./common/AuthenticationContextProvider.jsx";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
      <AuthenticationContextProvider>
        <App />
      </AuthenticationContextProvider>
    </BrowserRouter>
    <ToastContainer />
  </StrictMode>,
);
