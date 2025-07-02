import { useEffect } from "react";
import { useNavigate } from "react-router";
import { useAuth } from "../contexts/AuthContext";
import axios from "axios";
import { validateJWT } from "../config/api";
export default function RedirectToRoot() {
  const navigate = useNavigate();
  const { fetchEmployeeDetail, signOut } = useAuth();
  useEffect(() => {
    const checkJWT = async () => {
      const token = localStorage.getItem("accessToken");
      console.log("access-token nè: ", token);

      if (!token) {
        navigate("/admin/auth");
      } else {
        try {
          const response = await validateJWT({
            // headers: {
            //   Authorization: `Bearer ${token}`,
            // },
          });
          const isValid = response.status === 200;
          if (!isValid) {
            alert("Xác thực người dùng không hợp lệ. Vui lòng đăng nhập lại");
            signOut();
          } else {
            await fetchEmployeeDetail();
            navigate("/admin");
          }
        } catch (error) {
          if (error.response) {
            alert(
              `Lấy thông tin nhân viên thất bại, lỗi: ` +
                error.response.data.msg
            );
            throw new Error(error.response.data.msg);
          } else if (error.request) {
            signOut();
            alert("Không nhận được phản hồi từ server");
            throw new Error(error.response);
          } else {
            alert("Lỗi bất ngờ: " + error.message);
            throw new Error(error.response);
          }
        }
      }
    };
    checkJWT();
  }, []);
  return <></>;
}
