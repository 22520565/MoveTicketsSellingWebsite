import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import QuickBooking from "../../Components/homePage/QuickBooking";
import FilmListSection from "../../Components/homePage/FilmListSection";
import { getShowingFilms, getUpcommingFilms } from "../../config/api";
import eventBus from "../../utils/eventBus"; // Import event bus
import { LogIn } from "lucide-react";
import CinemaScheduleList from "../../Components/CinemaList";
const HomePage = () => {
  const [filmShowing, setFilmShowing] = useState([]);
  const [upcomingFilm, setUpcomingFilm] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    document.title = "Trang chủ";

    const fetchFilmShowing = async () => {
      try {
        const response = await getShowingFilms();

        if (response) {
          setFilmShowing(response._embedded.filmResponseDtoList);
        }
      } catch {
        throw new Error("Có lỗi xảy ra khi lấy danh sách phim đang chiếu");
      }
    };

    const fetchFilmUpcoming = async () => {
      try {
        const response = await getUpcommingFilms();
        if (response) {
          setUpcomingFilm(response._embedded.filmResponseDtoList);
        }
      } catch {
        throw new Error("Có lỗi xảy ra khi lấy danh sách phim sắp chiếu");
      }
    };

    fetchFilmShowing();
    fetchFilmUpcoming();
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

      <CinemaScheduleList />
    </div>
  );
};

export default HomePage;
