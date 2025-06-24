import React, { useEffect, useState } from "react";
import {
  getAllFilms,
  getCinemas,
  getAvailableFilmByDate,
  getAvailableShowDate,
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

  const [optionFilms, setOptionFilms] = useState([]);

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

  const getAllFilmByDate = async () => {
    const response = await getAvailableFilmByDate(selectedDate);
    console.log("🚀 ~ getAllFilmByDate ~ response:", response);

    if (response) {
      setAvailableFilm(response?._embedded?.filmResponseDtoList);

      const page = response?.page;
      setPagination({
        total: page?.totalElements,
        currentPage: page?.number + 1, // vì API đếm từ 0, client từ 1
        totalPages: page?.totalPages,
        limit: page?.size,
      });
    }
  };

  useEffect(() => {
    document.title = "Lịch chiếu";
    getAllFilm();
    getAllCinema();
    handleGetAvailableShowDate();
  }, []);

  console.log(selectedFilm);

  useEffect(() => {
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

      {availableFilm && availableFilm.length > 0 ? (
        availableFilm?.map((value) => {
          const filmDetail = value.film;
          const filmTypes = value.filmTypes;
          return (
            <>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
                <div className="md:col-span-1">
                  <img
                    src={filmDetail?.thumbnailURL}
                    alt="Movie Poster"
                    className="w-full rounded-lg shadow-lg"
                  />

                  <div className="mt-4">
                    <h3 className="text-2xl font-bold">{filmDetail.name}</h3>
                    <div className="mt-2 space-y-1">
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">⌚</span>
                        <span className="text-xl">
                          {filmDetail.filmDuration}p
                        </span>
                      </div>
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">🌏</span>
                        <span className="text-xl">
                          {" "}
                          {filmDetail.originatedCountry}
                        </span>
                      </div>
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">🎬</span>
                        <span className="text-xl"> {filmDetail.voice}</span>
                      </div>
                      <div className="flex items-center">
                        <span className="text-yellow-400 mr-2 text-xl">👥</span>
                        <span className="text-xl">
                          {filmDetail.ageRestriction +
                            " : " +
                            getAgeDescription(filmDetail.ageRestriction)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="md:col-span-3 space-y-6">
                  <div className=" rounded-lg p-4 border border-white-400 ">
                    <div className="flex items-center mb-4"></div>
                    <div className="space-y-4 text-xl">
                      <div>
                        {filmTypes.map((value) => {
                          return (
                            <>
                              <div className="space-y-4 text-xl">
                                <div className="text-xl text-gray-400 mb-2 ">
                                  Suất chiếu: {value.filmType}
                                </div>
                                <div className="flex flex-wrap gap-4 pb-4">
                                  {value.showTimes.map((time) => (
                                    <button
                                      key={time}
                                      className="px-4 py-2 bg-gray-700 hover:bg-yellow-500 rounded transition-colors"
                                      onClick={() =>
                                        handleNavigate(time, filmDetail._id)
                                      }
                                    >
                                      {time}
                                    </button>
                                  ))}
                                </div>
                              </div>
                            </>
                          );
                        })}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <hr className="pb-8" />
            </>
          );
        })
      ) : (
        <div className="text-center text-yellow-400 text-3xl font-extrabold py-8">
          🚫 HIỆN CHƯA CÓ LỊCH CHIẾU
        </div>
      )}

      {availableFilm?.length === 0 && selectedFilm !== null && (
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
      )}
      {/* Movie Info Section */}
    </div>
  );
};

export default ShowTimePage;
