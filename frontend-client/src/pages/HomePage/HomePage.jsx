import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import QuickBooking from "../../Components/homePage/QuickBooking";
import FilmListSection from "../../Components/homePage/FilmListSection";
import { getShowingFilms, getUpcommingFilms } from "../../config/api";
import eventBus from "../../utils/eventBus"; // Import event bus

const HomePage = () => {
  const [filmShowing, setFilmShowing] = useState([]);
  const [upcomingFilm, setUpcomingFilm] = useState([]);
  const navigate = useNavigate();

  const mockShowingFilms = [
    {
      _id: "1",
      name: "Avengers: Endgame",
      thumbnailURL:
        "https://image.tmdb.org/t/p/w500/q6725aR8Zs4IwGMXzZT8aC8lh41.jpg",
      originatedCountry: "USA",
      filmDuration: 181,
      ageRestriction: "13+",
      voice: "Tiếng Anh",
      trailerURL: "https://www.youtube.com/watch?v=TcMBFSGVi1c",
      twoDthreeD: ["2D", "3D"],
      isShowing: true,
    },
    {
      _id: "2",
      name: "The Batman",
      thumbnailURL:
        "https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg",
      originatedCountry: "USA",
      filmDuration: 176,
      ageRestriction: "16+",
      voice: "Tiếng Anh",
      trailerURL: "https://www.youtube.com/watch?v=mqqft2x_Aa4",
      twoDthreeD: ["2D"],
      isShowing: true,
    },
  ];

  // Mock Data - Danh sách phim sắp chiếu
  const mockUpcomingFilms = [
    {
      _id: "3",
      name: "Deadpool 3",
      thumbnailURL: "https://image.tmdb.org/t/p/w500/deadpool3.jpg",
      originatedCountry: "USA",
      filmDuration: 120,
      ageRestriction: "18+",
      voice: "Tiếng Anh",
      trailerURL: "https://www.youtube.com/watch?v=dummyurl",
      twoDthreeD: ["2D", "IMAX"],
      isShowing: false,
    },
    {
      _id: "4",
      name: "Spider-Man: Beyond the Spider-Verse",
      thumbnailURL: "https://image.tmdb.org/t/p/w500/spiderman.jpg",
      originatedCountry: "USA",
      filmDuration: 140,
      ageRestriction: "13+",
      voice: "Tiếng Anh",
      trailerURL: "https://www.youtube.com/watch?v=dummyurl2",
      twoDthreeD: ["2D", "3D"],
      isShowing: false,
    },
  ];

  useEffect(() => {
    document.title = "Trang chủ";

    // const fetchFilmShowing = async () => {
    //   try {
    //     const response = await getShowingFilms();
    //     if (response && response.data) {
    //       setFilmShowing(response.data);
    //     }
    //   } catch {
    //     throw new Error("Có lỗi xảy ra khi lấy danh sách phim đang chiếu");
    //   }
    // };

    // const fetchFilmUpcoming = async () => {
    //   try {
    //     const response = await getUpcommingFilms();
    //     if (response && response.data) {
    //       setUpcomingFilm(response.data);
    //     }
    //   } catch {
    //     throw new Error("Có lỗi xảy ra khi lấy danh sách phim sắp chiếu");
    //   }
    // };

    // // Lắng nghe sự kiện để lấy dữ liệu phim
    // eventBus.on("fetchFilms", () => {
    //   fetchFilmShowing();
    //   fetchFilmUpcoming();
    // });

    // // Gửi sự kiện để lấy phim ngay khi trang được load
    // eventBus.emit("fetchFilms");

    // // Cleanup event listener khi component unmount
    // return () => {
    //   eventBus.off("fetchFilms");
    // };
    setFilmShowing(mockShowingFilms);
    setUpcomingFilm(mockUpcomingFilms);
  }, []);

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
