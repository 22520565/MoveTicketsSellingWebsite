import { CardElement, useStripe, useElements } from "@stripe/react-stripe-js";
import { useState } from "react";

function CheckoutForm({ clientSecret, payload }) {
  const stripe = useStripe();
  const elements = useElements();
  const [loading, setLoading] = useState(false);
  console.log("Payload in CheckoutForm:", payload);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;

    setLoading(true);

    const result = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
      },
    });

    if (result.error) {
      alert(result.error.message);
      setLoading(false);
    } else {
      if (result.paymentIntent.status === "succeeded") {
        alert("Thanh toán thành công");
        // 👉 gọi API tạo order tại đây nếu chưa
      }
    }
  };

  return (
    <div className="flex justify-center items-start gap-10 mt-10 px-10">
      {/* Cột trái: thông tin đơn hàng */}
      <div className="w-1/2 bg-white text-black p-6 rounded shadow">
        <h2 className="text-2xl font-bold mb-4">Chi tiết đơn hàng</h2>

        {payload?.tickets?.map((ticket, index) => (
          <p key={index}>
            🎟 Vé loại #{ticket.typeId} – SL: {ticket.quantity}
          </p>
        ))}

        {payload?.seatIds?.length > 0 && (
          <p className="mt-2">💺 Ghế: {payload.seatIds.join(", ")}</p>
        )}

        {payload?.items?.length > 0 && (
          <div className="mt-2">
            🍿 Đồ ăn:
            {payload.items.map((item, i) => (
              <div key={i}>
                #{item.id} – SL: {item.quantity}
              </div>
            ))}
          </div>
        )}

        {payload?.promotionIds?.length > 0 && (
          <p className="mt-2">🔖 KM: {payload.promotionIds.join(", ")}</p>
        )}

        <p className="mt-2">🪙 Điểm sử dụng: {payload.pointUsage}</p>

        <p className="mt-4 font-bold text-green-700 text-lg">
          💵 Tổng thanh toán: {payload.totalPriceAfterDiscount.toLocaleString()}{" "}
          VNĐ
        </p>
      </div>

      {/* Cột phải: nhập thông tin thẻ */}
      <form
        onSubmit={handleSubmit}
        className="w-1/2 bg-white p-6 rounded shadow"
      >
        <CardElement className="border p-2 bg-white rounded mb-4" />
        <button
          type="submit"
          disabled={!stripe || loading}
          className="w-full mt-2 bg-green-500 text-white py-2 rounded"
        >
          {loading ? "Đang xử lý..." : "Xác nhận thanh toán"}
        </button>
      </form>
    </div>
  );
}

export default CheckoutForm;
