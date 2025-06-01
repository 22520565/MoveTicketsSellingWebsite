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
  return await api.post(`/films`, data, {
    headers: {
      "Content-Type": "application/json",
    },
  });
};

export const updateFilm = async (id, data) => {
  return await api.put(`/films/${id}`, data);
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

export const getAllTags = async () => {
  return await api.get(`/tags`);
};

export const getTagById = async (id) => {
  return await api.get(`/tags/${id}`);
};

export const uploadThumbnail = async (id, formData) => {
  return await api.patch(`/films/${id}/thumbnail`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const getAllItems = async () => {
  return await api.get(`/additional-items`);
};

export const getAllItemsDeleted = async () => {
  return await api.get(`/additional-items/deleted`);
};

export const addItem = async (data) => {
  return await api.post(`/additional-items`, data);
};

export const updateItem = async (id, data) => {
  return await api.put(`/additional-items/${id}`, data);
};

export const deleteItem = async (id) => {
  return await api.patch(`/additional-items/delete/${id}`);
};

export const undeleteItem = async (id) => {
  return await api.patch(`/additional-items/undelete/${id}`);
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
  return await axios.get(`/theaters`);
};
