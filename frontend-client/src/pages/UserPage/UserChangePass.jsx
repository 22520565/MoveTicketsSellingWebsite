import React from "react";
import { useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import { useAuth } from "../../Context/AuthContext";
import UserChangePassComponent from "../../Components/UserChangePassComponent";
import UserInfoLayout from "../../layouts/UserSpaceLayout";
import { changePasword } from "../../config/api";
import { toast } from "react-toastify";
const UserChangePass = () => {
  const { state } = useLocation(); // Lấy thông tin từ state
  const [fields, setFields] = useState([]);
  const { user } = useAuth();
  const handleSave = async (formValues) => {
    const { oldPassword, newPassword, confirmNewPassword } = formValues;

    if (newPassword !== confirmNewPassword) {
      toast.error("Mật khẩu mới và xác nhận mật khẩu không khớp");
      return;
    }

    try {
      const response = await changePasword({
        oldPassword,
        newPassword,
      });

      console.log("Change Password Response:", response);

      toast.success("Đổi mật khẩu thành công");
    } catch (error) {
      console.error("Change Password Error:", error);
      const errorMessage =
        error?.response?.data?.message || "Đổi mật khẩu thất bại";
      toast.error(errorMessage);
    }
  };
  // const convertDateToISO = (date) => {
  //   const [day, month, year] = date.split("/"); // Nếu date có định dạng DD/MM/YYYY
  //   return `${year}-${month}-${day}`; // Chuyển sang YYYY-MM-DD
  // };

  useEffect(() => {
    document.title = "Đổi mật khẩu";
    const updateFields = () => {
      const userInfo = state?.userInfo || {};
      console.log(userInfo);

      const updatedFields = [
        {
          for: "oldPassword",
          text: "Nhập mật khẩu cũ",
          type: "password",
          required: true,
        },
        {
          for: "newPassword",
          text: "Nhập mật khẩu mới",
          type: "password",
          required: true,
        },
        {
          for: "confirmNewPassword",
          text: "Xác nhận mật khẩu mới",
          type: "password",
          required: true,
        },
      ];

      setFields(updatedFields);
    };
    updateFields();
  }, [state]);

  return (
    <UserInfoLayout>
      <div className="flex w-full flex-col ">
        <div className=" text-2xl text-white"></div>
        {/* Main Content */}
        <UserChangePassComponent
          title="Đổi mật khẩu"
          layout="row"
          fields={fields}
          buttontitle="Đổi mật khẩu"
          onSubmit={handleSave}
        />
      </div>
    </UserInfoLayout>
  );
};

export default UserChangePass;
