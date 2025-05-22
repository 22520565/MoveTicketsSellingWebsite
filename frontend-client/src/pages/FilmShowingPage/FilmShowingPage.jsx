import { useEffect, useState } from "react";
import FilmCard from "../../Components/filmCard/index";
import axios from "axios";
import { getShowingFilms } from "../../config/api";

const FilmShowingPage = () => {
  const [filmShowing, setFilmShowing] = useState([]);

  useEffect(() => {
    document.title = "Phim đang chiếu";
    const fetchFilmShowing = async () => {
      try {
        const response = await getShowingFilms();
        if (response) {
          setFilmShowing(response._embedded.filmResponseDtoList);
        }
      } catch {
        throw new Error("There is an error while getting film detail");
      }
    };
    fetchFilmShowing();
  }, []);

  if (!filmShowing || filmShowing.length === 0) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex flex-col justify-center items-center gap-20 py-20">
      <h1 className="font-interExtraBold">PHIM ĐANG CHIẾU</h1>
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

export default FilmShowingPage;
