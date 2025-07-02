const calculateTotalAfterDiscount = (
  totalPrice,
  totalDiscount,
  pointUsage,
  param
) => {
  if (!param) return totalPrice;

  let discountedPrice = Math.floor(
    totalPrice - (totalPrice * totalDiscount) / 100
  );

  if (pointUsage && param?.loyalPoint_PointToReducedPriceRatio) {
    discountedPrice -= Math.floor(
      (pointUsage * param.loyalPoint_PointToReducedPriceRatio) / 100
    );
  }

  return discountedPrice < 0 ? 0 : discountedPrice;
};

import React, { useEffect, useState } from "react";
import { loadStripe } from "@stripe/stripe-js";
import CustomButton from "../button/index"; // Giả sử bạn đã có CustomButton component
import {
  createPaymentStripe,
  getCurrentPoint,
  getParam,
} from "../../config/api"; // Đảm bảo các API được định nghĩa đúng
import { useAuth } from "../../Context/AuthContext"; // Dùng context cho user
import { useNavigate } from "react-router-dom";

const PaymentSection = ({
  selectedFood,
  selectedPromotions,
  totalDiscount,
}) => {
  const [isLoading, setIsLoading] = useState(false); // State quản lý trạng thái loading
  const [paymentUrl, setPaymentUrl] = useState(null); // State quản lý URL thanh toán
  const [loyalPoint, setLoyalPoint] = useState(0); // Điểm tích lũy hiện tại
  const [param, setParam] = useState(null); // Tham số từ hệ thống
  console.log("🚀 ~ param:", param);
  const [usePoints, setUsePoints] = useState(false); // Sử dụng điểm
  const [pointUsage, setPointUsage] = useState(null); // Số điểm sẽ sử dụng

  const { user } = useAuth(); // Lấy thông tin user từ context
  const navigate = useNavigate();

  const totalPrice = selectedFood.reduce(
    (sum, food) => sum + food.quantity * food.price,
    0
  );

  const additionalItems = selectedFood.map((food) => {
    return {
      id: food.id,
      quantity: food.quantity,
    };
  });

  // Lấy danh sách ID từ selectedPromotions
  const promotionIds = selectedPromotions.map((promo) => promo.id);

  const handleCreatePayment = async () => {
    setIsLoading(true);
    try {
      if (!localStorage.getItem("accessToken")) {
        alert("Bạn cần phải đăng nhập trước khi thực hiện thanh toán");
        navigate("/auth");
        return;
      }

      const payload = {
        tickets: [], // Không có vé
        seatIds: [], // Không có ghế
        filmShowId: null, // Không chọn suất chiếu
        items: additionalItems
          .filter((item) => item.quantity > 0)
          .map((item) => ({
            id: item.id,
            quantity: item.quantity,
          })),
        promotionIds: promotionIds ?? [],
        loyalPoint: 0,
        pointUsage: usePoints ? pointUsage : 0,
        totalPrice: totalPrice, // hoặc 0 nếu để backend tính
        totalPriceAfterDiscount: totalAfterDiscount, // hoặc tính nếu cần
      };

      console.log("Payload đầy đủ gửi backend:", payload);
      localStorage.setItem("checkoutPayload", JSON.stringify(payload));

      const response = await createPaymentStripe(payload);

      const sessionId = response?.clientSecret;

      if (!sessionId) {
        alert("Không thể tạo phiên thanh toán. Vui lòng thử lại.");
        return;
      }

      const stripe = await loadStripe(
        "pk_test_51RSroiFLS9qgPWZTC329aaYLG3kpwxs5dB7cICsPSiZqk58x3DU3X2oYHE4DmiqoeT1g9Sx48CThnIgH9fQ9bEwS00YI7hWxoQ"
      );

      await stripe.redirectToCheckout({ sessionId });
    } catch (error) {
      console.error("Lỗi khi tạo thanh toán:", error);
      alert("Có lỗi xảy ra. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const currentUser = JSON.parse(localStorage.getItem("user"));
        if (currentUser) {
          setLoyalPoint(currentUser.loyalPoint);
        }

        const paramResponse = await getParam();
        console.log(paramResponse);

        if (paramResponse) {
          setParam(paramResponse);
        } else {
          console.error("Invalid paramResponse:", paramResponse);
        }
      } catch (error) {
        console.error("Error in fetchData:", error);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    if (!param) return;
    if (!usePoints) {
      setPointUsage(null);
      return;
    }
    const data = Math.min(
      totalPrice -
        (totalPrice * totalDiscount) /
          100 /
          param?.loyalPointPointToReducedPriceRatio,
      param?.loyalPointMaximumPointUseInOneGo
    );

    const calculatedPointUsage = Math.min(data, loyalPoint);

    setPointUsage(calculatedPointUsage);
  }, [usePoints, totalPrice, totalDiscount, param]);

  const handleTogglePoints = () => {
    if (usePoints === false && loyalPoint === 0) {
      alert(`Bạn không có điểm để sử dụng`);
      return;
    }

    if (
      !usePoints &&
      totalPrice < param?.loyalPointMinimumValueToUseLoyalPoint
    ) {
      alert(
        `Để có thể sử dụng điểm tích lũy, đơn hàng tối thiểu phải là: ${param?.loyalPointMinimumValueToUseLoyalPoint.toLocaleString()} VNĐ`
      );
      return;
    } else if (
      loyalPoint > param?.loyalPointMaximumPointUseInOneGo &&
      !usePoints
    ) {
      alert(
        `Điểm sử dụng tối đa trong một lần là ${param?.loyalPointMaximumPointUseInOneGo}. Phần dư ra có thể được sử dụng lại cho lần sau.`
      );
    }
    setUsePoints(!usePoints);
  };

  const totalAfterDiscount = calculateTotalAfterDiscount(
    totalPrice,
    totalDiscount,
    pointUsage,
    param
  );

  return (
    <>
      {additionalItems.length > 0 && (
        <div className="flex justify-between items-center px-24 bg-[#0f172a] text-white sticky bottom-0 py-4">
          {/* Phần Hóa đơn */}
          <div className="flex flex-col items-start max-w-xl w-full">
            <h1 className="text-3xl font-bold">HÓA ĐƠN</h1>
            {selectedFood.map((food) => (
              <p
                style={{
                  display: "flex",
                  flexDirection: "row",
                  gap: "5px",
                  fontSize: "18px",
                }}
                className="text-lg mt-2 break-words w-full"
                key={food.id}
              >
                <span style={{ color: "#F3EA28" }} className="text-gray-500">
                  x{food.quantity}
                </span>
                <span className="block">{food.name}</span>
              </p>
            ))}
          </div>

          <div className="flex flex-col items-end max-w-md w-full border-l-2 pl-6 py-4">
            <div className="flex flex-col w-full">
              <div className="grid grid-cols-2 gap-36 w-full">
                <div className="flex flex-col w-full">
                  <p className="text-lg">Tạm tính</p>
                  <p className="text-xl font-bold">
                    {totalPrice.toLocaleString()} VNĐ
                  </p>
                  <p className="text-lg">Tổng tiền</p>
                  <p className="text-xl font-bold">
                    {totalAfterDiscount.toLocaleString()} VNĐ
                  </p>
                </div>
                <div className="flex flex-col w-full">
                  <p className="text-lg">Khuyến mãi</p>
                  <p className="text-xl font-bold">{+totalDiscount} %</p>
                  <p className="text-lg">Điểm tích được</p>
                  <p className="text-xl font-bold">
                    {/* {
                      +Math.floor(
                        (totalAfterDiscount *
                          param?.loyalPoint_OrderToPointRatio) /
                          100
                      )
                    } */}

                    {param?.loyalPointOrderToPointRatio
                      ? +Math.floor(
                          (totalAfterDiscount *
                            param.loyalPointOrderToPointRatio) /
                            100
                        )
                      : 0}
                  </p>
                </div>
              </div>
              <div className="flex justify-between items-center mt-4">
                <p className="text-lg">Sử dụng điểm</p>
                <label className="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    className="hidden"
                    checked={usePoints}
                    onChange={handleTogglePoints}
                  />
                  <span
                    className={`w-10 h-5 flex items-center rounded-full p-1 ${
                      usePoints ? "bg-green-500" : "bg-gray-300"
                    }`}
                  >
                    <span
                      className={`bg-white w-4 h-4 rounded-full shadow-md transform duration-300 ${
                        usePoints ? "translate-x-5" : "translate-x-0"
                      }`}
                    ></span>
                  </span>
                  <span className="ml-3 text-lg">
                    {loyalPoint.toLocaleString()} điểm
                  </span>
                </label>
              </div>
              {pointUsage && (
                <div className="flex justify-between items-center mt-4">
                  <p className="text-lg">Đã sử dụng</p>

                  <span className="ml-3 text-lg">
                    {pointUsage.toLocaleString()} điểm
                  </span>
                </div>
              )}
            </div>

            <div className="w-full mt-2">
              <CustomButton
                defaultColor=""
                gradientFrom="#EE772E"
                gradientTo="#F6C343"
                textColor="#FFFFFF"
                hoverTextColor="#FFFFFF"
                borderColor="#FFFFFF"
                handleCreatePayment={handleCreatePayment} // Truyền sự kiện vào button
                href="#"
                className="w-full h-[40px] text-lg mt-4"
                text={isLoading ? "Đang xử lý..." : "Đặt ngay"} // Hiển thị text thay đổi khi đang xử lý
                disabled={isLoading} // Vô hiệu hóa nút khi đang xử lý
              />
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default PaymentSection;
