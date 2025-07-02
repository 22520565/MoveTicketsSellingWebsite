import React, { useState, useEffect } from "react";
import { FaPlay, FaInfoCircle } from "react-icons/fa";
import DatePicker from "react-datepicker";
import { IoClose } from "react-icons/io5";
import {
  getAllFilmShows,
  getFilmById,
  getRoomById,
  getAllRooms,
  getAllTags,
  getCinemas,
  getFilmStatistics,
} from "../../config/api";

const FilmShowChartPage = () => {
  const [selectedCinemaId, setSelectedCinemaId] = useState(""); // rạp đang chọn
  const [cinemas, setCinemas] = useState([]); // danh sách rạp

  const [rooms, setRooms] = useState([]); // State cho rooms
  const [allTags, setAllTags] = useState([]);
  const [events, setEvents] = useState([]); // State cho events

  const [selectedEvent, setSelectedEvent] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [startDate, setStartDate] = useState(
    new Date().toISOString().split("T")[0]
  );

  useEffect(() => {
    const fetchCinemas = async () => {
      try {
        const res = await getCinemas(); // hoặc endpoint tương ứng

        setCinemas(res.data._embedded.theaterResponseDtoList);
        setSelectedCinemaId(
          res.data._embedded.theaterResponseDtoList[0]?.id || ""
        );
      } catch (err) {
        console.error("Lỗi lấy danh sách rạp:", err);
      }
    };

    fetchCinemas();
  }, []);

  // const fetchData = async (date) => {
  //   try {
  //     const tagRes = await getAllTags();
  //     setAllTags(tagRes.data._embedded.tagResponseDtoList);

  //     const roomRes = await getAllRooms();
  //     setRooms(roomRes.data._embedded.roomResponseDtoList);

  //     const response = await getAllFilmShows();

  //     const data = response.data._embedded.filmShowResponseDtoList;

  //     const formattedDate = (typeof date === "string" ? new Date(date) : date)
  //       ?.toISOString()
  //       .split("T")[0];

  //     const filteredShows = data.filter(
  //       (show) => show.showDate === formattedDate
  //     );

  //     const processedData = await Promise.all(
  //       filteredShows.map(async (event) => {
  //         // Gọi API lấy thông tin phim theo filmId
  //         const filmRes = await getFilmById(event.filmId);
  //         const filmData = filmRes.data;

  //         // Gọi API lấy thông tin phòng
  //         const roomRes = await getRoomById(event.roomId);
  //         const roomData = roomRes.data;

  //         console.log(filmData);

  //         const categoryNames = filmData.tagIds
  //           .map((id) => allTags.find((tag) => tag.id === id)?.name)
  //           .filter(Boolean);

  //         return {
  //           ...event,

  //           room: roomData.name,
  //           film: filmData.name,
  //           duration: filmData.duration,
  //           category: categoryNames.join(", "),
  //           description: filmData.description,
  //         };
  //       })
  //     );

  //     setEvents(processedData); // hoặc xử lý tiếp
  //   } catch (error) {
  //     console.error("Lỗi khi tải dữ liệu:", error);
  //   }
  // };

  // useEffect(() => {
  //   console.log("Events:", events);
  // }, [events]);

  //fetch Event
  useEffect(() => {
    const fetchCinemas = async () => {
      if (!startDate || !selectedCinemaId) {
        console.warn(" startDate hoặc selectedCinemaId chưa sẵn sàng");
        return;
      }
      try {
        const res = await getFilmStatistics({
          date: startDate,
          theaterId: selectedCinemaId,
        }); // hoặc endpoint tương ứng

        setEvents(res.data.events || []);
        setRooms(res.data.roomNames || []);
      } catch (err) {
        console.error("Lỗi lấy danh sách rạp:", err);
      }
    };

    fetchCinemas();
  }, [startDate, selectedCinemaId]);
  // Gọi API mỗi khi selectedDate thay đổi
  // useEffect(() => {
  //   fetchData(startDate);
  // }, [startDate]);

  const getEventStyle = (startTime, duration, isSpanningEvent) => {
    const slotWidth = 50;
    let width, left;
    if (isSpanningEvent) {
      const widthPx = (Math.min(duration, 24 * 60) / 30) * slotWidth;
      width = `${widthPx}px`;
      left = "25px";
    } else {
      let endTime = startTime + duration;
      // let startPosition, endPosition;

      // if (endTime > 24) {
      //   // Event spans across midnight
      //   startPosition = startTime * 100;
      //   endPosition = 24 * 100 - 25;
      // } else {
      //   startPosition = startTime * 100;
      //   endPosition = endTime * 100;
      // }

      // width = `${endPosition - startPosition}px`;
      // left = `${startPosition + 25}px`;
      const clampedEnd = Math.min(endTime, 24 * 60);
      const widthPx = ((clampedEnd - startTime) / 30) * slotWidth;
      const leftPx = (startTime / 30) * slotWidth + 25;
      width = `${widthPx}px`;
      left = `${leftPx}px`;
    }
    console.log(
      `start: ${startTime}m, duration: ${duration}m → left: ${left}, width: ${width}`
    );
    return { width, left };
  };

  const getCategoryColor = (category) => {
    const colors = {
      Empty: "bg-blue-300",
      "Sci-Fi": "bg-blue-500",
      Action: "bg-red-500",
      Crime: "bg-purple-500",
    };
    return colors[category] || "bg-blue-500";
  };

  const handleEventClick = (event) => {
    setSelectedEvent(event);
    setShowModal(true);
  };

  // Hàm định dạng ngày tháng
  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  };

  //Hiển thị time trong modal
  const formatTime = (timeStrOrMinutes) => {
    // Nếu là chuỗi: "14:30:00"
    if (typeof timeStrOrMinutes === "string") {
      const [h, m] = timeStrOrMinutes.split(":").map(Number);
      return `${h.toString().padStart(2, "0")}:${m
        .toString()
        .padStart(2, "0")}`;
    }

    // Nếu là phút: số nguyên
    const hour = Math.floor(timeStrOrMinutes / 60);
    const minute = timeStrOrMinutes % 60;
    return `${hour.toString().padStart(2, "0")}:${minute
      .toString()
      .padStart(2, "0")}`;
  };

  const getMinutesFromTime = (timeStr) => {
    if (!timeStr || typeof timeStr !== "string") return 0;
    const [h, m] = timeStr.split(":").map(Number);
    return h * 60 + m;
  };

  ////////////////////////////////////

  const formatTime2 = (index) => {
    const hour = Math.floor(index / 2); // Chuyển chỉ số thành giờ
    const minute = index % 2 === 0 ? "00" : "30"; // Kiểm tra nếu là mốc 0.5 (ví dụ 1:30)
    return `${hour.toString().padStart(2, "0")}:${minute}`;
  };

  const formatDuration = (duration) => {
    const hours = Math.floor(duration / 60);
    const minutes = duration % 60;
    return `${hours}h ${minutes}m`;
  };

  const filteredEvents = events.filter((event) => {
    const eventDate = new Date(event.date);
    const startDateObj = new Date(startDate);
    if (eventDate.getTime() === startDateObj.getTime()) {
      // Same day event
      return true;
    }
    // Event started the previous day and continues into the current day
    if (
      eventDate.getTime() === startDateObj.getTime() - 86400000 &&
      event.showTime + event.duration > 24
    ) {
      return true;
    }
    return false;
  });

  useEffect(() => {
    console.log("🔍 Rooms changed:", rooms);
  }, [rooms]);

  useEffect(() => {
    if (filteredEvents.length > 0) {
      console.log("🎬 Filtered Events changed:", filteredEvents);
    }

    if (events.length > 0) console.log("🎬 All Events changed:", events);
  }, [filteredEvents]);

  // const filteredEvents = data.events;
  return (
    <div className="p-8 bg-gray-100 min-h-screen">
      <h1 className="text-3xl font-bold mb-8 text-gray-800">
        Biểu đồ suất phim
      </h1>

      {/* Date Selection */}
      <div className="mb-6 flex gap-4">
        <div className="flex items-center">
          <label className="mr-2 text-gray-700">Chọn ngày:</label>
          {/* <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="border rounded-md p-2 "
          /> */}
          <DatePicker
            selected={startDate}
            onChange={(date) => setStartDate(date)}
            dateFormat="dd/MM/yyyy"
            className="text-center border rounded-md p-2 w-[150px]"
          />
        </div>
        <select
          value={selectedCinemaId}
          onChange={(e) => setSelectedCinemaId(e.target.value)}
          className="p-2 border rounded-md"
        >
          {cinemas.map((cinema) => (
            <option key={cinema.id} value={cinema.id}>
              {cinema.name}
            </option>
          ))}
        </select>
      </div>
      <div className="mb-8">
        <h2 className="text-xl font-semibold mb-4">{formatDate(startDate)}</h2>

        <div className="relative overflow-x-auto">
          {filteredEvents.length === 0 ? (
            <h1 style={{ textAlign: "center" }}>Không có lịch chiếu</h1>
          ) : (
            <>
              <div className="flex ml-40 mb-4 ">
                {Array.from({ length: 48 }, (_, i) => (
                  <div
                    key={i}
                    className="flex-shrink-0 w-[50px] text-sm text-gray-600 text-center"
                  >
                    {formatTime2(i)}
                  </div>
                ))}
              </div>
              <div
                className="relative bg-white" // Thêm nền trắng
                style={{ minWidth: "2540px" }}
              >
                {rooms.map((roomName) => {
                  const filteredRoomEvents = filteredEvents.filter(
                    (event) => event.roomName === roomName
                  );

                  return (
                    <div
                      key={`${roomName}-${startDate}`}
                      className="flex items-center h-20 border-t border-gray-300"
                    >
                      <div className="w-40 flex-shrink-0 font-medium text-gray-700 pr-4 pl-2">
                        {roomName}
                      </div>

                      {/* Cột timeline */}
                      <div className="relative h-20 flex-1  border-t bg-white">
                        {filteredRoomEvents.length === 0 && (
                          <div className="absolute inset-0 flex items-center justify-center text-gray-500 text-xl">
                            Không có lịch phim
                          </div>
                        )}

                        {filteredRoomEvents.map((event) => {
                          const eventStartMinutes = getMinutesFromTime(
                            event.startTime
                          );

                          const isSpanningEvent =
                            new Date(event.date).getTime() <
                            new Date(startDate).getTime();

                          const adjustedStartTime = isSpanningEvent
                            ? 0
                            : eventStartMinutes;
                          const adjustedDuration = event.duration;

                          return (
                            <div
                              key={event.id}
                              className={`absolute top-2 bottom-2 rounded-lg ${getCategoryColor(
                                event.category
                              )} text-white cursor-pointer transition-transform hover:scale-y-110`}
                              style={getEventStyle(
                                adjustedStartTime,
                                adjustedDuration,
                                isSpanningEvent
                              )}
                              onClick={() => handleEventClick(event)}
                            >
                              <div className="p-2 text-sm flex items-center h-full">
                                <FaPlay className="mr-2" />
                                <span className="truncate">
                                  {event.filmName}
                                </span>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  );
                })}
              </div>
            </>
          )}
        </div>
      </div>

      {/* Modal */}
      {showModal && selectedEvent && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg max-w-md w-full">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">{selectedEvent.filmName}</h2>
              <button
                onClick={() => setShowModal(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                <IoClose className="h-5 w-5" />
              </button>
            </div>
            <div className="mb-4">
              <p className="text-gray-600">
                {selectedEvent.description.split("\n").map((line, index) => (
                  <p key={index}>
                    {index === 0 && <FaInfoCircle className="inline mr-2" />}
                    {line}
                  </p>
                ))}
              </p>
            </div>
            <div className=" text-gray-500">
              <p>Phòng: {selectedEvent.roomName}</p>
              <p>Ngày: {selectedEvent.date}</p>
              <p>
                Time: {formatTime(selectedEvent.startTime)} -{" "}
                {formatTime(
                  getMinutesFromTime(selectedEvent.startTime) +
                    selectedEvent.duration
                )}
              </p>
              <p>Thời lượng: {formatDuration(selectedEvent.duration)}</p>
              <p>Thể loại: {selectedEvent.category}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FilmShowChartPage;
