import React, { useEffect, useState, useRef } from "react";
import { ChevronDown } from "lucide-react";
import {
  getShowingFilms,
  getShowTimeOfDateByFilmId,
  getCinemas,
  getFilmByTheaterId,
  getFilmShowByFilmId,
  getRoomById,
  getFilmShowByFilmIdAndDate,
} from "../../config/api";
import { useNavigate } from "react-router-dom";
import {
  getDayAndMonthFromISOString,
  getDayOfWeekFromISOString,
} from "../../utils/utils";
import Spinner from "./Spinner";

const QuickBooking = () => {
  const navigate = useNavigate();
  const [cinemas, setCinemas] = useState([]);

  const [selectedCinema, setSelectedCinema] = useState(null);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedFilmShow, setSelectedFilmShow] = useState(null);

  const [isCinemaDropdownOpen, setIsCinemaDropdownOpen] = useState(false);
  const [isMovieDropdownOpen, setIsMovieDropdownOpen] = useState(false);
  const [isDateDropdownOpen, setIsDateDropdownOpen] = useState(false);
  const [isShowtimeDropdownOpen, setIsShowtimeDropdownOpen] = useState(false);

  const [availableMovies, setAvailableMovies] = useState([]);
  const [availableDates, setAvailableDates] = useState([]);
  const [allShowtimes, setAllShowtimes] = useState([]);
  const [availableShowtimes, setAvailableShowtimes] = useState([]);

  const [isLoadingMovies, setIsLoadingMovies] = useState(false);
  const [isLoadingDates, setIsLoadingDates] = useState(false);
  const [isLoadingShowtimes, setIsLoadingShowtimes] = useState(false);

  const closeAllDropdowns = () => {
    setIsCinemaDropdownOpen(false);
    setIsMovieDropdownOpen(false);
    setIsDateDropdownOpen(false);
    setIsShowtimeDropdownOpen(false);
  };

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

        if (response && response._embedded.theaterResponseDtoList) {
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

  useEffect(() => {
    const fetchMovie = async () => {
      try {
        setIsLoadingMovies(true);
        const response = await getFilmByTheaterId(selectedCinema.id);
        console.log(response);

        const films = response?._embedded?.filmResponseDtoList;

        if (Array.isArray(films) && films.length > 0) {
          const filmMap = films.map((film) => ({
            id: film.id,
            name: film.name,
          }));
          setAvailableMovies(filmMap);
        } else {
          setAvailableMovies([]);
        }
      } catch {
        throw new Error("There is an error while getting film detail");
      } finally {
        setIsLoadingMovies(false);
      }
    };
    if (selectedCinema?.id) {
      fetchMovie();
    }
  }, [selectedCinema]);

  useEffect(() => {
    const fetchDate = async () => {
      try {
        setIsLoadingDates(true);
        const response = await getFilmShowByFilmId(selectedMovie.id);
        const filmShows = response?._embedded?.filmShowResponseDtoList;
        console.log(filmShows);

        const roomIdsInSelectedTheater = [];

        for (const show of filmShows) {
          const roomRes = await getRoomById(show.roomId);
          console.log(roomRes);

          const roomData = roomRes;
          if (roomData.theaterId === selectedCinema.id) {
            roomIdsInSelectedTheater.push(roomData.id);
          }
        }

        const filteredShows = filmShows.filter((show) =>
          roomIdsInSelectedTheater.includes(show.roomId)
        );

        const now = new Date();

        const upcomingShows = filteredShows.filter((show) => {
          const showDateTime = new Date(`${show.showDate}T${show.showTime}`);
          return showDateTime > now;
        });

        setAllShowtimes(upcomingShows);
        const uniqueValidDates = [
          ...new Set(upcomingShows.map((show) => show.showDate)),
        ];

        setAvailableDates(uniqueValidDates);
      } catch {
        throw new Error("There is an error while getting date");
      } finally {
        setIsLoadingDates(false);
      }
    };
    if (selectedMovie?.id) {
      fetchDate();
    }
  }, [selectedMovie]);

  console.log(allShowtimes);

  useEffect(() => {
    const showtimes = allShowtimes
      .filter((show) => show.showDate === selectedDate)
      .map((show) => show.showTime.slice(0, 5));

    showtimes.sort();

    setAvailableShowtimes(showtimes);
  }, [selectedDate, allShowtimes]);

  const handleScroll = (dropdownName) => {
    const dropdown = dropdownRefs[dropdownName].current;
    const button = buttonRefs[dropdownName].current;

    if (dropdown && button) {
      const buttonRect = button.getBoundingClientRect();
      const dropdownHeight = dropdown.offsetHeight;

      dropdown.style.position = "absolute"; // đảm bảo position tồn tại

      if (window.innerHeight - buttonRect.bottom < dropdownHeight + 20) {
        dropdown.style.top = "auto";
        dropdown.style.bottom = "100%";
      } else {
        dropdown.style.top = "100%";
        dropdown.style.bottom = "auto";
      }
    }
  };

  useEffect(() => {
    const handleWindowScroll = () => {
      if (isCinemaDropdownOpen) handleScroll("cinema");
      if (isMovieDropdownOpen) handleScroll("movie");
      if (isDateDropdownOpen) handleScroll("date");
      if (isShowtimeDropdownOpen) handleScroll("showtime");
    };

    // ✅ Gọi lại vị trí dropdown ngay khi mở
    requestAnimationFrame(handleWindowScroll);

    // ✅ Lắng nghe scroll/resize để reposition
    window.addEventListener("scroll", handleWindowScroll);
    window.addEventListener("resize", handleWindowScroll);

    return () => {
      window.removeEventListener("scroll", handleWindowScroll);
      window.removeEventListener("resize", handleWindowScroll);
    };
  }, [
    isCinemaDropdownOpen,
    isMovieDropdownOpen,
    isDateDropdownOpen,
    isShowtimeDropdownOpen,
  ]);

  //phim
  const handleMovieClick = () => {
    if (!selectedCinema) return;
    setIsMovieDropdownOpen(!isMovieDropdownOpen);
  };
  useEffect(() => {
    if (selectedCinema) {
      setIsMovieDropdownOpen(!isMovieDropdownOpen);
    }
  }, [selectedCinema]);

  //date
  const handleDateClick = () => {
    if (!selectedMovie) return;
    setIsDateDropdownOpen(!isDateDropdownOpen);
  };

  useEffect(() => {
    if (selectedMovie) {
      setIsDateDropdownOpen(!isDateDropdownOpen);
    }
  }, [selectedMovie]);

  //gio
  const handleShowtimeClick = () => {
    if (!selectedDate) return;
    setIsShowtimeDropdownOpen(!isShowtimeDropdownOpen);
  };

  const handleNavigate = () => {
    console.log("Saving allShows to localStorage:", allShowtimes);
    localStorage.setItem("allShows", JSON.stringify(allShowtimes));
    navigate(`/movie/detail/${selectedMovie.id}`, {
      state: {
        initShowDate: selectedDate,
        initShowTime: selectedFilmShow,
        initCinema: selectedCinema,
      },
    });
  };

  return (
    <div className="bg-white rounded-lg shadow-lg p-4 flex items-center gap-3">
      <div className="font-bold text-2xl text-gray-800 whitespace-nowrap">
        ĐẶT VÉ NHANH
      </div>

      {/* Dropdown Cinema */}
      <div className="relative flex-1">
        <button
          ref={buttonRefs.cinema}
          className="w-full px-4 py-3 rounded-lg text-left flex items-center justify-between bg-white border-2 border-purple-600 hover:bg-gray-50"
          onClick={() => {
            closeAllDropdowns();
            setIsCinemaDropdownOpen(!isCinemaDropdownOpen);
          }}
        >
          <span className="text-purple-600 text-xl">
            {selectedCinema?.name || "1. Chọn Rạp"}
          </span>
          <ChevronDown className="w-5 h-5 text-purple-600" />
        </button>
        <div
          ref={dropdownRefs.cinema}
          style={{ top: "auto", bottom: "auto" }}
          className={`absolute w-full max-h-[320px] overflow-hidden transition-all duration-300 ease-out bg-white border border-gray-200 rounded-lg shadow-lg z-10 text-xl overflow-y-auto
            ${
              isCinemaDropdownOpen
                ? "max-h-[320px] opacity-100 translate-y-0"
                : "max-h-0 opacity-0 -translate-y-2 pointer-events-none"
            }`}
        >
          {cinemas.map((cinema) => (
            <div
              key={cinema.id}
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black"
              onClick={() => {
                const isSame = selectedCinema?.id === cinema.id;
                setIsCinemaDropdownOpen(!isCinemaDropdownOpen);

                if (isSame) return;

                setSelectedCinema(cinema);
                setSelectedMovie(null);
                setSelectedDate("");
                setSelectedFilmShow("");
                setIsCinemaDropdownOpen(!isCinemaDropdownOpen);
              }}
            >
              {cinema.name}
            </div>
          ))}
        </div>
      </div>

      {/* Dropdown Movie */}
      <div className="relative flex-1">
        <button
          ref={buttonRefs.movie}
          className={`w-full px-4 py-3 rounded-lg text-left flex items-center justify-between ${
            selectedCinema
              ? "bg-white border-2 border-purple-600 hover:bg-gray-50"
              : "bg-gray-100 border-2 border-gray-300 cursor-not-allowed"
          }`}
          disabled={!selectedCinema}
          onClick={() => {
            closeAllDropdowns();
            handleMovieClick();
          }}
        >
          <span
            className={
              selectedCinema
                ? "text-purple-600 text-xl"
                : "text-gray-500 text-xl"
            }
          >
            {isLoadingMovies ? (
              <div className="flex items-center gap-2">
                <Spinner size={18} /> Đang tải...
              </div>
            ) : selectedMovie ? (
              selectedMovie.name
            ) : (
              "2. Chọn Phim"
            )}
          </span>
          <ChevronDown
            className={`w-5 h-5 ${
              selectedCinema ? "text-purple-600" : "text-black-400"
            }`}
          />
        </button>
        <div
          ref={dropdownRefs.movie}
          style={{ top: "auto", bottom: "auto" }}
          className={`absolute w-full transition-all duration-300 ease-out bg-white border border-gray-200 rounded-lg shadow-lg z-10 text-xl overflow-hidden max-h-[400px] overflow-y-auto
            ${
              isMovieDropdownOpen
                ? "max-h-[400px] opacity-100 translate-y-0"
                : "max-h-0 opacity-0 -translate-y-2 pointer-events-none"
            }`}
        >
          {availableMovies.length === 0 && (
            <div className="p-4 text-gray-500">Không có phim nào khả dụng</div>
          )}
          {availableMovies.map((movie) => (
            <div
              key={movie.id}
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black"
              onClick={() => {
                const isSame = selectedMovie?.id === movie.id;
                setIsMovieDropdownOpen(!isMovieDropdownOpen);

                if (isSame) return;

                setSelectedMovie(movie);
                setSelectedDate("");
                setSelectedFilmShow("");
                setIsMovieDropdownOpen(!isMovieDropdownOpen);
              }}
            >
              {movie.name}
            </div>
          ))}
        </div>
      </div>

      {/* Dropdown Date */}
      <div className="relative flex-1">
        <button
          ref={buttonRefs.date}
          className={`w-full px-4 py-3 rounded-lg text-left flex items-center justify-between ${
            selectedMovie
              ? "bg-white border-2 border-purple-600 hover:bg-gray-50"
              : "bg-gray-100 border-2 border-gray-300 cursor-not-allowed"
          }`}
          onClick={() => {
            closeAllDropdowns();
            handleDateClick();
          }}
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
                ", " +
                getDayAndMonthFromISOString(selectedDate)) ||
              "3. Chọn Ngày"}
          </span>
          <ChevronDown
            className={`w-5 h-5 ${
              selectedMovie ? "text-purple-600" : "text-black-400"
            }`}
          />
        </button>
        <div
          ref={dropdownRefs.date}
          style={{ top: "auto", bottom: "auto" }}
          className={`absolute w-full transition-all duration-300 ease-out bg-white border border-gray-200 rounded-lg shadow-lg z-10 overflow-hidden max-h-[400px] overflow-y-auto
            ${
              isDateDropdownOpen
                ? "max-h-[400px] opacity-100 translate-y-0"
                : "max-h-0 opacity-0 -translate-y-2 pointer-events-none"
            }`}
        >
          {availableDates.length === 0 && (
            <div className="p-4 text-gray-500 text-xl">
              Không có ngày chiếu khả dụng
            </div>
          )}
          {availableDates.map((dateItem, index) => (
            <div
              key={index}
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black text-xl"
              onClick={() => {
                const isSame = selectedDate === dateItem;
                setIsDateDropdownOpen(!isDateDropdownOpen);

                if (isSame) return;

                setSelectedDate(dateItem);
                setSelectedFilmShow("");
                setIsDateDropdownOpen(!isDateDropdownOpen);
                setIsShowtimeDropdownOpen(!isShowtimeDropdownOpen);
              }}
            >
              {getDayOfWeekFromISOString(dateItem) +
                ", " +
                getDayAndMonthFromISOString(dateItem)}
            </div>
          ))}
        </div>
      </div>

      {/* Dropdown Showtime */}
      <div className="relative flex-1">
        <button
          ref={buttonRefs.showtime}
          className={`w-full px-4 py-3 rounded-lg text-left flex items-center justify-between ${
            selectedDate
              ? "bg-white border-2 border-purple-600 hover:bg-gray-50"
              : "bg-gray-100 border-2 border-gray-300 cursor-not-allowed"
          }`}
          onClick={() => {
            closeAllDropdowns();
            handleShowtimeClick();
          }}
          disabled={!selectedDate}
        >
          <span
            className={
              selectedDate ? "text-purple-600 text-xl" : "text-gray-500 text-xl"
            }
          >
            {selectedFilmShow?.showTime?.slice(0, 5) || "4. Chọn Suất"}
          </span>
          <ChevronDown
            className={`w-5 h-5 ${
              selectedDate ? "text-purple-600" : "text-black-400"
            }`}
          />
        </button>

        <div
          ref={dropdownRefs.showtime}
          style={{ top: "auto", bottom: "auto" }}
          className={`absolute w-full transition-all duration-300 ease-out bg-white border border-gray-200 rounded-lg shadow-lg z-10 overflow-hidden max-h-[400px] overflow-y-auto
      ${
        isShowtimeDropdownOpen
          ? "max-h-[400px] opacity-100 translate-y-0"
          : "max-h-0 opacity-0 -translate-y-2 pointer-events-none"
      }`}
        >
          {availableShowtimes.length === 0 && (
            <div className="p-4 text-gray-500 text-xl">
              Không có suất chiếu khả dụng
            </div>
          )}

          {availableShowtimes.map((timeItem, index) => (
            <div
              key={index}
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-black text-xl"
              onClick={() => {
                const fullShow = allShowtimes.find(
                  (show) =>
                    show.showDate === selectedDate &&
                    show.showTime.slice(0, 5) === timeItem
                );
                setSelectedFilmShow(fullShow);
                setIsShowtimeDropdownOpen(false);
              }}
            >
              {timeItem}
            </div>
          ))}
        </div>
      </div>

      {/* Book Now Button */}
      <button
        className={`px-6 py-3 rounded-lg font-medium whitespace-nowrap transition-colors text-xl ${
          selectedFilmShow
            ? "bg-purple-700 hover:bg-purple-800 text-white"
            : "bg-gray-300 text-gray-500 cursor-not-allowed"
        }`}
        onClick={handleNavigate}
        disabled={!selectedFilmShow}
      >
        ĐẶT NGAY
      </button>
    </div>
  );
};

export default QuickBooking;
