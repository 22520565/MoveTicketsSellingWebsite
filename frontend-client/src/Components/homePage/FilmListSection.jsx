import { useState, useRef } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";
import FilmCard from "../filmCard";

const FilmListSection = ({
  title = "PHIM ĐANG CHIẾU",
  filmList,
  onCLickSeeMore,
}) => {
  const [currentPage, setCurrentPage] = useState(0);
  const scrollContainerRef = useRef(null);

  const itemsPerPage = 4;
  const totalPages = Math.ceil(filmList.length / itemsPerPage);

  // const handleNext = () => {
  //   if (scrollContainerRef.current && currentPage < totalPages - 1) {
  //     const container = scrollContainerRef.current;
  //     container.scrollTo({ left: container.offsetWidth, behavior: "smooth" });
  //     setCurrentPage((prev) => prev + 1);
  //   }
  // };

  // const handlePrev = () => {
  //   if (scrollContainerRef.current && currentPage > 0) {
  //     const container = scrollContainerRef.current;
  //     container.scrollTo({ left: -container.offsetWidth, behavior: "smooth" });
  //     setCurrentPage((prev) => prev - 1);
  //   }
  // };

  const handleNext = () => {
    if (currentPage < totalPages - 1) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  const handlePrev = () => {
    if (currentPage > 0) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  const translateX = `-${currentPage * 100}%`;

  return (
    <div className="py-12 relative">
      <h2 className="text-4xl font-bold text-center text-white mb-12">
        {title}
      </h2>

      <div className="relative overflow-hidden px-8">
        <div className="w-full overflow-hidden">
          <div
            className="flex transition-transform duration-500 ease-in-out"
            style={{
              width: `${totalPages * 100}%`,
              transform: `translateX(-${(100 / totalPages) * currentPage}%)`,
            }}
          >
            {Array.from({ length: totalPages }).map((_, pageIndex) => {
              const pageFilms = filmList.slice(
                pageIndex * itemsPerPage,
                pageIndex * itemsPerPage + itemsPerPage
              );
              return (
                <div
                  key={pageIndex}
                  className="flex w-full flex-shrink-0"
                  style={{ width: `${100 / totalPages}%` }}
                >
                  {pageFilms.map((film) => (
                    <div
                      key={film.id}
                      className="flex-none w-full sm:w-1/2 lg:w-1/4 px-3"
                    >
                      <FilmCard
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
                    </div>
                  ))}
                </div>
              );
            })}
          </div>
        </div>

        {/* Navigation Arrows */}
        {currentPage > 0 && (
          <button
            onClick={handlePrev}
            className="absolute left-0 top-1/2 -translate-y-1/2 p-2 bg-black/50 text-white rounded-full hover:bg-black/70 z-10"
          >
            <ChevronLeft size={24} />
          </button>
        )}

        {currentPage < totalPages - 1 && (
          <button
            onClick={handleNext}
            className="absolute right-0 top-1/2 -translate-y-1/2 p-2 bg-black/50 text-white rounded-full hover:bg-black/70 z-10"
          >
            <ChevronRight size={24} />
          </button>
        )}
      </div>

      {/* Pagination Dots */}
      <div className="flex justify-center gap-2 mt-8">
        {Array.from({ length: totalPages }).map((_, index) => (
          <div
            key={index}
            className={`w-3 h-3 rounded-full ${
              currentPage === index ? "bg-white" : "bg-white/30"
            }`}
          />
        ))}
      </div>

      {/* View More Button */}
      <div className="flex justify-center mt-8">
        <button
          onClick={onCLickSeeMore}
          className="px-12 py-3 border-2 border-yellow-400 text-yellow-400 rounded-lg hover:bg-yellow-400 hover:text-black transition-colors text-lg font-bold"
        >
          XEM THÊM
        </button>
      </div>
    </div>
  );
};

export default FilmListSection;
