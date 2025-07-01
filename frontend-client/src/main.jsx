import "./index.css";
import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { ToastContainer } from "react-toastify";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import smoothscroll from "smoothscroll-polyfill";
import { AuthProvider } from "./Context/AuthContext";
import UpdateDocumentTitle from "./utils/UpdataDocumentTittle";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import RootLayout from "./layouts/RootLayout";
import ErrorPage from "./pages/ErrorPage";
import HomePage from "./pages/HomePage/HomePage";
import RulePage from "./pages/RulePage";
import LoginPage from "./pages/LoginPage/LoginPage";
import ForgotPasswordPage from "./pages/LoginPage/ForgotPassPage";
import SuccessPage from "./pages/LoginPage/SuccessPage";
import UserInfoLayout from "./layouts/UserSpaceLayout";
import UserInfoPage from "./pages/UserPage/UserInfor";
import UserChangePass from "./pages/UserPage/UserChangePass";
import UserTransHistory from "./pages/UserPage/UserTransHistory";
// import App from "./App.jsx";
import FilmDetailPage from "./pages/FilmDetailPage/FilmDetailPage";
import FilmShowingPage from "./pages/FilmShowingPage/FilmShowingPage";
import FilmUpComing from "./pages/FIlmUpComing/FilmUpComing";
import SearchPage from "./pages/SearchPage/SearchPage";
import ShowTimePage from "./pages/ShowTimePage/ShowTimePage";
import FoodPage from "./pages/FoodPage/FootPage";
import OrderSuccessPage from "./pages/OrderSuccess";
import OrderFailPage from "./pages/OrderFailed";
import CheckoutPage from "./pages/FilmDetailPage/CheckoutPage";
import TestScroll from "./Components/homePage/TestScroll";

smoothscroll.polyfill();
console.log("Smooth scroll polyfill applied");
// const stripePromise = loadStripe(
//   "pk_test_51RSroiFLS9qgPWZTC329aaYLG3kpwxs5dB7cICsPSiZqk58x3DU3X2oYHE4DmiqoeT1g9Sx48CThnIgH9fQ9bEwS00YI7hWxoQ"
// );

const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: "/",
        element: <HomePage />,
      },
      {
        path: "/test-scroll",
        element: <TestScroll />,
      },
      {
        path: "rule",
        element: <RulePage />,
      },
      {
        path: "auth",
        element: <LoginPage />,
      },
      {
        path: "auth/forgot-password",
        element: <ForgotPasswordPage />,
      },
      {
        path: "auth/success",
        element: <SuccessPage />,
      },
      {
        path: "/user/user-space",
        element: <UserInfoLayout />,
      },
      {
        path: "/user/user-space/infor",
        element: <UserInfoPage />,
      },
      {
        path: "/user/user-space/change-pass",
        element: <UserChangePass />,
      },
      {
        path: "/user/user-space/trans-history",
        element: <UserTransHistory />,
      },
      {
        path: "/movie/detail/:filmID",
        element: <FilmDetailPage />,
      },
      {
        path: "/movie/showing",
        element: <FilmShowingPage />,
      },
      {
        path: "/movie/upcoming",
        element: <FilmUpComing />,
      },
      {
        path: "/search",
        element: <SearchPage />,
      },
      {
        path: "/showtimes",
        element: <ShowTimePage />,
      },
      {
        path: "/food",
        element: <FoodPage />,
      },
      {
        path: "/order-success",
        element: <OrderSuccessPage />,
      },
      {
        path: "/order-failed",
        element: <OrderFailPage />,
      },
    ],
  },
  {
    path: "*",
    element: <ErrorPage />,
  },
]);
ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <AuthProvider>
      <RouterProvider router={router}>
        {/* Đặt UpdateDocumentTitle bên trong RouterProvider */}
        <UpdateDocumentTitle />
      </RouterProvider>
    </AuthProvider>
    <ToastContainer
      position="top-right"
      autoClose={3000}
      hideProgressBar={false}
      newestOnTop={false}
      closeButton={true}
      pauseOnHover={true}
      draggable={true}
      rtl={false}
      style={{
        fontSize: "18px",
        padding: "16px",
        minWidth: "300px",
      }}
    />
  </React.StrictMode>
);
