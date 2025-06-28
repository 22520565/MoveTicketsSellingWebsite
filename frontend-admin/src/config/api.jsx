import axiosDefault from "axios";
import api from "./axios_custom";

export const callLogin = async (data) => {
  return axiosDefault.post(
    "http://localhost:8080/api/auth/employee/login",
    { ...data },
    {
      headers: {
        "Content-Type": "application/json",
        "x-no-retry": true,
      },
    }
  );
};

export const getEmployeeDetail = async () => {
  return await api.get(`/self/employee`);
};

export const validateJWT = async () => {
  return await api.get(`/self/employee/validate-jwt`);
};

export const getTheaterById = async (id) => {
  return await api.get(`/theaters/${id}`);
};

export const getAllFilms = async () => {
  return await api.get(`/films`);
};

export const getAllFilmsDeleted = async () => {
  return await api.get(`/films/deleted`);
};

export const addNewFilm = async (data) => {
  let { file, ...finalData } = data;

  try {
    // B1: Tạo item trước
    const createRes = await api.post(`/films`, finalData);
    const createdItem = createRes.data;
    const itemId = createdItem.id;

    // B2: Nếu có file thì upload
    if (file && itemId) {
      const formData = new FormData();
      formData.append("file", file);

      const image = await api.patch(`/films/${itemId}/thumbnail`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      return {
        ...createdItem,
        thumbnailUrl: image.data.url, // giả sử API trả về { url: '...' }
      };
    }

    return createdItem;
  } catch (err) {
    console.error("Lỗi tạo item hoặc upload ảnh:", err);
    throw err;
  }
};

export const updateFilm = async (id, data) => {
  let { file, ...finalData } = data;

  try {
    // B1: Nếu có file mới thì upload thumbnail trước
    if (file) {
      const formData = new FormData();
      formData.append("file", file);

      const image = await api.patch(`/films/${id}/thumbnail`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      return {
        ...finalData,
        thumbnailUrl: image.data.url, // giả sử API trả về { url: '...' }
      };
    }

    console.log("Không có file mới, chỉ cập nhật dữ liệu:", finalData);

    // B2: Gửi phần còn lại của dữ liệu (JSON)
    return await api.put(`/films/${id}`, finalData);
  } catch (err) {
    console.error("Lỗi cập nhật sản phẩm hoặc ảnh:", err);
    throw err;
  }
};

export const deleteFilm = async (id) => {
  return await api.patch(`/films/delete/${id}`);
};

export const undeleteFilm = async (id) => {
  return await api.patch(`/films/undelete/${id}`);
};

export const getFilmById = async (id) => {
  return await api.get(`/films/${id}`);
};

export const getAllFilmShows = async () => {
  return await api.get(`/film-shows`);
};

export const getAllFilmShowsDeleted = async () => {
  return await api.get(`/film-shows/deleted`);
};

export const deleteFilmShow = async (id) => {
  return await api.patch(`/film-shows/delete/${id}`);
};

export const undeleteFilmShow = async (id) => {
  return await api.patch(`/film-shows/undelete/${id}`);
};

export const addFilmShows = async (data) => {
  return await api.post(`/film-shows`, { ...data });
};

export const getAllRooms = async () => {
  return await api.get(`/rooms`);
};

export const getAllRoomsDeleted = async () => {
  return await api.get(`/rooms/deleted`);
};

export const getRoomById = async (id) => {
  return await api.get(`/rooms/${id}`);
};

export const uploadThumbnail = async (id, formData) => {
  return await api.patch(`/films/${id}/thumbnail`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

//Additional Items
export const getAllItems = async (page = 0, size = 20) => {
  return await api.get(`/additional-items`, {
    params: {
      page,
      size,
    },
  });
};

export const getAllItemsDeleted = async () => {
  return await api.get(`/additional-items/deleted`);
};

export const addItem = async (data) => {
  let { file, ...finalData } = data;

  try {
    // B1: Tạo item trước
    const createRes = await api.post(`/additional-items`, finalData);
    const createdItem = createRes.data;
    const itemId = createdItem.id;

    // B2: Nếu có file thì upload
    if (file && itemId) {
      const formData = new FormData();
      formData.append("file", file);

      const image = await api.patch(
        `/additional-items/${itemId}/thumbnail`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );
      return {
        ...createdItem,
        thumbnailUrl: image.data.url, // giả sử API trả về { url: '...' }
      };
    }

    return createdItem;
  } catch (err) {
    console.error("Lỗi tạo item hoặc upload ảnh:", err);
    throw err;
  }
};

export const updateItem = async (id, data) => {
  let { file, ...finalData } = data;

  try {
    // B1: Nếu có file mới thì upload thumbnail trước
    if (file) {
      const formData = new FormData();
      formData.append("file", file);

      const image = await api.patch(
        `/additional-items/${id}/thumbnail`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      return {
        ...finalData,
        thumbnailUrl: image.data.url, // giả sử API trả về { url: '...' }
      };
    }

    console.log("Không có file mới, chỉ cập nhật dữ liệu:", finalData);

    // B2: Gửi phần còn lại của dữ liệu (JSON)
    return await api.put(`/additional-items/${id}`, finalData);
  } catch (err) {
    console.error("Lỗi cập nhật sản phẩm hoặc ảnh:", err);
    throw err;
  }
};

export const deleteItem = async (id) => {
  return await api.patch(`/additional-items/delete/${id}`);
};

export const undeleteItem = async (id) => {
  return await api.patch(`/additional-items/undelete/${id}`);
};

//Promotions
export const getAllPromotions = async (page = 0, size = 20) => {
  return await api.get(`/promotions`, {
    params: {
      page,
      size,
    },
  });
};

export const getAllProsExpired = async (page = 0, size = 20) => {
  return await api.get(`/promotions/expired`, {
    params: {
      page,
      size,
    },
  });
};

export const getAllProsInactive = async (page = 0, size = 20) => {
  return await api.get(`/promotions/inactive`, {
    params: {
      page,
      size,
    },
  });
};

export const addPromotion = async (data) => {
  let { file, ...finalData } = data;

  try {
    // B1: Tạo item trước
    const createRes = await api.post(`/promotions`, finalData);
    const createdItem = createRes.data;
    const itemId = createdItem.id;

    // B2: Nếu có file thì upload
    if (file && itemId) {
      const formData = new FormData();
      formData.append("file", file);

      const image = await api.patch(
        `/promotions/${itemId}/thumbnail`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );
      return {
        ...createdItem,
        thumbnailUrl: image.data.url, // giả sử API trả về { url: '...' }
      };
    }

    return createdItem;
  } catch (err) {
    console.error("Lỗi tạo item hoặc upload ảnh:", err);
    throw err;
  }
};

export const updatePromotion = async (id, data) => {
  let { file, ...finalData } = data;

  try {
    // B1: Nếu có file mới thì upload thumbnail trước
    if (file) {
      const formData = new FormData();
      formData.append("file", file);

      const image = await api.patch(`/promotions/${id}/thumbnail`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      return {
        ...finalData,
        thumbnailUrl: image.data.url, // giả sử API trả về { url: '...' }
      };
    }

    console.log("Không có file mới, chỉ cập nhật dữ liệu:", finalData);

    // B2: Gửi phần còn lại của dữ liệu (JSON)
    return await api.put(`/promotions/${id}`, finalData);
  } catch (err) {
    console.error("Lỗi cập nhật sản phẩm hoặc ảnh:", err);
    throw err;
  }
};

export const deletePromotion = async (id) => {
  return await api.delete(`/promotions/${id}`);
};

export const pausePromotion = async (id) => {
  return await api.patch(`/promotions/pause/${id}`);
};

export const resumePromotion = async (id) => {
  return await api.patch(`/promotions/resume/${id}`);
};

//Param Page
export const getAllTicketType = async (page = 0, size = 20) => {
  return await api.get(`/ticket-types`, {
    params: {
      page,
      size,
    },
  });
};

export const getTicketTypeById = async (id) => {
  return await api.get(`/ticket-types/${id}`);
};
export const deleteTicketTypeById = async (id) => {
  return await api.delete(`/ticket-types/${id}`);
};
export const addTicketType = async (data) => {
  return await api.post(`/ticket-types`, data);
};
export const updateTicketType = async (id, data) => {
  return await api.put(`/ticket-types/${id}`, data);
};

export const getAllTags = async (page = 0, size = 20) => {
  return await api.get(`/tags`, {
    params: {
      page,
      size,
    },
  });
};

export const getTagById = async (id) => {
  return await api.get(`/tags/${id}`);
};
export const deleteTagById = async (id) => {
  return await api.delete(`/tags/${id}`);
};
export const addTag = async (data) => {
  return await api.post(`/tags`, data);
};

export const getParam = async () => {
  return await api.get(`/params`);
};

export const updateParam = async (data) => {
  return await api.put(`/params`, data);
};

//Statistics page
export const getTiketServeRate = async (data) => {
  return await api.get("/statistics/ticket-serve-rate", {
    params: {
      date: data.date, // bắt buộc
      theaterId: data.theaterId, // optional
    },
  });
};

export const getTiketCategoryRate = async (data) => {
  return await api.get("/statistics/ticket-category-rate", {
    params: {
      date: data.date, // bắt buộc
      theaterId: data.theaterId, // optional
      page: params.page || 0, // mặc định
      size: params.size || 20, // mặc định
    },
  });
};

export const getMonthlyStatistics = async (year) => {
  return await api.get(`/statistics/monthly/${year}`);
};

export const getMonthlyStatisticsByTheater = async (year, theaterId) => {
  return await api.get(`/statistics/monthly/${year}/theater/${theaterId}`);
};

export const getHotFilmStatistics = async (params) => {
  return await api.get("/statistics/hot-film", {
    params: {
      date: params.date, // bắt buộc
      theaterId: params.theaterId, // optional
    },
  });
};

export const getFilmStatistics = async (params) => {
  return await api.get("/statistics/film", {
    params: {
      date: params.date, // bắt buộc
      theaterId: params.theaterId, // tùy chọn
      page: params.page || 0,
      size: params.size || 10,
    },
  });
};

export const getDailyStatistics = async (date) => {
  return await api.get(`/statistics/daily/${date}`);
};

export const getDailyStatisticsByTheater = async (date, theaterId) => {
  return await api.get(`/statistics/daily/${date}/theater/${theaterId}`);
};

export const getBestSellingItem = async (params) => {
  return await api.get("/statistics/best-selling-item", {
    params: {
      date: params.date, // bắt buộc
      theaterId: params.theaterId, // tùy chọn
    },
  });
};

export const getAdditionalItemsRate = async (params) => {
  return await api.get("/statistics/additional-items-rate", {
    params: {
      date: params.date,
      theaterId: params.theaterId,
      page: params.page || 0,
      size: params.size || 10,
    },
  });
};

//............
// export const callSignUp = async (data) => {
//   return axios.post("/auth/user/sign-up", { ...data });
// };

// export const callAccount = async () => {
//   return axios.get("/auth/user/account");
// };

// export const callSignOut = async (data) => {
//   return axios.get("/auth/user/sign-out", { ...data });
// };

// export const updateUser = async (id, updateData) => {
//   return axios.put(`/user/user/${id}`, { ...updateData });
// };

// export const changePasword = async (id, updateData) => {
//   return axios.post(`/user/user/change-password/${id}`, { ...updateData });
// };

// export const getShowingFilms = async () => {
//   return await axios.get(`film-show/showing`);
// };

// export const getUpcommingFilms = async () => {
//   return await axios.get(`film-show/upcoming`);
// };

// export const searchFilm = async ({ keyword, page = 1, limit = 2 }) => {
//   return await axios.post(`films/searchFilm`, {
//     keyword,
//     page,
//     limit,
//   });
// };

// export const getShowTimeOfDateByFilmId = async (filmId) => {
//   return await axios.get(`film-show/getByDate`, {
//     params: {
//       filmId,
//     },
//   });
// };

// export const getAvailableShowDate = async (filmId) => {
//   return await axios.get(`film-show/get-available/showDate`);
// };

// export const getAvailableFilmByDate = async ({
//   date,
//   filmId,
//   page,
//   limit = 1000,
// }) => {
//   return await axios.post(`film-show/get-film-available-by-date`, {
//     date,
//     filmId,
//     page,
//     limit,
//   });
// };

// export const getAllFoods = async () => {
//   return await axios.get(`/additional-items`);
// };

// export const resetPassword = async (email) => {
//   return await axios.post(`/email/send-reset-password`, { userEmail: email });
// };

// export const createPayment = async ({
//   totalPrice,
//   filmShowId = null,
//   seatSelections = null,
//   promotionIDs = null,
//   ticketSelections = null,
//   additionalItemSelections = null,
//   pointUsage = null,
// }) => {
//   return await axios.post(`/payment`, {
//     totalPrice,
//     filmShowId,
//     seatSelections,
//     promotionIDs,
//     ticketSelections,
//     additionalItemSelections,
//     pointUsage,
//   });
// };

// export const createPromotion = async (formData) => {
//   return await axios.post(`/promotion`, { ...formData });
// };
// export const getCurrentPro = async (date) => {
//   return await axios.get(`/promotion`, { params: { date } });
// };
// export const updatePro = async (id) => {
//   return await axios.patch(`/promotion/${id}`);
// };
// export const deletePro = async (id) => {
//   return await axios.delete(`/promotion/${id}`);
// };

// export const getProById = async (id) => {
//   return await axios.get(`/promotion/${id}`);
// };

// export const getAllOrderByUserId = async () => {
//   return await axios.get(`/orders/all-order-by-userId`);
// };

// export const getAllPromotion = async () => {
//   return await axios.get(`/promotion/active`);
// };

// // point

// export const getCurrentPoint = async () => {
//   return await axios.get(`/loyalpoint`);
// };

// export const getParam = async () => {
//   return await axios.get(`/param`);
// };

export const getCinemas = async () => {
  return await api.get(`/theaters`);
};
