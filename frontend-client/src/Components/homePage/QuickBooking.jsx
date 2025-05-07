import React, { useEffect, useState, useRef } from "react";
import { ChevronDown } from "lucide-react";
import {
  getShowingFilms,
  getShowTimeOfDateByFilmId,
  getCinemas,
} from "../../config/api";
import { useNavigate } from "react-router-dom";
import {
  getDayAndMonthFromISOString,
  getDayOfWeekFromISOString,
} from "../../utils/utils";

const QuickBooking = () => {
  const navigate = useNavigate();
  const [cinemas, setCinemas] = useState([]);

  const [selectedCinema, setSelectedCinema] = useState(null);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedShowtime, setSelectedShowtime] = useState("");

  const [isCinemaDropdownOpen, setIsCinemaDropdownOpen] = useState(false);
  const [isMovieDropdownOpen, setIsMovieDropdownOpen] = useState(false);
  const [isDateDropdownOpen, setIsDateDropdownOpen] = useState(false);
  const [isShowtimeDropdownOpen, setIsShowtimeDropdownOpen] = useState(false);

  const [availableMovies, setAvailableMovies] = useState([]);
  const [availableDates, setAvailableDates] = useState([]);
  const [availableShowtimes, setAvailableShowtimes] = useState([]);

  const dropdownRefs = {
    cinema: useRef(null),
    movie: useRef(null),
    date: useRef(null),
    showtime: useRef(null),
  };

  const buttonRefs = {
    cinema: useRef(null),
    movie: useRef(null),
    date: useRef(null),
    showtime: useRef(null),
  };
  const handleGetDateAndShowTime = async (filmId) => {
    try {
      const response = await getShowTimeOfDateByFilmId(filmId);
      if (response?.success && response.data) {
        const formattedDates = response.data.map((item) => {
          return {
            date: item.date,
            showtimes: item.show.flatMap((s) =>
              s.showTimes.map((st) => ({
                id: st._id,
                time: st.showTime,
              }))
            ),
          };
        });
        setAvailableDates(formattedDates);
        setAvailableShowtimes([]);
      }
    } catch (error) {
      console.error("Error fetching dates and showtimes:", error);
    }
  };

  useEffect(() => {
    const fetchCinemas = async () => {
      try {
        const response = await getCinemas();
        console.log(response._embedded.theaterResponseDtoList);

        if (response && response._embedded.theaterResponseDtoList) {
          console.log(1);

          const cinemaMap = response._embedded.theaterResponseDtoList.map(
            (cinema) => ({
              id: cinema.id,
              name: cinema.name,
            })
          );
          setCinemas(cinemaMap);
        }
      } catch {
        throw new Error("There is an error while getting film detail");
      }
    };
    fetchCinemas();
  }, []);

  console.log(cinemas);

  // useEffect(() => {
  //   const fetchFilmShowing = async () => {
  //     try {
  //       const response = await getShowingFilms();
  //       if (response && response.data) {
  //         const filmShowingMap = response.data.map((film) => ({
  //           id: film._id,
  //           name: film.name,
  //         }));
  //         setAvailableMovies(filmShowingMap);
  //       }
  //     } catch {
  //       throw new Error("There is an error while getting film detail");
  //     }
  //   };
  //   fetchFilmShowing();
  // }, []);

  // useEffect(() => {
  //   setSelectedDate("");
  //   setSelectedShowtime("");
  //   if (selectedMovie?.id) {
  //     handleGetDateAndShowTime(selectedMovie.id);
  //   }
  // }, [selectedMovie]);

  // useEffect(() => {
  //   setSelectedShowtime("");
  //   if (selectedDate) {
  //     // Find showtimes for selected date
  //     const dateData = availableDates.find((d) => d.date === selectedDate);
  //     setAvailableShowtimes(dateData?.showtimes || []);
  //   }
  // }, [selectedDate]);

  const handleScroll = (dropdownName) => {
    if (
      dropdownRefs[dropdownName].current &&
      buttonRefs[dropdownName].current
    ) {
      const buttonRect =
        buttonRefs[dropdownName].current.getBoundingClientRect();
      const dropdownRect =
        dropdownRefs[dropdownName].current.getBoundingClientRect();

      // Kiểm tra nếu không còn đủ không gian dưới và tự động chuyển dropdown lên trên
      if (window.innerHeight - buttonRect.bottom < dropdownRect.height) {
        dropdownRefs[dropdownName].current.style.top = "auto";
        dropdownRefs[dropdownName].current.style.bottom = "100%"; // Đưa lên trên khi không đủ không gian dưới
      } else {
        dropdownRefs[dropdownName].current.style.top = "100%"; // Đưa xuống dưới nếu đủ không gian
        dropdownRefs[dropdownName].current.style.bottom = "auto";
      }
    }
  };

  useEffect(() => {
    if (isCinemaDropdownOpen) {
      handleScroll("cinema"); // Kiểm tra vị trí dropdown khi nó mở
    }
    if (isMovieDropdownOpen) {
      handleScroll("movie");
    }
    if (isDateDropdownOpen) {
      handleScroll("date");
    }
    if (isShowtimeDropdownOpen) {
      handleScroll("showtime");
    }
  }, [
    isCinemaDropdownOpen,
    isMovieDropdownOpen,
    isDateDropdownOpen,
    isShowtimeDropdownOpen,
  ]);

  const handleMovieClick = () => {
    if (!selectedCinema) return;
    setIsMovieDropdownOpen(!isMovieDropdownOpen);
  };

  const handleDateClick = () => {
    if (!selectedMovie) return;
    setIsDateDropdownOpen(!isDateDropdownOpen);
  };

  const handleShowtimeClick = () => {
    if (!selectedDate) return;
    setIsShowtimeDropdownOpen(!isShowtimeDropdownOpen);
  };

  const handleNavigate = () => {
    navigate(`/movie/detail/${selectedMovie.id}`, {
      state: {
        initShowDate: selectedDate,
        initShowTime: selectedShowtime,
      },
    });
  };

  return (
    <div className="bg-white rounded-lg shadow-lg p-4 flex items-center gap-3">
      <div className="font-bold text-2xl text-gray-800 whitespace-nowrap">
        ĐẶT VÉ NHANH
      </div>

      <div className="relative flex-1">
        <button
          ref={buttonRefs.cinema}
          className={`w-full px-4 py-3 rounded-lg text-left flex items-center justify-between bg-white border-2 border-purple-600 hover:bg-gray-50`}
          onClick={() => setIsCinemaDropdownOpen(!isCinemaDropdownOpen)}
        >
          <span className={"text-purple-600 text-xl"}>
            {selectedCinema?.name || "1. Chọn Rạp"}
          </span>
          <ChevronDown className={`w-5 h-5 text-purple-600`} />
        </button>
        {isCinemaDropdownOpen && (
          <div
            ref={dropdownRefs.cinema}
            className="absolute w-full max-h-[320px] mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10 text-xl overflow-y-auto"
          >
            {cinemas.map((cinema) => (
              <div
                key={cinema.id}
                className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black"
                onClick={() => {
                  setSelectedCinema(cinema);
                  setIsCinemaDropdownOpen(false);
                }}
              >
                {cinema.name}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="relative flex-1">
        <button
          ref={buttonRefs.movie}
          className={`w-full px-4 py-3 rounded-lg text-left flex items-center justify-between ${
            selectedMovie
              ? "bg-white border-2 border-purple-600 hover:bg-gray-50"
              : "bg-gray-100 border-2 border-gray-300 cursor-not-allowed"
          }`}
          disabled={!selectedCinema}
          onClick={handleMovieClick}
        >
          <span
            className={
              selectedCinema
                ? "text-purple-600 text-xl"
                : "text-gray-500 text-xl"
            }
          >
            {selectedMovie?.name || "2. Chọn Phim"}
          </span>
          <ChevronDown
            className={`w-5 h-5 ${
              selectedCinema ? "text-purple-600" : "text-black-400"
            }`}
          />
        </button>
        {isMovieDropdownOpen && (
          <div
            ref={dropdownRefs.movie}
            className="absolute w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10 text-xl"
          >
            {availableMovies.map((movie) => (
              <div
                key={movie.id}
                className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black"
                onClick={() => {
                  setSelectedMovie(movie);
                  setIsMovieDropdownOpen(false);
                }}
              >
                {movie.name}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="relative flex-1">
        <button
          ref={buttonRefs.date}
          className={`w-full px-4 py-3 rounded-lg text-left flex items-center justify-between ${
            selectedMovie
              ? "bg-white border-2 border-purple-600 hover:bg-gray-50"
              : "bg-gray-100 border-2 border-gray-300 cursor-not-allowed"
          }`}
          onClick={handleDateClick}
          disabled={!selectedMovie}
        >
          <span
            className={
              selectedMovie
                ? "text-purple-600 text-xl"
                : "text-gray-500 text-xl"
            }
          >
            {(selectedDate &&
              getDayOfWeekFromISOString(selectedDate) +
                " , " +
                getDayAndMonthFromISOString(selectedDate)) ||
              "3. Chọn Ngày"}
          </span>
          <ChevronDown
            className={`w-5 h-5 ${
              selectedMovie ? "text-purple-600" : "text-black-400"
            }`}
          />
        </button>
        {isDateDropdownOpen && (
          <div
            ref={dropdownRefs.date}
            className="absolute w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10"
          >
            {availableDates.map((dateItem, index) => (
              <div
                key={index}
                className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black text-xl"
                onClick={() => {
                  setSelectedDate(dateItem.date);
                  setIsDateDropdownOpen(false);
                }}
              >
                {getDayOfWeekFromISOString(dateItem.date) +
                  "," +
                  getDayAndMonthFromISOString(dateItem.date)}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="relative flex-1">
        <button
          ref={buttonRefs.showtime}
          className={`w-full px-4 py-3 rounded-lg text-left flex items-center justify-between ${
            selectedDate
              ? "bg-white border-2 border-purple-600 hover:bg-gray-50"
              : "bg-gray-100 border-2 border-gray-300 cursor-not-allowed"
          }`}
          onClick={handleShowtimeClick}
          disabled={!selectedDate}
        >
          <span
            className={
              selectedDate ? "text-purple-600 text-xl" : "text-gray-500 text-xl"
            }
          >
            {selectedShowtime || "4. Chọn Suất"}
          </span>
          <ChevronDown
            className={`w-5 h-5 ${
              selectedDate ? "text-purple-600" : "text-black-400"
            }`}
          />
        </button>
        {isShowtimeDropdownOpen && (
          <div
            ref={dropdownRefs.showtime}
            className="absolute w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10"
          >
            {availableShowtimes.map((timeItem) => (
              <div
                key={timeItem.id}
                className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black text-xl"
                onClick={() => {
                  setSelectedShowtime(timeItem.time);
                  setIsShowtimeDropdownOpen(false);
                }}
              >
                {timeItem.time}
              </div>
            ))}
          </div>
        )}
      </div>

      <button
        className={`px-6 py-3 rounded-lg font-medium whitespace-nowrap transition-colors text-xl ${
          selectedShowtime
            ? "bg-purple-700 hover:bg-purple-800 text-white"
            : "bg-gray-300 text-gray-500 cursor-not-allowed"
        }`}
        onClick={() => {
          handleNavigate();
        }}
        disabled={!selectedShowtime}
      >
        ĐẶT NGAY
      </button>
    </div>
  );
};

export default QuickBooking;
