import React, { useState, useEffect } from "react";
import {
  FaTag,
  FaRegClock,
  FaGlobeAmericas,
  FaCommentDots,
} from "react-icons/fa";
import { LuUserRoundCheck } from "react-icons/lu";
import { FaRegCirclePlay } from "react-icons/fa6";
import "./filmPage.css";
import FilmInfoSection from "../../Components/FilmInfoSection";
import TrailerModal from "../../Components/TrailerModal";
import axios from "axios";
import ScheduleChooseBox from "../../Components/ScheduleChooseBox";
import ShowtimeChooseBox from "../../Components/ShowtimeChooseBox";
import TicketType from "../../Components/TicketType";
import { useNavigate, useParams } from "react-router-dom";
import { useLocation } from "react-router-dom";
import {
  getCurrentPoint,
  getCurrentPro,
  getParam,
  getShowTimeOfDateByFilmId,
} from "../../config/api";
import formatCurrencyNumber from "../../utils/FormatCurrency";
import QuantitySelectorV2 from "../../Components/QuantitySelectorV2";
import { use } from "react";
import FoodCardV2 from "../../Components/FoodCard/FoodCardV2";
import "./oStyle.css";
import "./cStyle.css";
import whiteScreen from "../../assets/whiteScreen.png";
import CustomButton from "../../Components/button/index"; // Giả sử bạn đã có CustomButton component
import { useAuth } from "../../Context/AuthContext"; // Dùng context cho user
import { createPayment } from "../../config/api"; // Đảm bảo createPayment được định nghĩa đúng
import PromotionList from "../../Components/PromotionList"; // Import PromotionList
import { FaArrowLeft } from "react-icons/fa"; // Import biểu tượng mũi tên
import { ModalWhenBuyTicket } from "../../Components/Modal/ModalWhenBuyTicket";
import {
  getFilmById,
  getFilmShowByFilmId,
  getAdditionalItem,
  getAllTags,
  getAllRooms,
  getCinemas,
  getRoomById,
  getAllTicketType,
  getFilmShowById,
  getRoomSeatByRoomId,
  getRoomSeatLockByFilmshowId,
  createPaymentStripe,
  getRoomSeatUnuable,
} from "../../config/api";
import { FileImage } from "lucide-react";
import CinemaScheduleList from "../../Components/CinemaList";
import { loadStripe } from "@stripe/stripe-js";

const seatWidth = 50;
const seatHeight = 40;
const gapX = 20;
const gapY = 15;

const FilmDetailPage = () => {
  const { filmID } = useParams();
  const location = useLocation();
  const { initShowDate, initShowTime, initCinema } = location.state || {};

  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const [videoOpen, setVideoOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState(initShowDate || "");
  const [isPromotionListOpen, setIsPromotionListOpen] = useState(false); // Trạng thái PromotionList
  const [selectedPromotions, setSelectedPromotions] = useState([]);
  const [totalDiscount, setTotalDiscount] = useState(0);
  const [availableCinemaSchedules, setAvailableCinemaSchedules] = useState([]);

  const [allTags, setAllTags] = useState([]);

  useEffect(() => {
    setSelectedFilmShowID(null);
  }, [selectedDate]);

  const [selectedShowtime, setSelectedShowtime] = useState(initShowTime || "");

  const [availableDates, setAvailableDates] = useState([]);
  const [uniqueAvailableDates, setUniqueAvailableDates] = useState([]);
  const [availableShowtimesWithFilmType, setAvailableShowtimesWithFilmType] =
    useState([]);

  const fetchDate = async () => {
    try {
      const response = await getFilmShowByFilmId(filmID);

      const filmShows = response?._embedded?.filmShowResponseDtoList;

      // const roomIdsInSelectedTheater = [];

      // for (const show of filmShows) {
      //   const roomRes = await getRoomById(show.roomId);
      //   console.log("room res: ", roomRes);

      //   const roomData = roomRes;
      //   if (roomData.theaterId === selectedCinema.id) {
      //     roomIdsInSelectedTheater.push(roomData.id);
      //   }
      // }

      // const filteredShows = filmShows.filter((show) =>
      //   roomIdsInSelectedTheater.includes(show.roomId)
      // );

      // const now = new Date();

      // console.log("Filtered shows: ", filteredShows);

      // const upcomingShows = filteredShows.filter((show) => {
      //   const showDateTime = new Date(`${show.showDate}T${show.showTime}`);
      //   return showDateTime > now;
      // });

      // setAvailableDates(upcomingShows);
      const now = new Date();

      const validShows = filmShows.filter((show) => {
        const showDateTime = new Date(`${show.showDate}T${show.showTime}`);
        return showDateTime > now;
      });
      const uniqueDates = Array.from(
        new Map(validShows.map((show) => [show.showDate, show])).values()
      );
      console.log(uniqueDates);

      // // Lấy các ngày chiếu duy nhất
      // const availableDates = Array.from(
      //   new Set(validShows.map((show) => show.showDate))
      // ).sort();

      setAvailableDates(validShows);
      setUniqueAvailableDates(uniqueDates);
    } catch {
      throw new Error("There is an error while getting date");
    }
  };

  const handleGetDateAndShowTime = async (filmID) => {
    try {
      const dateRes = localStorage.getItem("allShows");
      localStorage.removeItem("allShows");
      let response = null;

      if (dateRes) {
        try {
          const parsed = JSON.parse(dateRes);
          if (Array.isArray(parsed)) {
            response = parsed.filter((s) => s.filmId === Number(filmID));
          } else {
            console.warn("Parsed data is not an array:", parsed);
          }
        } catch (error) {
          console.error("Error parsing JSON:", error);
        }
      }

      if (response) {
        console.log("Using cached response:", response);

        setAvailableDates(response);
        setAvailableShowtimesWithFilmType([]);
      } else {
        await fetchDate();
      }
    } catch (error) {
      console.error("Error fetching dates and showtimes:", error);
    }
  };

  //filmdetail
  useEffect(() => {
    const fetchFilmDetail = async () => {
      try {
        const response = await getFilmById(filmID);
        if (response) {
          setFilmDetail(response);
          setIsPopupOpen(true);
        }
      } catch (error) {
        console.error("Error fetching film details:", error);
      }
    };

    fetchFilmDetail();
    const fetchFilmDetailAndShowTime = async () => {
      try {
        await handleGetDateAndShowTime(filmID); // đợi fetch suất chiếu xong
        console.log("Available dates after fetch: ", availableDates);
      } catch (error) {
        console.error("Error fetching film details or show times:", error);
      }
    };

    fetchFilmDetailAndShowTime();
  }, []);

  //cinemalist
  useEffect(() => {
    const fetCinemaList = async () => {
      const rooms = (await getAllRooms())._embedded.roomResponseDtoList;
      const allCinemas = (await getCinemas())._embedded.theaterResponseDtoList;

      const cinemaSchedules = [];

      allCinemas.forEach((cinema) => {
        const schedules = [];

        availableDates.forEach((show) => {
          const room = rooms.find((r) => r.id === show.roomId);
          if (!room) return;

          const isSameCinema = cinema.id === room.theaterId;
          if (!isSameCinema) return;

          const time = show.showTime.slice(0, 5);

          // Kiểm tra nếu chưa tồn tại suất này thì thêm vào
          const alreadyExists = schedules.some(
            (s) => s.showTime === time && s.filmShowId === show.id
          );

          if (!alreadyExists) {
            schedules.push({
              showTime: time,
              filmShowId: show.id, // hoặc show._id tùy theo cấu trúc
            });
          }
        });

        cinemaSchedules.push({
          address: cinema.address,
          city: cinema.city,
          name: cinema.name,
          schedules: {
            Standard: schedules.sort((a, b) =>
              a.showTime.localeCompare(b.showTime)
            ),
          },
        });
      });

      setAvailableCinemaSchedules(cinemaSchedules);
    };

    fetCinemaList();
  }, [availableDates]);

  //useEffect(()=>{console.log("HI" + JSON.stringify(availableShowtimesWithFilmType))},[availableShowtimesWithFilmType])
  useEffect(() => {
    setSelectedShowtime("");
    if (selectedDate) {
      const dateData = availableDates.find((d) => d.date === selectedDate);
      setAvailableShowtimesWithFilmType(dateData?.show || []);
    }
  }, [selectedDate]);

  useEffect(() => {
    if (availableDates.length > 0) {
      if (initShowDate && initShowTime) {
        const initShow = availableDates.find(
          (show) =>
            show.showDate === initShowDate && show.showTime === initShowTime
        );

        if (initShow) {
          setSelectedDate(initShowDate);
          setSelectedShowtime(initShowTime);
        }
      } else {
        const first = availableDates[0];
        setSelectedDate(first.showDate);
        setSelectedShowtime(first.showTime);
      }
    }
  }, [availableDates]);

  const [filmDetail, setFilmDetail] = useState();

  useEffect(() => {
    console.log("show: ", availableDates);
  }, [availableDates]);

  const handleClosePopup = () => {
    setIsPopupOpen(false); // Đóng popup khi người dùng tắt
  };

  useEffect(() => {
    document.title = filmDetail?.name || "Loading...";
  }, [filmDetail]);

  //tag
  useEffect(() => {
    const fetchTags = async () => {
      try {
        const response = await getAllTags();
        if (response) {
          setAllTags(response._embedded.tagResponseDtoList);
        }
      } catch (error) {
        console.error("Error fetching tags: ", error);
      }
    };
    fetchTags();
  }, []);

  // Mapping ageLimit to appropriate category
  const getAgeCategory = (ageLimit) => {
    switch (ageLimit) {
      case "T13":
      case "T16":
        return "TEEN";
      case "T18":
        return "ADULT";
      case "P":
      case "K":
        return "KID";
      default:
        return "ADULT"; // Default case if age is unrecognized
    }
  };
  // Function to get the age description
  const getAgeDescription = (ageRestriction) => {
    switch (ageRestriction) {
      case "T13":
        return "Phim dành cho khán giả từ đủ 13 tuổi trở lên (13+)";
      case "T16":
        return "Phim dành cho khán giả từ đủ 16 tuổi trở lên (16+)";
      case "T18":
        return "Phim dành cho khán giả từ đủ 18 tuổi trở lên (18+)";
      case "P":
        return "Phim dành cho khán giả thiếu nhi (P)";
      case "K":
        return "Phim dành cho khán giả nhỏ tuổi (K)";
      default:
        return ""; // Return an empty string or fallback message
    }
  };

  const [selectedFilmShowID, setSelectedFilmShowID] = useState(null);
  const [selectedFilmShow, setSelectedFilmShow] = useState(null);
  const [ticketSelection, setTicketSelection] = useState([]);
  const [additionalItemSelections, setAdditionalItemSelections] = useState([]);
  const [totalTicket_Single, setTotalTicket_Single] = useState(0);
  const [totalTicket_Pair, setTotalTicket_Pair] = useState(0);

  const [usedSingle, setUsedSingle] = useState(0);
  const [usedPair, setUsedPair] = useState(0);
  const [isBookedApplied, setIsBookedApplied] = useState(false);

  useEffect(() => {
    if (selectedFilmShow) return;
    setRoomDetail(null);
  }, [selectedFilmShow]);
  //FETCH
  //loại vé
  useEffect(() => {
    try {
      const fetchTicketType = async () => {
        const response = await getAllTicketType();
        setTicketSelection(
          response._embedded.ticketTypeResponseDtoList.map((ticketType) => ({
            ...ticketType,
            quantity: 0,
          }))
        );
      };
      fetchTicketType();
    } catch (error) {
      if (error.response) {
        alert(
          `Lấy thông tin loại vé thất bại, lỗi: ` + error.response.data.msg
        );
      } else if (error.request) {
        alert("Không nhận được phản hồi từ server");
      } else {
        alert("Lỗi bất ngờ: " + error.message);
      }
    }
  }, []);
  // sản phẩm ngoài
  useEffect(() => {
    try {
      const fetchAdditionalItem = async () => {
        const response = await getAdditionalItem();
        setAdditionalItemSelections(
          response._embedded.additionalItemResponseDtoList.map(
            (additional) => ({
              ...additional,
              quantity: 0,
            })
          )
        );
      };
      fetchAdditionalItem();
    } catch (error) {
      if (error.response) {
        alert(
          `Lấy thông tin sản phẩm ngoài thất bại, lỗi: ` +
            error.response.data.msg
        );
      } else if (error.request) {
        alert("Không nhận được phản hồi từ server");
      } else {
        alert("Lỗi bất ngờ: " + error.message);
      }
    }
  }, []);

  // Film show detail
  useEffect(() => {
    const fetchFilmShowDetail = async () => {
      if (selectedFilmShowID !== null) {
        try {
          const response = await getFilmShowById(selectedFilmShowID);
          console.log("Selected Film Show Response: ", response);

          setSelectedFilmShow(response);
        } catch (error) {}
      } else {
        setSelectedFilmShow(null);
      }
    };
    fetchFilmShowDetail();
  }, [selectedFilmShowID]);

  //filmshow đã chọn
  useEffect(() => {
    console.log("filmshow đã chọn: ", selectedFilmShow);
  }, [selectedFilmShow]);

  const [roomDetail, setRoomDetail] = useState();
  const [roomSeat, setRoomSeat] = useState();
  const fetchRoom = async () => {
    if (!selectedFilmShow) {
      return;
    }
    try {
      const response = await getRoomById(selectedFilmShow.roomId);

      setRoomDetail(response);
    } catch (error) {
      if (error.response) {
        alert(`Lấy thông tin phòng thất bại, lỗi: ` + error.response.data.msg);
      } else if (error.request) {
        alert("Không nhận được phản hồi từ server");
      } else {
        alert("Lỗi bất ngờ: " + error.message);
      }
    }
  };
  //init room
  useEffect(() => {
    fetchRoom();
  }, [selectedFilmShow]);

  //init room seat
  useEffect(() => {
    const fetchRoomSeats = async () => {
      if (!roomDetail) return;
      try {
        const response = await getRoomSeatByRoomId(roomDetail.id); // Giả sử response chứa mảng ghế

        console.log("Room Seats Response: ", response);

        if (response) {
          const appendedSeat = response.map((row) =>
            row.map((seat) => ({
              ...seat,
              selected: false,
              enabled: false,
              booked: false,
            }))
          );
          setRoomSeat(appendedSeat);
        }
      } catch (error) {
        console.error(" Lỗi khi lấy danh sách ghế:", error);
      }
    };

    fetchRoomSeats();
  }, [roomDetail]);

  const handleSelectSeat = (row, col) => {
    const updatedRoomSeat = roomSeat.map((row) =>
      row.map((seat) => ({ ...seat }))
    );
    if (updatedRoomSeat[row][col - 1].type === "P") {
      col--;
    }
    if (updatedRoomSeat[row][col].type === "") {
      return;
    }
    if (updatedRoomSeat[row][col].booked) {
      return;
    }
    if (updatedRoomSeat[row][col].selected) {
      updatedRoomSeat[row][col].selected = false;
      setRoomSeat(updatedRoomSeat);
      return;
    }
    if (!updatedRoomSeat[row][col].enabled) {
      return;
    }
    updatedRoomSeat[row][col].selected = true;
    setRoomSeat(updatedRoomSeat);
  };
  const updateUsedTicket = () => {
    if (!roomSeat) {
      return;
    }
    let usedSingle = 0,
      usedPair = 0;
    for (let i = 0; i < roomSeat.length; i++) {
      for (let j = 0; j < roomSeat[0].length; j++) {
        if (roomSeat[i][j].selected) {
          if (roomSeat[i][j].type === "P") {
            usedPair++;
          } else {
            usedSingle++;
          }
        }
      }
    }
    setUsedSingle(usedSingle);
    setUsedPair(usedPair);
  };

  // const setBookedSeat = () => {
  //   if (!selectedFilmShow) {
  //     return;
  //   }
  //   const bookedSeatPoss = selectedFilmShow.lockedSeats;
  //   const updatedSeat = roomSeat;
  //   for (const bookedSeatPos of bookedSeatPoss) {
  //     updatedSeat[bookedSeatPos.i][bookedSeatPos.j].booked = true;
  //   }
  //   setRoomSeat(updatedSeat);
  // };
  const setBookedSeat = async () => {
    if (!selectedFilmShow || !selectedFilmShowID || isBookedApplied) return;

    try {
      const response = await getRoomSeatUnuable(selectedFilmShowID);

      const lockedSeats = response?._embedded?.roomSeatResponseDtoList;

      if (!Array.isArray(lockedSeats)) {
        console.warn("lockedSeats không hợp lệ:", lockedSeats);
        return;
      }
      const lockedIds = lockedSeats.map((s) => s.id);

      const updatedSeat = roomSeat.map((row) =>
        row.map((seat) =>
          lockedIds.includes(seat.id) ? { ...seat, booked: true } : { ...seat }
        )
      );

      setRoomSeat(updatedSeat);
      setIsBookedApplied(true);
    } catch (err) {
      console.error("Lỗi khi lấy danh sách ghế đã khóa:", err);
    }
  };

  //update room seat effect
  useEffect(() => {
    if (!roomSeat || !selectedFilmShowID) return;
    setBookedSeat();
  }, [selectedFilmShowID, roomSeat, isBookedApplied]);

  useEffect(() => {
    setIsBookedApplied(false); // khi chọn suất chiếu mới thì reset flag
  }, [selectedFilmShowID]);

  // Khi roomSeat thay đổi → tính số ghế đã dùng
  useEffect(() => {
    if (!roomSeat) return;
    updateUsedTicket();
  }, [roomSeat]);

  //update enable seat
  useEffect(() => {
    if (!roomSeat) {
      return;
    }
    const updatedRoomSeat = [...roomSeat];
    //disable all
    if (usedSingle >= totalTicket_Single) {
      for (let i = 0; i < updatedRoomSeat.length; i++) {
        for (let j = 0; j < updatedRoomSeat[0].length; j++) {
          if (updatedRoomSeat[i][j].type !== "P") {
            updatedRoomSeat[i][j].enabled = false;
          }
        }
      }
    } else {
      for (let i = 0; i < updatedRoomSeat.length; i++) {
        for (let j = 0; j < updatedRoomSeat[0].length; j++) {
          if (updatedRoomSeat[i][j].type !== "P") {
            updatedRoomSeat[i][j].enabled = true;
            //console.log(`${i},${j} ${updatedRoomSeat[i][j].enabled} `)
          }
        }
      }
    }
    setRoomSeat(updatedRoomSeat);
  }, [usedSingle, totalTicket_Single]);
  useEffect(() => {
    if (!roomSeat) {
      return;
    }
    const updatedRoomSeat = [...roomSeat];
    //disable all
    if (usedPair >= totalTicket_Pair) {
      for (let i = 0; i < updatedRoomSeat.length; i++) {
        for (let j = 0; j < updatedRoomSeat[0].length; j++) {
          if (updatedRoomSeat[i][j].type === "P") {
            updatedRoomSeat[i][j].enabled = false;
          }
        }
      }
    } else {
      for (let i = 0; i < updatedRoomSeat.length; i++) {
        for (let j = 0; j < updatedRoomSeat[0].length; j++) {
          if (updatedRoomSeat[i][j].type === "P") {
            updatedRoomSeat[i][j].enabled = true;
          }
        }
      }
    }
    setRoomSeat(updatedRoomSeat);
  }, [usedPair, totalTicket_Pair]);

  const updateTotalTicket = () => {
    let single = 0,
      pair = 0;
    for (let i = 0; i < ticketSelection.length; i++) {
      if (!ticketSelection[i].isPair) {
        single += ticketSelection[i].quantity;
      } else {
        pair += ticketSelection[i].quantity;
      }
    }
    setTotalTicket_Single(single);
    setTotalTicket_Pair(pair);
  };
  //ticket
  useEffect(() => {
    updateTotalTicket();
  }, [ticketSelection]);
  //additional item

  const handleSubmit = () => {};

  if (!filmDetail) {
    return <div>Loading...</div>;
  }

  const getTagById = (id, allTags) => {
    const tag = allTags.find((tag) => tag.id === id);
    return tag ? tag.name : "Không rõ";
  };

  return (
    <div className="p-6 space-y-12 md:space-y-40">
      {isPopupOpen && (
        <ModalWhenBuyTicket isOpen={isPopupOpen} onClose={handleClosePopup} />
      )}

      <div className="grid items-start grid-cols-5 gap-6 md:gap-12 rounded-lg">
        <div className="col-span-2 w-full h-full top-0 text-center relative ">
          <div className="relative border border-gray-300 rounded-lg ">
            {/* Hình ảnh phim */}
            <img
              src={filmDetail.thumbnailUrl}
              alt="Film Thumbnail"
              className="w-full h-full object-cover rounded-lg "
            />

            <div>
              <div className="absolute top-0 left-0 flex items-center">
                <div className="flex items-center">
                  {/* Nhãn 2D */}
                  <div className="flex bg-[#FF9933] w-[33px] h-[35px] lg:w-[71px] lg:h-[78px] justify-center items-center shadow-md">
                    <span className="border-2 border-black p-0.5 text-xs rounded-md font-interBold text-black">
                      2D
                    </span>
                  </div>

                  {/* Chỉ hiển thị 3D nếu is3D === true */}
                  {filmDetail.is3D && (
                    <div className="flex bg-[#663399] w-[33px] h-[35px] lg:w-[71px] lg:h-[78px] justify-center items-center shadow-md">
                      <span className="border-2 border-white p-0.5 text-xs rounded-md font-interBold text-white">
                        3D
                      </span>
                    </div>
                  )}
                  {/* Nhãn T13, T16, T18, P, K */}
                  <div className="flex flex-col w-[33px] h-[35px] lg:w-[71px] lg:h-[78px] items-center justify-center bg-[#FF0033] shadow-md">
                    <span className="text-white font-interBold overflow-hidden text-sm">
                      {filmDetail.ageRestriction}
                    </span>
                    <span className="px-0.5 bg-black text-white font-interBold text-[0.5rem] tracking-widest">
                      {getAgeCategory(filmDetail.ageRestriction)}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="col-span-3 pt-6 space-y-10">
          <h1 className="text-5xl rounded-full w-full h-6 flex items-center justify-start font-interExtraBold">
            {filmDetail.name}
          </h1>

          <div className="flex flex-col items-start justify-start space-y-4 text-left w-full mt-4 film-info">
            <p className="flex items-center mt-2 text-2xl">
              <FaTag className="icon-style" />
              Thể loại:{" "}
              {filmDetail.tagIds
                .map((id) => getTagById(id, allTags))
                .join(", ")}
            </p>
            <p className="flex items-center mt-2 text-2xl">
              <FaRegClock className="icon-style" />
              {`${filmDetail.duration} phút`}
            </p>
            <p className="flex items-center mt-2 text-2xl">
              <FaGlobeAmericas className="icon-style" />
              Quốc gia: {filmDetail.originatedCountry}
            </p>
            <p className="flex items-center mt-2 text-2xl">
              <FaCommentDots className="icon-style" />
              Phụ đề: {filmDetail.voice}
            </p>
            <p className="flex items-center mt-2 text-2xl">
              <LuUserRoundCheck className="icon-style" />{" "}
              <span className="bg-mainColor text-white">
                {filmDetail.ageRestriction}:{" "}
                {getAgeDescription(filmDetail.ageRestriction)}
              </span>
            </p>
          </div>
          <div>
            <FilmInfoSection
              className="hidden md:block"
              filmContent={filmDetail.content}
              filmDescription={filmDetail.description}
            />
          </div>
          <button
            className="flex items-center text-[1.5rem]"
            onClick={() => setVideoOpen(true)}
          >
            <div className="flex items-center justify-center mr-2">
              <FaRegCirclePlay className="w-[31px] h-[31px] text-[#fe1e3e] bg-[#d9d9d9] rounded-full" />
            </div>
            <span className="text-white border-b-2">Xem trailer</span>
          </button>
        </div>
        {/* Sử dụng TrailerModal */}
        <TrailerModal
          videoOpen={videoOpen}
          setVideoOpen={setVideoOpen}
          videoUrl={filmDetail.trailerUrl}
        />
      </div>

      <FilmInfoSection
        className="block md:hidden mt-6"
        filmContent={filmDetail.content}
        filmDescription={filmDetail.description}
      />
      <div>
        <div className="flex flex-col justify-center items-center space-y-12">
          <h1 className="font-interExtraBold">LỊCH CHIẾU</h1>
          <div className="flex flex-wrap justify-center items-center mt-6 gap-4">
            {uniqueAvailableDates.map((dateGroup) => {
              return (
                <ScheduleChooseBox
                  date={dateGroup.showDate}
                  isSelected={selectedDate === dateGroup.showDate}
                  onClick={() => setSelectedDate(dateGroup.showDate)}
                />
              );
            })}
          </div>
          <CinemaScheduleList
            cinemasData={availableCinemaSchedules}
            selectedShowtime={selectedShowtime}
            onSelectShowtime={(showTime, filmShowId) => {
              setSelectedShowtime(showTime);
              setSelectedFilmShowID(filmShowId);
            }}
          />

          {/* {availableShowtimesWithFilmType?.map((dataGroup) => {
            return (
              <div className="flex flex-col justify-center items-center space-y-2">
                <h1 className="font-interExtraBold">SUẤT CHIẾU</h1>
                <h2 className="font-interBold">{dataGroup.showType}</h2>
                <hr className="text-white w-full p-1"></hr>
                <div className="flex flex-wrap justify-center items-center mt-6 gap-4">
                  {dataGroup?.showTimes?.map((value) => {
                    return (
                      <ShowtimeChooseBox
                        time={value.showTime}
                        isSelected={selectedShowtime === value.showTime}
                        onClick={() => {
                          setSelectedShowtime(value.showTime);
                          setSelectedFilmShowID(value._id);
                        }}
                      />
                    );
                  })}
                </div>
              </div>
            );
          })} */}
        </div>
      </div>

      {selectedFilmShow && (
        <div className="flex flex-col justify-center items-center space-y-12">
          <h1 className="font-interExtraBold">CHỌN LOẠI VÉ</h1>
          <div className="flex flex-wrap lg:grid lg:grid-cols-3 justify-center items-center mt-6 gap-4 lg:gap-8">
            {ticketSelection.map((ticketType) => {
              return (
                <div className="ticketBox">
                  <span
                    style={{ fontWeight: "medium" }}
                    className="text-xl group-hover:text-[#f2ea28]"
                  >
                    {ticketType.title}
                  </span>
                  <span className="text-lg">
                    {formatCurrencyNumber(ticketType.price) + "VNĐ"}
                  </span>
                  <QuantitySelectorV2
                    quantity={ticketType.quantity}
                    onIncrement={(e) => {
                      let updatedQuantity = ticketType.quantity + 1;
                      if (updatedQuantity > 8) {
                        alert("Bạn chỉ có thể mua tối đa 8 vé loại này");
                      } else {
                        setTicketSelection((prev) =>
                          prev.map(
                            (item) =>
                              item.id === ticketType.id // Match by id
                                ? { ...item, quantity: updatedQuantity } // Update the quantity for the matched item
                                : item // Keep other items unchanged
                          )
                        );
                      }
                    }}
                    onDecrement={(e) => {
                      let updatedQuantity = ticketType.quantity - 1;
                      if (updatedQuantity < 0) {
                        updatedQuantity = 0; // Parse the new quantity
                      } else {
                        setTicketSelection((prev) =>
                          prev.map(
                            (item) =>
                              item.id === ticketType.id // Match by id
                                ? { ...item, quantity: updatedQuantity } // Update the quantity for the matched item
                                : item // Keep other items unchanged
                          )
                        );
                      }
                    }}
                  />
                </div>
              );
            })}
          </div>
        </div>
      )}
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        {roomDetail?.name && Array.isArray(roomSeat) && roomSeat.length > 0 && (
          <RoomDisplay
            roomSeat={roomSeat}
            roomName={roomDetail.name}
            center={{
              x1: roomDetail.centerX1,
              x2: roomDetail.centerX2,
              y1: roomDetail.centerY1,
              y2: roomDetail.centerY2,
            }}
            handleSelectSeat={handleSelectSeat}
          />
        )}
      </div>
      {selectedFilmShow && (
        <div className="flex flex-col justify-center items-center space-y-12">
          <h1 className="font-interExtraBold">CHỌN BẮP NƯỚC</h1>
          <div className="flex flex-wrap justify-center items-center mt-6 gap-4 md:gap-8">
            {additionalItemSelections.map((food) => {
              return (
                <FoodCardV2
                  food={food}
                  quantity={food.quantity}
                  onIncrement={(e) => {
                    let updatedQuantity = food.quantity + 1;
                    if (updatedQuantity > 4) {
                      alert("Bạn chỉ có thể mua tối đa 4 sản phẩm loại này");
                      return;
                    }
                    setAdditionalItemSelections((prev) =>
                      prev.map(
                        (item) =>
                          item.id === food.id // Match by id
                            ? { ...item, quantity: updatedQuantity } // Update the quantity for the matched item
                            : item // Keep other items unchanged
                      )
                    );
                  }}
                  onDecrement={(e) => {
                    let updatedQuantity = food.quantity - 1;
                    if (updatedQuantity < 0) {
                      updatedQuantity = 0; // Parse the new quantity
                    }
                    setAdditionalItemSelections((prev) =>
                      prev.map(
                        (item) =>
                          item.id === food.id // Match by id
                            ? { ...item, quantity: updatedQuantity } // Update the quantity for the matched item
                            : item // Keep other items unchanged
                      )
                    );
                  }}
                />
              );
            })}
          </div>
        </div>
      )}

      {roomDetail && roomSeat && (
        <BottomBar
          filmName="Alibaba"
          date="20-12-2024"
          time="10:30"
          roomName={roomDetail.name}
          seatSelections={roomSeat}
          ticketSelections={ticketSelection}
          additionalItemSelections={additionalItemSelections}
          selectedFilmShowId={selectedFilmShowID}
          totalDiscount={totalDiscount} // Truyền tổng khuyến mãi
          selectedPromotions={selectedPromotions} // Truyền danh sách khuyến mãi
        />
      )}
      {selectedFilmShow && (
        <PromotionList
          isOpen={isPromotionListOpen}
          setIsOpen={setIsPromotionListOpen}
          onApplyPromotions={(selectedPromotions, totalDiscount) => {
            setSelectedPromotions(selectedPromotions); // Lưu danh sách khuyến mãi
            setTotalDiscount(totalDiscount); // Lưu tổng khuyến mãi
            console.log("Các khuyến mãi đã chọn:", selectedPromotions);
            console.log("Tổng khuyến mãi:", totalDiscount, "%");
          }}
        />
      )}

      {/* Nút mở sidebar PromotionList */}
      {selectedFilmShow && !isPromotionListOpen && (
        <button
          onClick={() => setIsPromotionListOpen(true)}
          className="fixed inset-y-1/2 right-0 transform -translate-y-1/2 text-white px-4 py-2 rounded-l-lg shadow-lg flex items-center justify-center"
          style={{
            position: "fixed",
            top: "30%",
            right: "0",
            transform: "translateY(-50%)",
          }}
        >
          <FaArrowLeft size={20} />
        </button>
      )}
    </div>
  );
};

function RoomDisplay({ roomSeat, roomName, handleSelectSeat, center }) {
  let flag = false;
  return (
    <div className="RoomDisplay">
      <h1>{roomName}</h1>
      <div style={{ zIndex: 1 }} className="screen">
        <img src={whiteScreen} alt="Screen" />
        <h1 className="center-text">Màn hình</h1>
      </div>

      <div className="Create_RoomSeats">
        <div className="col">
          {roomSeat.map((row, rowIndex) => (
            <div key={rowIndex} className="row">
              <span className="row-label">
                {String.fromCharCode(65 + rowIndex)}
              </span>
              <div className="seatRow flex flex-wrap gap-x-6 gap-y-5">
                {
                  // Use for loop to iterate over the seats in the row
                  (() => {
                    const seatSlots = [];
                    for (
                      let seatIndex = 0;
                      seatIndex < row.length;
                      seatIndex++
                    ) {
                      const seat = row[seatIndex];
                      if (seat.type === "") {
                        seatSlots.push(
                          <SeatSlot key={seatIndex} seatType={seat.type}>
                            {!flag &&
                              center.x1 >= 0 &&
                              center.y1 >= 0 &&
                              center.x2 >= 0 &&
                              center.y2 >= 0 &&
                              center.x2 >= center.x1 &&
                              center.y2 >= center.y1 && (
                                <>
                                  {(flag = true)}
                                  <div
                                    style={{
                                      position: "absolute",
                                      borderColor: "red",
                                      borderRadius: "5px",
                                      borderWidth: "2px",
                                      borderStyle: "solid",
                                      top: -4 + center.x1 * (seatHeight + gapY),
                                      left: -4 + center.y1 * (seatWidth + gapX),
                                      width:
                                        (center.y2 - center.y1 + 1) *
                                          seatWidth +
                                        (center.y2 - center.y1) * gapX +
                                        7 +
                                        0,
                                      height:
                                        (center.x2 - center.x1 + 1) *
                                          seatHeight +
                                        (center.x2 - center.x1) * gapY +
                                        7 +
                                        0,
                                      boxSizing: "border-box",
                                      zIndex: -1,
                                    }}
                                  />
                                </>
                              )}
                          </SeatSlot>
                        );
                      } else {
                        seatSlots.push(
                          <SeatSlot
                            key={seatIndex}
                            selected={seat.selected}
                            disabled={seat.booked || !seat.enabled}
                            label={seat.name}
                            seatType={seat.type}
                            handleOnClick={() =>
                              handleSelectSeat(rowIndex, seatIndex)
                            }
                          >
                            {!flag &&
                              center.x1 >= 0 &&
                              center.y1 >= 0 &&
                              center.x2 >= 0 &&
                              center.y2 >= 0 &&
                              center.x2 >= center.x1 &&
                              center.y2 >= center.y1 && (
                                <>
                                  {(flag = true)}
                                  <div
                                    style={{
                                      position: "absolute",
                                      borderColor: "red",
                                      borderRadius: "10px",
                                      borderWidth: "4px",
                                      borderStyle: "solid",
                                      top: -4 + center.x1 * (seatHeight + gapY),
                                      left: -4 + center.y1 * (seatWidth + gapX),
                                      width:
                                        (center.y2 - center.y1 + 1) *
                                          seatWidth +
                                        (center.y2 - center.y1) * gapX +
                                        7 +
                                        0,
                                      height:
                                        (center.x2 - center.x1 + 1) *
                                          seatHeight +
                                        (center.x2 - center.x1) * gapY +
                                        7 +
                                        0,
                                      boxSizing: "border-box",
                                      zIndex: -1,
                                    }}
                                  />
                                </>
                              )}
                          </SeatSlot>
                        );
                      }
                      if (seat.type === "P") {
                        seatIndex++;
                      }
                    }
                    return seatSlots;
                  })()
                }
              </div>
            </div>
          ))}
        </div>
        <SeatLegend />
      </div>
    </div>
  );
}

function SeatSlot({
  label,
  seatType,
  handleOnClick,
  selected,
  disabled,
  children,
}) {
  if (seatType === "") {
    return (
      <div onClick={handleOnClick} className={"Create_SeatSlot_Empty "}>
        {children}
      </div>
    );
  } else if (seatType === "N") {
    return (
      <div
        onClick={handleOnClick}
        className={
          "Create_SeatSlot_Normal " +
          (selected ? "bgS" : disabled ? "dN" : "bgN")
        }
      >
        {label}
        {children}
      </div>
    );
  } else if (seatType === "V") {
    return (
      <div
        onClick={handleOnClick}
        className={
          "Create_SeatSlot_VIP " + (selected ? "bgS" : disabled ? "dV" : "bgV")
        }
      >
        {label}
        {children}
      </div>
    );
  } else if (seatType === "P") {
    return (
      <div
        onClick={handleOnClick}
        className={
          "Create_SeatSlot_Pair " + (selected ? "bgS" : disabled ? "dP" : "bgP")
        }
      >
        {label}
        {children}
      </div>
    );
  }
}
function SeatLegend() {
  return (
    <div className="Room-Legend">
      <div className="item">
        <div className="box-unselected" />
        Trống
      </div>
      <div className="item">
        <div className="box-normal" />
        Ghế thường
      </div>
      <div className="item">
        <div className="box-VIP" />
        Ghế VIP
      </div>
      <div className="item">
        <div className="box-pair" />
        Ghế đôi
      </div>
      <div className="item">
        <div className="box-selected" />
        Đang chọn
      </div>
      <div className="item">
        <div className="box-booked" />
        Đã được đặt/ <br /> Không thể chọn
      </div>
    </div>
  );
}
function BottomBar({
  roomName,
  seatSelections,
  ticketSelections,
  additionalItemSelections,
  selectedFilmShowId,
  totalDiscount, // Thêm prop
  selectedPromotions, // Thêm prop
}) {
  const [usePoints, setUsePoints] = useState(false);
  const [loyalPoint, setLoyalPoint] = useState(0);
  const { user } = useAuth(); // Lấy user từ context
  const [paymentUrl, setPaymentUrl] = useState(null); // State quản lý URL thanh toán
  const [param, setParam] = useState(null);
  const [pointUsage, setPointUsage] = useState(null);
  const [priceAfterAll, setPriceAfterAll] = useState(0);
  const [clientSecret, setClientSecret] = useState(null);
  const navigate = useNavigate();

  const handleCreatePayment = async () => {
    if (!localStorage.getItem("accessToken")) {
      alert("Bạn cần phải đăng nhập trước khi thực hiện thanh toán");
      navigate("/auth");
      return;
    }

    try {
      console.log("Danh sách promotionIds:", promotionIds);
      const payload = {
        totalPrice: calculateTotalPrice(),
        totalPriceAfterDiscount: priceAfterAll,
        filmShowId: selectedFilmShowId,
        tickets: ticketSelections
          .filter((t) => t.quantity > 0)
          .map((t) => ({
            typeId: t.id,
            quantity: t.quantity,
          })),
        seatIds: seatSelections
          .flat()
          .filter((s) => s.selected)
          .map((s) => s.id),
        items: additionalItemSelections
          .filter((i) => i.quantity > 0)
          .map((i) => ({
            id: i.id,
            quantity: i.quantity,
          })),
        promotionIds: selectedPromotions.map((p) => p.id),
        loyalPoint: Math.floor(
          (priceAfterAll * param?.loyalPointOrderToPointRatio) / 100
        ),
        pointUsage: usePoints ? pointUsage : 0,
      };

      console.log("Payload gửi:", payload);

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
    }
  };
  // Lấy danh sách promotionId từ selectedPromotions
  const promotionIds = selectedPromotions.map((promo) => promo.id);

  // Log danh sách promotionIds để kiểm tra

  useEffect(() => {
    if (!param) return;
    if (usePoints === false) setPointUsage(null);

    const pointUsage = Math.min(
      param.loyalPointMaximumPointUseInOneGo,
      loyalPoint
    );

    setPointUsage(pointUsage);
  }, [usePoints]);

  const handleTogglePoints = () => {
    if (usePoints === false && loyalPoint === 0) {
      alert(`Bạn không có điểm để sử dụng`);
      return;
    }

    if (
      usePoints === false &&
      calculateTotalPrice() < param.loyalPointMinimumValueToUseLoyalPoint
    ) {
      alert(
        `Bạn có thể sử dụng điểm cho hóa hóa đơn từ : ${param.loyalPointMinimumValueToUseLoyalPoint.toLocaleString()} VNĐ`
      );
      return;
    }

    if (
      usePoints === false &&
      loyalPoint > param.loyalPointMaximumPointUseInOneGo
    ) {
      alert(
        `Điểm sử dụng tối đa trong một lần là ${param.loyalPointMaximumPointUseInOneGo}. Phần dư ra có thể được sử dụng lại cho lần sau.`
      );
      setUsePoints(!usePoints);
      return;
    }
    setUsePoints(!usePoints);
  };

  useEffect(() => {
    setUsePoints(false);
  }, [seatSelections, ticketSelections, additionalItemSelections]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const user = JSON.parse(localStorage.getItem("user"));
        if (user && user.loyalPoint !== undefined) {
          setLoyalPoint(user.loyalPoint);
        } else {
          console.error("Không tìm thấy loyalPoint trong localStorage");
        }

        const paramResponse = await getParam();
        console.log("Param response:", paramResponse);

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

  const calculateTotalPrice = () => {
    let total = 0;
    let vCount = 0;
    for (let i = 0; i < ticketSelections.length; i++) {
      total += ticketSelections[i].quantity * ticketSelections[i].price;
    }
    for (let i = 0; i < seatSelections.length; i++) {
      for (let j = 0; j < seatSelections[i].length; j++) {
        if (seatSelections[i][j].selected) {
          //console.log(seatSelections[i][j].seatType)
          if (seatSelections[i][j].type === "V") {
            vCount++;
          }
        }
      }
    }
    if (param) {
      console.log;
      total += vCount * param.addedPriceForVipSeat;
    }

    for (let i = 0; i < additionalItemSelections.length; i++) {
      total +=
        additionalItemSelections[i].quantity *
        additionalItemSelections[i].price;
    }
    return total;
  };

  useEffect(() => {
    if (!param) return;
    if (usePoints === false) setPointUsage(null);

    const pointUsage = Math.min(
      param.loyalPointMaximumPointUseInOneGo,
      loyalPoint
    );

    setPointUsage(pointUsage);

    const price = !usePoints
      ? calculateTotalPrice() - (calculateTotalPrice() * totalDiscount) / 100
      : calculateTotalPrice() -
          (calculateTotalPrice() * totalDiscount) / 100 -
          (pointUsage * param.loyalPointPointToReducedPriceRatio) / 100 <
        0
      ? 0
      : calculateTotalPrice() -
        (calculateTotalPrice() * totalDiscount) / 100 -
        (pointUsage * param.loyalPointPointToReducedPriceRatio) / 100;

    setPriceAfterAll(price);
  }, [
    usePoints,
    seatSelections,
    additionalItemSelections,
    totalDiscount,
    param,
  ]);

  return (
    <div
      style={{ zIndex: 5, width: "-webkit-fill-available" }}
      className="flex justify-between items-center px-24 bg-[#0f172a] text-white sticky bottom-0 py-4"
    >
      {/* Phần Hóa đơn */}
      <div
        style={{ fontSize: "16px" }}
        className="flex flex-col items-start max-w-xl w-full"
      >
        <h1 className="text-3xl font-bold">HÓA ĐƠN</h1>
        {(() => {
          let string = "";
          let exist = false;
          for (let i = 0; i < ticketSelections.length; i++) {
            if (ticketSelections[i].quantity > 0) {
              if (string !== "") {
                string = string.concat(
                  `, ${ticketSelections[i].quantity}x ${ticketSelections[i].title}`
                );
              } else {
                string = string.concat(
                  `${ticketSelections[i].quantity}x ${ticketSelections[i].title}`
                );
              }

              exist = true;
            }
          }
          if (exist) {
            return (
              <>
                Thông tin vé: {string}
                <br />
              </>
            );
          }
          return null;
        })()}
        {/*seat*/}
        {(() => {
          let string = "";
          let exist = false;
          for (let i = 0; i < seatSelections.length; i++) {
            for (let j = 0; j < seatSelections[i].length; j++) {
              if (seatSelections[i][j].selected) {
                if (string === "") {
                  string = string.concat(seatSelections[i][j].name);
                } else {
                  string = string.concat(`, ${seatSelections[i][j].name}`);
                }
                exist = true;
              }
            }
          }
          if (exist) {
            return (
              <>
                Tên phòng: {roomName}| Các ghế đã chọn: {string}
                <br />
              </>
            );
          }
          return null;
        })()}
        {/*other*/}
        {(() => {
          let vCount = 0;
          let vExist = false;
          for (let i = 0; i < seatSelections.length; i++) {
            for (let j = 0; j < seatSelections[i].length; j++) {
              if (seatSelections[i][j].selected) {
                //console.log(seatSelections[i][j].seatType)
                if (seatSelections[i][j].seatType === "V") {
                  vCount++;
                  vExist = true;
                }
              }
            }
          }
          let string = "Khác: ";
          if (vExist) {
            if (vExist) {
              string = string.concat(`${vCount}x ghế VIP `);
            }
            return (
              <>
                {string}
                <br />
              </>
            );
          }
          return null;
        })()}
        {/*additional item*/}
        {(() => {
          let exist = false;
          for (let i = 0; i < additionalItemSelections.length; i++) {
            if (additionalItemSelections[i].quantity > 0) {
              exist = true;
            }
          }
          if (exist) {
            return (
              <>
                Sản phẩm ngoài:
                <br />
                {additionalItemSelections.map((element) => {
                  if (element.quantity > 0) {
                    return (
                      <div key={element.id}>
                        {element.quantity}x {element.name}
                        <br />
                      </div>
                    );
                  }
                  return null;
                })}
                <br />
              </>
            );
          }
        })()}
      </div>

      <div
        style={{ width: "100%" }}
        className="flex flex-col items-end max-w-md w-full border-l-2 pl-6 py-4"
      >
        <div className="flex flex-col w-full">
          <div className="flex justify-between">
            <p className="text-lg">Tạm tính</p>
            <p className="text-xl font-bold">
              {calculateTotalPrice().toLocaleString()} VNĐ
            </p>
          </div>
          <div className="flex justify-between">
            <p className="text-lg">Khuyến mãi</p>
            <p className="text-xl font-bold">{totalDiscount}%</p>
          </div>
          <div className="flex justify-between">
            <p className="text-lg">Tổng tiền</p>
            <p className="text-xl font-bold">
              {priceAfterAll.toLocaleString()}
              VNĐ
            </p>
          </div>
          <div className="flex justify-between">
            <p className="text-lg">Điểm tích được </p>
            <p className="text-xl font-bold">
              +{" "}
              {Math.floor(
                (priceAfterAll * param?.loyalPointOrderToPointRatio) / 100
              ).toLocaleString()}
            </p>
          </div>
          <div className="flex justify-between items-center">
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
          {usePoints && (
            <div className="flex justify-between">
              <p className="text-lg">Đã sử dụng </p>
              <p className="text-xl font-bold">
                {pointUsage?.toLocaleString()}
              </p>
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
            text={"Đặt ngay"} // Hiển thị text thay đổi khi đang xử lý
          />
        </div>
      </div>
    </div>
  );
}

export default FilmDetailPage;
