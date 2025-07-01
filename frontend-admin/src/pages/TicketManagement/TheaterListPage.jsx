import { useState, useEffect } from "react";
import Table from "../../components/Table";
import { TbCancel } from "react-icons/tb";
import { FiEdit2 } from "react-icons/fi";
import { BiRefresh } from "react-icons/bi";
import Dialog from "../../components/Dialog/ConfirmDialog";
import SuccessDialog from "../../components/Dialog/SuccessDialog";
import RefreshLoader from "../../components/Loading";
import {
  getCinemas,
  deleteCinema,
  addCinema,
  updateCinema,
} from "../../config/api";
import TheaterModal from "../../components/Modal/TheaterModal";

export default function TheaterListPage() {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [mode, setMode] = useState("add");

  const [cineNameQuery, setCineNameQuery] = useState("");
  const [addressQuery, setAddressQuery] = useState("");
  const [cityQuery, setCityQuery] = useState("");

  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [dialogData, setDialogData] = useState({ title: "", message: "" });

  const [currentPage, setCurrentPage] = useState(1);

  const [loading, setLoading] = useState(false);

  const handleDeleteFilter = () => {
    setAddressQuery("");
    setCineNameQuery("");
    setCityQuery("");
    setStatusQuery("");
  };

  //mở modal add
  const handleAddClick = () => {
    setMode("add");
    setSelectedOrder(null);
    setIsDetailModalOpen(true);
  };

  //mở modal hủy phục vụ
  const handleCancelClick = (order) => {
    setMode("cancel");
    setSelectedOrder(order);
    setIsConfirmModalOpen(true);
    setDialogData({
      title: "Xác nhận",
      message: "Bạn chắc chắn muốn xóa rạp này ? Việc này không thể hoàn tác.",
    });
  };

  //đóng modal
  const handleCloseModal = () => {
    setIsDetailModalOpen(false);
    setIsConfirmModalOpen(false);
    setIsSuccessModalOpen(false);
    setSelectedOrder(null);
  };

  //mở modal view
  const handleEditClick = (order) => {
    setSelectedOrder(order);
    setIsDetailModalOpen(true);
    setMode("edit");
  };

  //Xác nhận phục vụ
  const handleConfirmModal = (order) => {
    setIsConfirmModalOpen(true);
    setSelectedOrder(order);
    if (mode === "add") {
      setDialogData({
        title: "Xác nhận",
        message: "Bạn chắc chắn muốn thêm rạp này ?",
      });
    }
    if (mode === "edit") {
      setDialogData({
        title: "Xác nhận",
        message: "Bạn chắc chắn muốn cập nhật rạp này ?",
      });
    }
  };

  const handleRefresh = async () => {
    setLoading(true);
    fetchOrder();
    setTimeout(() => {
      setLoading(false);
    }, 2000);
  };

  //Confirm modal hiện ra và bấm xác nhận
  const handleConfirmClick = async () => {
    try {
      if (mode === "cancel") {
        await deleteCinema(selectedOrder.id);
        setDialogData({
          title: "Thành công",
          message: "Xóa rạp phim thành công",
        });
      } else if (mode === "add") {
        await addCinema(selectedOrder);
        setDialogData({
          title: "Thành công",
          message: "Thêm rạp phim thành công",
        });
      } else if (mode === "edit") {
        await updateCinema(selectedOrder.id, selectedOrder);
        setDialogData({
          title: "Thành công",
          message: "Cập nhật rạp phim thành công",
        });
      }
      setIsDetailModalOpen(false);
      await handleRefresh();
      setIsSuccessModalOpen(true);
    } catch (error) {
      alert("Thao tác thất bại, lỗi: " + error.response);
    } finally {
      setTimeout(() => {
        setLoading(false);
      }, 2000);
      setIsConfirmModalOpen(false);
    }
  };

  const fetchOrder = async () => {
    try {
      setLoading(true);
      const response = await getCinemas();

      setOrders(response?.data?._embedded?.theaterResponseDtoList || []);
    } catch (error) {
      alert("Thao tác thất bại, lỗi: " + error.response.data.msg);
    } finally {
      setLoading(false); // End loading when API call is complete
    }
  };

  // Gọi API khi component được render lần đầu
  useEffect(() => {
    fetchOrder();
  }, []);
  if (!orders) {
    return;
  }
  const itemsPerPage = 7;
  const filteredData = orders.filter((order) => {
    //lọc theo tên khách hàng
    const matchesName = cineNameQuery
      ? order.name
          .toLowerCase()
          .normalize("NFC")
          .includes(cineNameQuery.toLowerCase().normalize("NFC"))
      : true;

    // Lọc theo mã code
    const matchesCity =
      !cityQuery || order.city?.toLowerCase().includes(cityQuery.toLowerCase());

    const matchesAddress =
      !addressQuery ||
      order.address?.toLowerCase().includes(addressQuery.toLowerCase());

    // Kết hợp cả ba điều kiện
    return matchesCity && matchesName && matchesAddress;
  });
  const totalPages = Math.ceil(filteredData.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedData = filteredData.slice(
    startIndex,
    startIndex + itemsPerPage
  );

  const columns = [
    {
      header: "Tên rạp chiếu phim",
      key: "name",
    },
    { header: "Địa chỉ", key: "address" },
    { header: "Thành phố", key: "city" },
    {
      header: "Hành động",
      key: "actions",
      render: (_, row) => (
        <div className="flex space-x-3">
          <button
            className="text-blue-600 hover:text-blue-800"
            onClick={() => handleEditClick(row)}
          >
            <FiEdit2 className="w-4 h-4" />
          </button>
          <button className="text-red-600 hover:text-red-800">
            <TbCancel
              className="w-4 h-4"
              onClick={() => handleCancelClick(row)}
            />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">
          Thông tin rạp phim
        </h2>
        <div className="flex items-center gap-4">
          <button
            onClick={handleRefresh}
            className="r p-4 rounded-full hover:bg-gray-100 transition-all duration-300"
            disabled={loading}
          >
            <BiRefresh
              className={`text-4xl text-black hover:text-black ${
                loading
                  ? "animate-spin"
                  : "hover:rotate-180 transition-transform duration-300"
              }`}
            />
          </button>
          <div className="flex items-center w-[300px]">
            <input
              type="text"
              placeholder="Tên rạp...."
              value={cineNameQuery}
              onChange={(e) => setCineNameQuery(e.target.value)}
              className="w-full px-4 py-2 rounded-lg focus:outline-none border"
            />
          </div>
          <div className="flex items-center w-1/4">
            <input
              type="text"
              placeholder="Địa chỉ..."
              value={addressQuery}
              onChange={(e) => setAddressQuery(e.target.value)}
              className="w-full px-4 py-2 rounded-lg focus:outline-none border"
            />
          </div>
          <div className="flex items-center w-1/4">
            <input
              type="text"
              placeholder="Thành phố..."
              value={cityQuery}
              onChange={(e) => setCityQuery(e.target.value)}
              className="w-full px-4 py-2 rounded-lg focus:outline-none border"
            />
          </div>

          <button
            className="ml-4 px-4 py-2 text-gray-600 bg-gray-300 rounded-lg hover:bg-gray-400"
            onClick={() => handleDeleteFilter()}
          >
            Xóa lọc
          </button>
          <div>
            <button
              className="px-4 py-2 bg-black text-white rounded-lg hover:bg-blue-700 transition-colors duration-200"
              onClick={() => handleAddClick()}
            >
              Thêm rạp +
            </button>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm overflow-x-auto">
        <Table columns={columns} data={paginatedData} />

        {filteredData.length > 0 && (
          <div className="flex items-center justify-between px-6 py-4 bg-gray-50">
            <button
              onClick={() => setCurrentPage(currentPage - 1)}
              disabled={currentPage === 1}
              className="px-4 py-2 text-sm text-gray-600 bg-white rounded-lg shadow-sm disabled:opacity-50"
            >
              Trước
            </button>
            <span className="text-sm text-gray-600">
              Trang {currentPage} trên {totalPages}
            </span>
            <button
              onClick={() => setCurrentPage(currentPage + 1)}
              disabled={currentPage === totalPages}
              className="px-4 py-2 text-sm text-gray-600 bg-white rounded-lg shadow-sm disabled:opacity-50"
            >
              Tiếp
            </button>
          </div>
        )}
      </div>

      <TheaterModal
        type={selectedOrder}
        isOpen={isDetailModalOpen}
        onClose={handleCloseModal}
        onSave={handleConfirmModal}
        mode={mode}
      />

      <Dialog
        isOpen={isConfirmModalOpen}
        title={dialogData.title}
        message={dialogData.message}
        onClose={handleCloseModal}
        onConfirm={handleConfirmClick}
      />

      <SuccessDialog
        isOpen={isSuccessModalOpen}
        title={dialogData.title}
        message={dialogData.message}
        onClose={handleCloseModal}
      />
      <RefreshLoader isOpen={loading} />
    </div>
  );
}
