import { createContext, useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getEmployeeDetail } from "../config/api";
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [employeeDetail, setEmployeeDetail] = useState(null);
  const signIn = async (newToken, refreshToken, id) => {
    localStorage.setItem("accessToken", newToken);
    localStorage.setItem("refreshToken", refreshToken);
    localStorage.setItem("userId", id);
    await fetchEmployeeDetail();
  };

  const signOut = () => {
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("accessToken");
  };
  const fetchEmployeeDetail = async () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      return;
    }
    try {
      const response = await getEmployeeDetail();

      const data = response.data;

      setEmployeeDetail(data);
    } catch (error) {
      if (error.response) {
        alert(
          `Lấy thông tin nhân viên thất bại, lỗi: ` + error.response.data.msg
        );
        throw new Error(error.response.data.msg);
      } else if (error.request) {
        alert("Không nhận được phản hồi từ server");
        throw new Error(error.response.data.msg);
      } else {
        alert("Lỗi bất ngờ: " + error.message);
        throw new Error(error.response.data.msg);
      }
    }
  };
  return (
    <AuthContext.Provider
      value={{ signIn, signOut, employeeDetail, fetchEmployeeDetail }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
