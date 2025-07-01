import api from "./axios_custom";
import axios from "./axios_private";

export const callLogin = async (data) => {
  return axios.post("/auth/customer/login", { ...data });
};

export const callSignUp = async (data) => {
  return axios.post("/auth/customer/register", { ...data });
};

export const callAccount = async () => {
  return axios.get("/self/customer");
};

export const callSignOut = async (data) => {
  return axios.post("/auth/customer/logout", { ...data });
};

export const updateUser = async (updateData) => {
  return axios.put(`/self/customer`, { ...updateData });
};

export const changePasword = async (updateData) => {
  return axios.patch(`/self/customer/reset-password`, { ...updateData });
};

export const searchFilm = async ({ keyword, page = 0, size = 10 }) => {
  return await axios.get(`/films/search/${keyword}`, {
    params: {
      page,
      size,
    },
  });
};

export const getShowTimeOfDateByFilmId = async (filmId) => {
  return await axios.get(`film-show/getByDate`, {
    params: {
      filmId,
    },
  });
};

export const getAvailableShowDate = async (filmId) => {
  return await axios.get(`film-show/get-available/showDate`);
};

export const getAvailableFilmByDate = async (date) => {
  return await axios.get(`/film-shows/available-film-by-date/${date}`);
};

export const getAllFilms = async () => {
  return await axios.get(`/films`);
};

export const getAllFoods = async () => {
  return await axios.get(`/additional-items`);
};

export const resetPassword = async (email) => {
  return await axios.post(`/email/send-reset-password`, { userEmail: email });
};

export const createPayment = async ({
  totalPrice,
  filmShowId = null,
  seatSelections = null,
  promotionIDs = null,
  ticketSelections = null,
  additionalItemSelections = null,
  pointUsage = null,
}) => {
  return await axios.post(`/payment`, {
    totalPrice,
    filmShowId,
    seatSelections,
    promotionIDs,
    ticketSelections,
    additionalItemSelections,
    pointUsage,
  });
};

export const createPromotion = async (formData) => {
  return await axios.post(`/promotion`, { ...formData });
};
export const getCurrentPro = async (date) => {
  return await axios.get(`/promotion`, { params: { date } });
};
export const updatePro = async (id) => {
  return await axios.patch(`/promotion/${id}`);
};
export const deletePro = async (id) => {
  return await axios.delete(`/promotion/${id}`);
};

export const getProById = async (id) => {
  return await axios.get(`/promotion/${id}`);
};

export const getAllOrderByUserId = async (id) => {
  return await axios.get(`/orders/by-customer/${id}`);
};

export const getAllPromotion = async () => {
  return await axios.get(`/promotions/active`);
};

// point

export const getCurrentPoint = async () => {
  return await axios.get(`/loyalpoint`);
};

export const getParam = async () => {
  return await axios.get(`/params`);
};

export const getCinemas = async () => {
  return await axios.get(`/theaters`);
};

export const getShowingFilms = async () => {
  return await axios.get(`film-shows/showing`);
};

export const getUpcommingFilms = async () => {
  return await axios.get(`film-shows/upcoming`);
};

export const getAllTags = async () => {
  return await axios.get(`/tags`);
};

export const getFilmByTheaterId = async (id) => {
  return await axios.get(`/films/by-theater/${id}`);
};

export const getTheaterByFilmId = async (id) => {
  return await axios.get(`/theaters/by-film/${id}`);
};

export const getFilmById = async (id) => {
  return await axios.get(`/films/${id}`);
};

export const getFilmShowByFilmId = async (id) => {
  return await axios.get(`/film-shows/by-film/${id}`);
};

export const getFilmShowByFilmIdAndDate = async (id, date) => {
  return await axios.get(`/film-shows/by-film/${id}/by-date/${date}`);
};

export const getAdditionalItem = async () => {
  return await axios.get(`/additional-items`);
};

export const getRoomById = async (id) => {
  return await axios.get(`/rooms/${id}`);
};

export const getAllRooms = async () => {
  return await axios.get(`/rooms`);
};

export const getAllItems = async () => {
  return await axios.get(`/additional-items`);
};

//filmdetail
export const getAllTicketType = async () => {
  return await axios.get(`/ticket-types`);
};

export const getFilmShowById = async (id) => {
  return await axios.get(`/film-shows/${id}`);
};

export const getRoomSeatByRoomId = async (id) => {
  return await axios.get(`/room-seats/by-room/${id}`);
};

export const getRoomSeatLockByFilmshowId = async (filmShowId) => {
  return await api.get(
    `/room-seats/by-film-show/${filmShowId}?page=0&size=250`
  );
};

export const createPaymentStripe = async (data) => {
  return await axios.post(`/orders/stripe-checkout`, data);
};

export const createOrder = async (data) => {
  return await axios.post(`/orders`, data);
};

export const getRoomSeatUnuable = async (id) => {
  return await api.get(`/room-seats/unusable-by-film-show/${id}`);
};

export const getCustomerById = async (id) => {
  return await api.get(`/customers/${id}`);
};

export const getFilmshowById = async (id) => {
  return await api.get(`/film-shows/${id}`);
};
