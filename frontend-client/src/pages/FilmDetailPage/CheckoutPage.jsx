import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { createPaymentStripe } from "../../config/api";
import { loadStripe } from "@stripe/stripe-js";

export default function CheckoutPage() {
  const location = useLocation();
  const payload =
    location.state?.payload ||
    JSON.parse(localStorage.getItem("checkoutPayload"));

  const [loading, setLoading] = useState(true);

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
          const stripe = await loadStripe("pk_test_...");
          await stripe.redirectToCheckout({ sessionId });
        } else {
          alert("Không thể tạo phiên thanh toán");
        }
      } catch (err) {
        console.error("Lỗi khi tạo Checkout Session:", err);
        alert("Lỗi khi tạo Checkout Session");
      } finally {
        setLoading(false);
      }
    };

    redirectToStripe();
  }, []);

  if (!payload) {
    return <div>Không có thông tin đơn hàng. Vui lòng quay lại.</div>;
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      {loading ? (
        <div className="text-center text-lg font-semibold">
          Đang chuyển hướng đến trang thanh toán...
          <div className="mt-4 animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-green-500 mx-auto" />
        </div>
      ) : (
        <div>Đã xử lý xong (nhưng không chuyển được)</div>
      )}
    </div>
  );
}
