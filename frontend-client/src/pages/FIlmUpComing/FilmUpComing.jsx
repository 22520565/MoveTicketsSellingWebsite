import { useEffect, useState } from "react";
import FilmCard from "../../Components/filmCard/index";
import axios from "axios";
import { getAllFilms, getShowingFilms } from "../../config/api";

const FilmUpcoming = () => {
  const [filmShowing, setFilmShowing] = useState([]);

  useEffect(() => {
    // Khi component mounted, reset scroll về đầu
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, []);

  const fetchFilmData = async () => {
    try {
      const [allRes, showingRes] = await Promise.all([
        getAllFilms(),
        getShowingFilms(),
      ]);

      const allFilms = allRes._embedded.filmResponseDtoList || [];
      const showingFilms = showingRes?._embedded?.filmResponseDtoList || [];

      let upcoming = [];

      if (showingFilms.length === 0) {
        // Nếu không có phim đang chiếu → upcoming = tất cả
        upcoming = allFilms;
      } else {
        // Ngược lại → lọc ra những phim chưa chiếu
        const showingIds = new Set(showingFilms.map((film) => film.id));
        upcoming = allFilms.filter((film) => !showingIds.has(film.id));
      }

      setFilmShowing(upcoming);
    } catch (error) {
      console.error("Lỗi khi lấy dữ liệu phim:", error);
      throw new Error("Có lỗi xảy ra khi lấy dữ liệu phim.");
    }
  };

  useEffect(() => {
    document.title = "Phim sắp chiếu";

    fetchFilmData();
  }, []);

  if (!filmShowing || filmShowing.length === 0) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex flex-col justify-center items-center gap-20 py-20">
      <h1 className="font-interExtraBold">PHIM SẮP CHIẾU</h1>
      <div className="flex flex-wrap justify-center items-center gap-4 md:gap-12">
        {filmShowing.map((film) => (
          <FilmCard
            key={film.id}
            filmId={film.id}
            imageUrl={film.thumbnailUrl || ""}
            name={film.name || "Không có tên"}
            country={film.originatedCountry || "Không rõ"}
            type={film.tagIds || "Chưa xác định"}
            duration={film.duration || 0}
            ageLimit={film.ageRestriction || "Không rõ"}
            voice={film.voice || "Không rõ"}
            trailerURL={film.trailerUrl}
            twoDthreeD={film.is3D ? ["2D", "3D"] : ["2D"]}
            isShowing={true}
          />
        ))}
      </div>
    </div>
  );
};

export default FilmUpcoming;
