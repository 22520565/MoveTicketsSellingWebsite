import { useState, useEffect } from "react";
import Table from "../../components/Table";
import axios from "axios";
import { FiTrash2 } from "react-icons/fi";
import { TbCancel } from "react-icons/tb";
import { IoIosRefresh } from "react-icons/io";
import { BiRefresh } from "react-icons/bi";
import TicketDetailModal from "../../components/Modal/TicketDetailModal";
import TicketCancelModal from "../../components/Modal/TicketCancelModal";
import Dialog from "../../components/Dialog/ConfirmDialog";
import SuccessDialog from "../../components/Dialog/SuccessDialog";
import RefreshLoader from "../../components/Loading";
import { FaLock, FaLockOpen } from "react-icons/fa";
import { FaL } from "react-icons/fa6";
import {
  getCustomers,
  blockCustomer,
  unblockCustomer,
  deleteCustomer,
  undeleteCustomer,
  getCustomersBlocked,
  getCustomersDeleted,
} from "../../config/api";

const UserAccountManagementPage = () => {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [reason, setReason] = useState("");

  const [cusEmailQuery, setCusEmailQuery] = useState("");
  const [cusNameQuery, setCusNameQuery] = useState("");
  const [cusPhoneQuery, setCusPhoneQuery] = useState("");
  const [statusQuery, setStatusQuery] = useState("");

  const [isTicketModalOpen, setIsTicketModalOpen] = useState(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState(false);
  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [dialogData, setDialogData] = useState({ title: "", message: "" });
  const [mode, setMode] = useState("");

  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(false);

  //nhấn nút chặn
  const handleCancelClick = (order) => {
    if (order.deleted) return;
    setSelectedUser(order);
    if (order.blocked) {
      setMode("unblock");
      setDialogData({
        title: "Xác nhận",
        message: "Bạn chắc chắn muốn mở chặn tài khoản này này ?",
      });
    } else {
      setMode("block");
      setDialogData({
        title: "Xác nhận",
        message: "Bạn chắc chắn muốn chặn tài khoản này này ?",
      });
    }
    setIsConfirmModalOpen(true);
  };

  //nhấn nút xóa
  const handleDeleteClick = (order) => {
    setSelectedUser(order);
    setMode("delete");
    setDialogData({
      title: "Xác nhận",
      message: "Bạn chắc chắn muốn xóa tài khoản này ?",
    });
    setIsConfirmModalOpen(true);
  };

  //nhấn nút khôi phục
  const handleRestoreClick = (order) => {
    setSelectedUser(order);
    setMode("undelete");
    setDialogData({
      title: "Xác nhận",
      message: "Bạn chắc chắn muốn khôi phục tài khoản này ?",
    });
    setIsConfirmModalOpen(true);
  };

  const handleRefresh = async () => {
    setLoading(true);
    fetchUser();
    setTimeout(() => {
      setLoading(false);
    }, 2000);
  };

  //Xác nhận in vé
  const handleConfirmClick = async () => {
    setIsConfirmModalOpen(false);
    try {
      if (mode === "block") {
        await blockCustomer(selectedUser.id);
        setDialogData({
          title: "Thành công",
          message: "Chặn tài khoản thành công",
        });
      } else if (mode === "delete") {
        await deleteCustomer(selectedUser.id);
        setDialogData({
          title: "Thành công",
          message: "Xóa tài khoản thành công",
        });
      } else if (mode === "unblock") {
        await unblockCustomer(selectedUser.id);
        setDialogData({
          title: "Thành công",
          message: "Mở chặn tài khoản thành công",
        });
      } else if (mode === "undelete") {
        await undeleteCustomer(selectedUser.id);
        setDialogData({
          title: "Thành công",
          message: "Khôi phục tài khoản thành công",
        });
      }

      await handleRefresh();

      setIsSuccessModalOpen(true);
    } catch (error) {
      console.log("Error response:", error.response); // Để kiểm tra xem error có phản hồi hay không
      alert(
        "Thao tác thất bại, lỗi: " +
          (error.response ? error.response.success : error.message)
      );
    }
  };

  const fetchUser = async () => {
    setLoading(true);
    try {
      const [activeRes, blockedRes, deletedRes] = await Promise.all([
        getCustomers(),
        getCustomersBlocked(),
        getCustomersDeleted(),
      ]);
      // Gọi API lấy danh sách người dùng
      const active = (
        activeRes?.data?._embedded?.customerResponseDtoList || []
      ).map((c) => ({
        ...c,
        status: "active",
      }));

      const blocked = (
        blockedRes?.data?._embedded?.customerResponseDtoList || []
      ).map((c) => ({
        ...c,
        status: "blocked",
      }));

      const deleted = (
        deletedRes?.data?._embedded?.customerResponseDtoList || []
      ).map((c) => ({
        ...c,
        status: "deleted",
      }));

      // Lọc những order có printed === false
      const merged = [...active, ...blocked, ...deleted];
      setUsers(merged);
    } catch (error) {
      alert("Thao tác thất bại, lỗi: " + error.response.data.msg);
    } finally {
      setLoading(false);
    }
  };

  // Gọi API khi component được render lần đầu
  useEffect(() => {
    fetchUser();
  }, []);
  if (!users) {
    return;
  }

  console.log(users);

  const itemsPerPage = 7;
  const filteredData = users.filter((order) => {
    const matchesName = cusNameQuery
      ? order.name
          .toLowerCase()
          .normalize("NFC")
          .includes(cusNameQuery.toLowerCase().normalize("NFC"))
      : true;

    // Lọc theo mã code
    const matchesEmail = cusEmailQuery
      ? order.email.toLowerCase().includes(cusEmailQuery.toLowerCase())
      : true;
    const matchesPhone = cusPhoneQuery
      ? order.phoneNumber.toLowerCase().includes(cusPhoneQuery.toLowerCase())
      : true;

    const matchesStatus =
      statusQuery === "all"
        ? true // Trả về tất cả khi status là "All"
        : statusQuery === "Chặn"
        ? order.blocked === true // Lọc theo Chặn
        : statusQuery === "Không chặn"
        ? order.blocked === false // Lọc theo Không chặn
        : true;

    // Kết hợp cả hai điều kiện
    return matchesPhone && matchesEmail && matchesName && matchesStatus;
  });

  const totalPages = Math.ceil(filteredData.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedData = filteredData.slice(
    startIndex,
    startIndex + itemsPerPage
  );
  const statusOptions = ["Chặn", "Không chặn"];

  const columns = [
    { header: "Tên người dùng", key: "name" },
    { header: "Email", key: "email" },
    { header: "Số điện thoại", key: "phoneNumber" },
    {
      header: "Trạng thái",
      key: "status",
      render: (_, row) => {
        let statusText = "";
        let statusClass = "";

        if (row.deleted) {
          statusText = "Đã xoá";
          statusClass = "bg-gray-100 text-gray-800";
        } else if (row.blocked) {
          statusText = "Bị chặn";
          statusClass = "bg-red-100 text-red-800";
        } else {
          statusText = "Hoạt động";
          statusClass = "bg-green-100 text-green-800";
        }

        return (
          <span className={`px-2 py-1 rounded-full text-xs ${statusClass}`}>
            {statusText}
          </span>
        );
      },
    },
    {
      header: "Hành động",
      key: "actions",
      render: (_, row) => (
        <div className="flex items-center gap-2">
          {/* Nút khoá hoặc mở khoá */}
          {!row.blocked ? (
            <button
              className="text-red-600 hover:text-red-800"
              onClick={() => handleCancelClick(row)}
              title="Chặn người dùng"
            >
              <FaLock className="w-5 h-5" />
            </button>
          ) : (
            <button
              className="text-green-600 hover:text-green-800"
              onClick={() => handleCancelClick(row)}
              title="Bỏ chặn người dùng"
            >
              <FaLockOpen className="w-5 h-5" />
            </button>
          )}

          {/* Nút khôi phục nếu đã xoá */}
          {row.deleted && (
            <button
              className="text-green-600 hover:text-green-800"
              onClick={() => handleRestoreClick(row)}
              title="Khôi phục"
            >
              <IoIosRefresh className="w-4 h-4" />
            </button>
          )}

          {/* Nút xoá nếu chưa bị xoá */}
          {!row.deleted && (
            <button
              className="text-red-600 hover:text-red-800"
              onClick={() => handleDeleteClick(row)}
              title="Xoá người dùng"
            >
              <FiTrash2 className="w-4 h-4" />
            </button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">
          Thông tin người dùng
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
              placeholder="Tên người dùng...."
              value={cusNameQuery}
              onChange={(e) => setCusNameQuery(e.target.value)}
              className="w-full px-4 py-2 rounded-lg focus:outline-none border"
            />
          </div>
          <div className="flex items-center w-1/4">
            <input
              type="text"
              placeholder="Nhập email..."
              value={cusEmailQuery}
              onChange={(e) => setCusEmailQuery(e.target.value)}
              className="w-full px-4 py-2 rounded-lg focus:outline-none border"
            />
          </div>
          <div className="flex items-center w-1/4">
            <input
              type="text"
              placeholder="Nhập SĐT..."
              value={cusPhoneQuery}
              onChange={(e) => setCusPhoneQuery(e.target.value)}
              className="w-full px-4 py-2 rounded-lg focus:outline-none border"
            />
          </div>
          <div className="flex items-center w-[300px]">
            <select
              name="status"
              value={statusQuery}
              onChange={(e) => setStatusQuery(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="" disabled>
                <span className="text-gray-400">Trạng thái</span>
              </option>
              <option value="all">Tất cả</option>
              {statusOptions.map((status, index) => (
                <option key={index} value={status}>
                  {status}
                </option>
              ))}
            </select>
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

      <Dialog
        isOpen={isConfirmModalOpen}
        title={dialogData.title}
        message={dialogData.message}
        onClose={() => {
          setIsConfirmModalOpen(false);
        }}
        onConfirm={handleConfirmClick}
      />

      <SuccessDialog
        isOpen={!loading && isSuccessModalOpen}
        title={dialogData.title}
        message={dialogData.message}
        onClose={() => {
          setIsSuccessModalOpen(false);
        }}
      />

      <RefreshLoader isOpen={loading} />
    </div>
  );
};

export default UserAccountManagementPage;
