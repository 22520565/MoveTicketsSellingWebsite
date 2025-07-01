import React from "react";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { createOrder, callAccount } from "../config/api";

const OrderSuccessPage = () => {
  const navigate = useNavigate();
  useEffect(() => {
    document.title = "Đặt hàng thành công";
  }, []);
  useEffect(() => {
    const fetchUpdatedUser = async () => {
      const payload = JSON.parse(localStorage.getItem("checkoutPayload"));
      console.log("Order Success Payload:", payload);

      const currentUser = JSON.parse(localStorage.getItem("user"));
      if (currentUser?.id) {
        try {
          const updatedUserResponse = await callAccount();
          console.log(updatedUserResponse);

          if (updatedUserResponse) {
            localStorage.setItem("user", JSON.stringify(updatedUserResponse));
            console.log("Updated user in localStorage:", updatedUserResponse);
          }
        } catch (error) {
          console.error("Lỗi khi cập nhật user:", error);
        }
      }
    };

    fetchUpdatedUser();
  }, [navigate]);

  return (
    <div className="flex flex-col items-center justify-center h-screen">
      <div className="bg-white p-12 rounded-lg shadow-lg w-full max-w-lg text-center">
        <h2 className="text-3xl font-semibold text-green-500">
          Đơn hàng thành công!
        </h2>
        <p className="text-xl text-gray-600 mt-4">Cảm ơn bạn đã đặt hàng!</p>
        <div className="mt-8">
          <button
            onClick={() => navigate("/")}
            className="bg-blue-500 text-white py-3 px-8 rounded-lg hover:bg-blue-600 transition duration-300 text-xl"
          >
            Quay về trang chủ
          </button>
        </div>
      </div>
    </div>
  );
};

export default OrderSuccessPage;
