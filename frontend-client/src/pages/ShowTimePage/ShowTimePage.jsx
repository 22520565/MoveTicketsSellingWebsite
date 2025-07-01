import React, { use, useEffect, useState } from "react";
import {
  getAllFilms,
  getCinemas,
  getAvailableFilmByDate,
  getAvailableShowDate,
  getFilmShowByFilmId,
  getTheaterByFilmId,
  getAllRooms,
} from "../../config/api";
import { getDateStringFromISOSring } from "../../utils/utils";
import { useNavigate } from "react-router-dom";

const ShowTimePage = () => {
  const [availableShowDates, setAvailableShowDates] = useState([]);
  const [selectedDate, setSelectedDate] = useState("");

  const [availableFilm, setAvailableFilm] = useState([]);
  const [selectedFilm, setSelectedFilm] = useState(null);

  const [selectedCinema, setSelectedCinema] = useState("");
  const [cinemaOptions, setCinemaOptions] = useState([]);

  const [selectedTime, setSelectedTime] = useState("");

  const [optionFilms, setOptionFilms] = useState([]);
  const [loading, setLoading] = useState(true);

  const [pagination, setPagination] = useState({
    total: 0,
    currentPage: 1,
    totalPages: 0,
    limit: 100,
  });

  const formatVNDate = (dateStr, index) => {
    const date = new Date(dateStr);
    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const weekday = [
      "Chủ Nhật",
      "Thứ Hai",
      "Thứ Ba",
      "Thứ Tư",
      "Thứ Năm",
      "Thứ Sáu",
      "Thứ Bảy",
    ];

    return index === 0
      ? `Hôm nay ${day}/${month}`
      : `${weekday[date.getDay()]} ${day}/${month}`;
  };

  const getAllFilm = async () => {
    const response = await getAllFilms();
    if (response) {
      setOptionFilms(response._embedded.filmResponseDtoList);
    }
  };

  const getAllCinema = async () => {
    const response = await getCinemas();
    if (response && response._embedded.theaterResponseDtoList) {
      const cinemaMap = response._embedded.theaterResponseDtoList.map(
        (cinema) => ({
          id: cinema.id,
          name: cinema.name,
        })
      );
      setCinemaOptions(cinemaMap);
    }
  };

  const navigate = useNavigate();
  const handleGetAvailableShowDate = async () => {
    const today = new Date();
    const dates = [];

    for (let i = 0; i < 4; i++) {
      const date = new Date(today);
      date.setDate(today.getDate() + i);
      dates.push(date.toISOString().split("T")[0]); // YYYY-MM-DD
    }

    setAvailableShowDates(dates);
    setSelectedDate(dates[0]);
  };

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

  useEffect(() => {
    console.log("film nè: ", availableFilm);
  }, [availableFilm]);

  useEffect(() => {
    document.title = "Lịch chiếu";
    getAllFilm();
    getAllCinema();
    handleGetAvailableShowDate();
  }, []);

  useEffect(() => {
    const getAllFilmByDate = async () => {
      setLoading(true);
      try {
        const filmRes = await getAvailableFilmByDate(selectedDate);
        const films = filmRes?._embedded?.filmResponseDtoList || [];

        const filmWithTypesPromises = films.map(async (film) => {
          const filmShowRes = await getFilmShowByFilmId(film.id);
          const filmShows =
            filmShowRes?._embedded?.filmShowResponseDtoList || [];
          console.log("Film Shows: ", filmShows);

          const theatersRes = await getTheaterByFilmId(film.id);
          const theaters = theatersRes?._embedded?.theaterResponseDtoList || [];
          console.log("Theaters: ", theaters);

          const roomsRes = await getAllRooms();
          const rooms = roomsRes?._embedded?.roomResponseDtoList || [];
          console.log("Rooms: ", rooms);

          const grouped = {};

          filmShows.forEach((show) => {
            const room = rooms.find((r) => r.id === show.roomId);
            if (!room || !room.theaterId) return;

            const theaterId = room.theaterId;
            const theater = theaters.find((t) => t.id === theaterId);
            if (!theater) return;

            if (!grouped[theaterId]) {
              grouped[theaterId] = {
                theater,
                filmTypes: {},
              };
            }

            if (!grouped[theaterId].filmTypes[show.type]) {
              grouped[theaterId].filmTypes[show.type] = [];
            }

            grouped[theaterId].filmTypes[show.type].push(show);
          });

          const groupedArray = Object.values(grouped).map((group) => ({
            theater: group.theater,
            filmTypes: Object.entries(group.filmTypes).map(
              ([type, showTimes]) => ({
                type,
                showTimes,
              })
            ),
          }));

          return {
            film,
            filmTypes: groupedArray,
          };
        });

        const results = await Promise.all(filmWithTypesPromises);
        setAvailableFilm(results);

        const page = filmRes?.page;
        setPagination({
          total: page?.totalElements,
          currentPage: page?.number + 1,
          totalPages: page?.totalPages,
          limit: page?.size,
        });
      } catch (err) {
        console.error("Lỗi lấy lịch chiếu:", err);
      }
      setLoading(false);
    };

    getAllFilmByDate();
  }, [selectedDate, selectedFilm]);

  const handleNavigate = (time, currentFilmId) => {
    navigate(`/movie/detail/${currentFilmId}`, {
      state: {
        initShowDate: selectedDate,
        initShowTime: time,
      },
    });
  };

  return (
    <div className=" text-white px-[160px] gap-20 py-10 min-h-[800px]">
      <h1 className="col-span-2 text-center font-bold">Lịch chiếu</h1>

      <div className="w-full flex justify-center mb-8">
        <div className="flex gap-6  max-w-[1500px] w-full mx-auto">
          {/* 1. Ngày */}
          <div className="basis-1/4 border border-white-400 rounded-lg p-2">
            <label className="block mb-2 text-2xl font-bold text-yellow-300">
              1. Ngày
            </label>
            <select
              className="w-full p-2 bg-white text-black rounded-lg text-xl font-bold"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
            >
              {availableShowDates.map((dateStr, index) => (
                <option key={dateStr} value={dateStr}>
                  {formatVNDate(dateStr, index)}
                </option>
              ))}
            </select>
          </div>

          {/* 2. Phim (Chiếm nhiều nhất) */}
          <div className="basis-2/4 border border-white-400 rounded-lg p-2">
            <label className="block mb-2 text-2xl font-bold text-yellow-300">
              2. Phim
            </label>
            <select
              className="w-full p-2 bg-white text-black rounded-lg text-xl font-bold"
              value={JSON.stringify(selectedFilm)}
              onChange={(e) => {
                const value = e.target.value;
                setSelectedFilm(value ? JSON.parse(value) : null);
              }}
            >
              <option value="" hidden>
                Chọn phim
              </option>
              {optionFilms?.map((film) => (
                <option key={film.id} value={JSON.stringify(film)}>
                  {film.name}
                </option>
              ))}
            </select>
          </div>

          {/* 3. Rạp */}
          <div className="basis-1/4 border border-white-400 rounded-lg p-2">
            <label className="block mb-2 text-2xl font-bold text-yellow-300">
              3. Rạp
            </label>
            <select
              className="w-full p-2 bg-white text-black rounded-lg text-xl font-bold"
              value={selectedCinema}
              onChange={(e) => setSelectedCinema(e.target.value)}
            >
              <option value="" disabled hidden>
                Chọn rạp
              </option>
              {cinemaOptions?.map((cinema) => (
                <option key={cinema.id} value={cinema.id}>
                  {cinema.name}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>
      <hr className="pb-8 border-white  w-full mx-auto" />

      {loading ? (
        <div className="text-white text-center text-xl py-8">
          Đang tải dữ liệu...
        </div>
      ) : availableFilm && availableFilm.length > 0 ? (
        availableFilm.map((value) => {
          const filmDetail = value.film;
          const theaters = value.filmTypes;

          return (
            <>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
                <div className="md:col-span-1">
                  <img
                    src={filmDetail?.thumbnailUrl}
                    alt="Movie Poster"
                    className="w-full rounded-lg shadow-lg"
                  />

                  <div className="mt-4">
                    <h3 className="text-2xl font-bold">{filmDetail?.name}</h3>
                    <div className="mt-2 space-y-1">
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">⌚</span>
                        <span className="text-xl">{filmDetail?.duration}p</span>
                      </div>
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">🌏</span>
                        <span className="text-xl">
                          {filmDetail?.originatedCountry}
                        </span>
                      </div>
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">🎬</span>
                        <span className="text-xl">{filmDetail?.voice}</span>
                      </div>
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">👥</span>
                        <span className="text-xl">
                          {filmDetail?.ageRestriction +
                            " : " +
                            getAgeDescription(filmDetail?.ageRestriction)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="md:col-span-3 space-y-6">
                  <div className="rounded-lg p-4 border border-white-400">
                    {theaters && theaters.length > 0 ? (
                      theaters.map((theater, idx) => (
                        <div
                          key={idx}
                          className="flex flex-col md:flex-row gap-6 mb-6 border-b border-gray-700 pb-4"
                        >
                          {/* Thông tin rạp bên trái */}
                          <div className="min-w-[250px]">
                            <div className="text-3xl font-semibold text-white">
                              {theater.theater.name}
                            </div>
                            <div className="text-white text-xl">
                              {theater.theater.address}
                            </div>
                          </div>

                          {/* Các loại suất chiếu bên phải, mỗi loại một hàng */}
                          <div className="flex flex-col gap-3">
                            {theater.filmTypes &&
                            theater.filmTypes.length > 0 ? (
                              theater.filmTypes.map((ft, ftIdx) => (
                                <div key={ftIdx} className="space-y-2">
                                  <div className="text-2xl text-white font-medium min-w-[50px]">
                                    {ft.type?.toUpperCase()}:
                                  </div>
                                  {ft.showTimes && ft.showTimes.length > 0 ? (
                                    <div className="flex flex-wrap gap-2">
                                      {ft.showTimes.map((time, tIdx) => (
                                        <button
                                          key={tIdx}
                                          className={`px-4 py-2 rounded border text-sm font-semibold transition-colors
              ${
                selectedTime === time
                  ? "bg-yellow-400 text-black border-yellow-400"
                  : "border-white text-white hover:border-yellow-400 hover:text-yellow-400"
              }`}
                                          onClick={() => {
                                            setSelectedTime(time.showTime);
                                            handleNavigate(time, filmDetail.id);
                                          }}
                                        >
                                          {time.showTime.slice(0, 5)}
                                        </button>
                                      ))}
                                    </div>
                                  ) : (
                                    <div className="text-yellow-400 text-sm">
                                      Không có suất
                                    </div>
                                  )}
                                </div>
                              ))
                            ) : (
                              <div className="text-yellow-400 text-sm">
                                🚫 Chưa có suất chiếu
                              </div>
                            )}
                          </div>
                        </div>
                      ))
                    ) : (
                      <div className="text-yellow-400">
                        🚫 Chưa có rạp chiếu phim này
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </>
          );
        })
      ) : (
        <div className="text-center text-yellow-400 text-3xl font-extrabold py-8">
          🚫 HIỆN CHƯA CÓ LỊCH CHIẾU
        </div>
      )}

      {/* {availableFilm?.length === 0 && selectedFilm !== null && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <div className="md:col-span-1">
              <img
                src={selectedFilm?.thumbnailURL}
                alt="Movie Poster"
                className="w-full rounded-lg shadow-lg"
              />

              <div className="mt-4">
                <h3 className="text-2xl font-bold">{selectedFilm.name}</h3>
                <div className="mt-2 space-y-1">
                  <div className="flex items-center">
                    <span className="text-yellow-400 mr-2 text-xl">⌚</span>
                    <span className="text-xl">
                      {selectedFilm.filmDuration}p
                    </span>
                  </div>
                  <div className="flex items-center">
                    <span className="text-yellow-400 mr-2 text-xl">🌏</span>
                    <span className="text-xl">
                      {" "}
                      {selectedFilm.originatedCountry}
                    </span>
                  </div>
                  <div className="flex items-center">
                    <span className="text-yellow-400 mr-2 text-xl">🎬</span>
                    <span className="text-xl"> {selectedFilm.voice}</span>
                  </div>
                  <div className="flex items-center">
                    <span className="text-yellow-400 mr-2 text-xl">👥</span>
                    <span className="text-xl">
                      {selectedFilm.ageRestriction +
                        " : " +
                        getAgeDescription(selectedFilm.ageRestriction)}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div className="md:col-span-3 space-y-6">
              <div className=" rounded-lg p-4 border border-white-400 ">
                <div className="flex items-center mb-4"></div>
                <div className="space-y-4 text-xl">Chưa có suất chiếu</div>
              </div>
            </div>
          </div>
          <hr className="pb-8" />
        </>
      )} */}
      {/* Movie Info Section */}
    </div>
  );
};

export default ShowTimePage;
