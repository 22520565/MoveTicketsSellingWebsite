import React from "react";
import { FiX } from "react-icons/fi";
import { useState, useEffect, useMemo } from "react";

const TheaterModal = ({ isOpen, onClose, type, onSave, mode }) => {
  if (!isOpen) return null;
  const isEditMode = mode === "edit";
  const title = isEditMode ? "Cập nhật thông tin rạp" : "Thêm mới rạp";

  console.log(type);

  const [formData, setFormData] = useState({
    id: type?.id || "",
    name: type?.name || "",
    address: type?.address || "",
    city: type?.city || "",
  });
  useEffect(() => {
    if (!isEditMode) {
      setFormData({
        id: "",
        name: "",
        address: "",
        city: "",
      });
    } else {
      // Nếu ở chế độ chỉnh sửa, thì giữ giá trị hiện tại của 'type'
      setFormData({
        id: type?.id || "",
        name: type?.name || "",
        address: type?.address || "",
        city: type?.city || "",
      });
    }
  }, [isEditMode, type]);

  const isFormValid = useMemo(() => {
    const requiredFields = ["name", "address", "city"];

    return requiredFields.every((field) => !!formData[field]);
  }, [formData]);

  const handleSubmit = () => {
    onSave(formData);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg w-[400px] max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-gray-800">{title}</h2>
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700"
            >
              <FiX className="w-6 h-6" />
            </button>
          </div>

          <div className="flex flex-col gap-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Tên rạp
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={(e) =>
                  setFormData((prev) => ({ ...prev, name: e.target.value }))
                }
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Địa chỉ
              </label>
              <input
                type="text"
                name="address"
                value={formData.address}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    address: e.target.value,
                  })
                }
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Thành phố
              </label>
              <input
                type="text"
                name="city"
                value={formData.city}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    city: e.target.value,
                  })
                }
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="flex justify-end space-x-4 mt-6">
            <button
              onClick={onClose}
              className="px-4 py-2 text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200"
            >
              Hủy bỏ
            </button>
            <button
              onClick={() => {
                handleSubmit();
              }}
              disabled={!isFormValid}
              className={`px-4 py-2 rounded-lg  ${
                isFormValid
                  ? "bg-blue-500 hover:bg-blue-700"
                  : "bg-gray-400 cursor-not-allowed"
              } text-white`}
            >
              {isEditMode ? "Lưu thay đổi" : "Thêm rạp"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TheaterModal;
