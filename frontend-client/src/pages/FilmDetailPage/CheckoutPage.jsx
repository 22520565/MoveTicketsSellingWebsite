import { useLocation } from "react-router-dom";
import { Elements } from "@stripe/react-stripe-js";
import CheckoutForm from "./CheckoutForm"; // Import the CheckoutForm component
import { createPaymentStripe } from "../../config/api";
import { loadStripe } from "@stripe/stripe-js";

import { useEffect } from "react";

export default function CheckoutPage() {
  const location = useLocation();
  const payload =
    location.state?.payload ||
    JSON.parse(localStorage.getItem("checkoutPayload"));

  console.log("Location state:", location.state);
  console.log("Payload:", payload);
  if (!payload) {
    return <div>Không có thông tin đơn hàng. Vui lòng quay lại.</div>;
  }
  useEffect(() => {
    const redirectToStripe = async () => {
      try {
        if (!payload) {
          alert("Không tìm thấy dữ liệu đơn hàng.");
          return;
        }
        const response = await createPaymentStripe(payload);
        console.log("Response from createPaymentStripe:", response);

        const sessionId = response?.clientSecret;

        if (sessionId) {
          const stripe = await loadStripe(
            "pk_test_51RSroiFLS9qgPWZTC329aaYLG3kpwxs5dB7cICsPSiZqk58x3DU3X2oYHE4DmiqoeT1g9Sx48CThnIgH9fQ9bEwS00YI7hWxoQ"
          );
          await stripe.redirectToCheckout({ sessionId });
        } else {
          alert("Không thể tạo phiên thanh toán");
        }
      } catch (err) {
        console.error("Lỗi khi tạo Checkout Session:", err);
        alert("Lỗi khi tạo Checkout Session");
      }
    };

    redirectToStripe();
  }, []);

  return <div>Đang chuyển hướng đến trang thanh toán...</div>;
}
