import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import QuickBooking from "../../Components/homePage/QuickBooking";
import FilmListSection from "../../Components/homePage/FilmListSection";
import {
  getShowingFilms,
  getUpcommingFilms,
  getAllFilms,
} from "../../config/api";
import eventBus from "../../utils/eventBus"; // Import event bus
import { LogIn } from "lucide-react";
import CinemaScheduleList from "../../Components/CinemaList";
const HomePage = () => {
  const [filmShowing, setFilmShowing] = useState([]);
  const [upcomingFilm, setUpcomingFilm] = useState([]);
  const navigate = useNavigate();

  const fetchFilmData = async () => {
    try {
      const [allRes, showingRes] = await Promise.all([
        getAllFilms(),
        getShowingFilms(),
      ]);

      const allFilms = allRes._embedded.filmResponseDtoList || [];
      const showingFilms = showingRes?._embedded?.filmResponseDtoList || [];

      setFilmShowing(showingFilms);

      let upcoming = [];

      if (showingFilms.length === 0) {
        // Nếu không có phim đang chiếu → upcoming = tất cả
        upcoming = allFilms;
      } else {
        // Ngược lại → lọc ra những phim chưa chiếu
        const showingIds = new Set(showingFilms.map((film) => film.id));
        upcoming = allFilms.filter((film) => !showingIds.has(film.id));
      }

      setUpcomingFilm(upcoming);
    } catch (error) {
      console.error("Lỗi khi lấy dữ liệu phim:", error);
      throw new Error("Có lỗi xảy ra khi lấy dữ liệu phim.");
    }
  };

  useEffect(() => {
    document.title = "Trang chủ";

    // const fetchFilmShowing = async () => {
    //   try {
    //     const response = await getShowingFilms();

    //     if (response) {
    //       setFilmShowing(response._embedded.filmResponseDtoList);
    //     }
    //   } catch {
    //     throw new Error("Có lỗi xảy ra khi lấy danh sách phim đang chiếu");
    //   }
    // };

    // const fetchFilmUpcoming = async () => {
    //   try {
    //     const response = await getUpcommingFilms();
    //     if (response) {
    //       setUpcomingFilm(response._embedded.filmResponseDtoList);
    //     }
    //   } catch {
    //     throw new Error("Có lỗi xảy ra khi lấy danh sách phim sắp chiếu");
    //   }
    // };

    fetchFilmData();

    // fetchFilmShowing();
    // fetchFilmUpcoming();
  }, []);

  console.log("dang chieu: ", filmShowing);
  console.log("sap chieu: ", upcomingFilm);

  return (
    <div className="container mx-auto px-4 md:px-6 lg:px-8 py-6 max-w-8xl">
      <img
        src="https://api-website.cinestar.com.vn/media/MageINIC/bannerslider/bap-nuoc-onl.jpg"
        alt="Movie Banner"
        className="w-full h-full object-cover pb-5"
      />
      <QuickBooking />
      <FilmListSection
        title="PHIM ĐANG CHIẾU"
        filmList={filmShowing}
        onCLickSeeMore={() => navigate("/movie/showing")}
      />
      <FilmListSection
        title="PHIM SẮP CHIẾU"
        filmList={upcomingFilm}
        onCLickSeeMore={() => navigate("/movie/upcoming")}
      />
    </div>
  );
};

export default HomePage;
