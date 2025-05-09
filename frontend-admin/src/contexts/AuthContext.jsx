import { createContext, useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [employeeDetail, setEmployeeDetail] = useState(null);
  const signIn = async (newToken, id) => {
    localStorage.setItem("access_token", newToken);
    localStorage.setItem("userId", id);
    await fetchEmployeeDetail();
  };

  const signOut = () => {
    localStorage.removeItem("access_token");
    localStorage.removeItem("userId");
  };
  const fetchEmployeeDetail = async (id) => {
    const token = localStorage.getItem("access_token");
    if (!token) {
      return;
    }
    try {
      const id = localStorage.getItem("userId");
      console.log(id);

      const response = await axios.get(
        `http://localhost:8080/api/employees/${id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      console.log(response);
      const data = response.data.userResponseDto;
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
