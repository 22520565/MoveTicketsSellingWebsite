import axios from "axios";

const instance = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true, // nếu backend set cookie HttpOnly
});

const NO_RETRY_HEADER = "x-no-retry";

//  Request interceptor: thêm Authorization header
instance.interceptors.request.use(
  function (config) {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    if (!config.headers.Accept && config.headers["Content-Type"]) {
      config.headers.Accept = "application/json";
      config.headers["Content-Type"] = "application/json; charset=utf-8";
    }

    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

//  Response interceptor: handle refresh token nếu accessToken hết hạn
instance.interceptors.response.use(
  function (response) {
    return response.data;
  },
  async function (error) {
    const originalRequest = error.config;

    if (
      error?.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.headers[NO_RETRY_HEADER]
    ) {
      originalRequest._retry = true;

      try {
        // Gọi API refresh token
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) {
          throw new Error("No refresh token available");
        }

        const res = await axios.post(
          "http://localhost:8080/api/auth/customer/refresh-token",
          { token: refreshToken }
        );

        const newAccessToken = res.accessToken;
        localStorage.setItem("accessToken", newAccessToken);
        const newRefreshToken = res.refreshToken;
        localStorage.setItem("refreshToken", newRefreshToken);

        // Gán accessToken mới cho request gốc
        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;

        // Retry request cũ
        return instance(originalRequest);
      } catch (refreshError) {
        // Nếu refresh token fail → xoá token và redirect login
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");

        window.location.href = "/"; // Hoặc navigate nếu trong context React Router

        return Promise.reject(refreshError);
      }
    }

    return error?.response?.data || Promise.reject(error);
  }
);

export default instance;
