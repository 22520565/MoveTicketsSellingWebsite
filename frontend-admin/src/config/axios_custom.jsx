import axios from "axios";

const instance = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

const NO_RETRY_HEADER = "x-no-retry";

instance.interceptors.request.use(
  function (config) {
    if (
      config.url !== "/auth/employee/login" &&
      config.url !== "/auth/employee/register" &&
      window !== "undefined" &&
      window &&
      window.localStorage &&
      window.localStorage.getItem("accessToken")
    ) {
      config.headers.Authorization =
        "Bearer " + window.localStorage.getItem("accessToken");
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

// ✅ Response interceptor: handle refresh token
instance.interceptors.response.use(
  (response) => response, // Trả về response nếu thành công
  async (error) => {
    const originalRequest = error.config;

    // Kiểm tra nếu lỗi 401 và chưa retry
    if (error?.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/admin/auth";
        return Promise.reject(error);
      }

      try {
        // Gọi API refresh token
        const res = await axios.post(
          "http://localhost:8080/api/auth/employee/refresh-token",
          { refreshToken }
        );

        const { accessToken, refreshToken: newRefreshToken } = res.data;
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", newRefreshToken);

        // Gắn accessToken mới vào request gốc
        originalRequest.headers["Authorization"] = `Bearer ${accessToken}`;

        // Retry request gốc
        return instance(originalRequest);
      } catch (refreshError) {
        // Refresh token thất bại → xóa token, chuyển về login
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/admin/auth";
        return Promise.reject(refreshError);
      }
    }

    // Các lỗi khác trả về nguyên data (hoặc reject)
    return Promise.reject(error);
  }
);

// instance.interceptors.response.use(
//   function (response) {
//     return response.data;
//   },
//   async function (error) {
//     return error?.response?.data || Promise.reject(error);
//   }
// );

export default instance;
