import React, { useState } from "react";

export default function CinemaScheduleList({
  cinemasData,
  onSelectShowtime,
  selectedShowtime,
}) {
  console.log("cnime: ", selectedShowtime);
  console.log("data: ", cinemasData);

  const [selectedCity, setSelectedCity] = useState("Hồ Chí Minh");

  const cities = [...new Set(cinemasData.map((cinema) => cinema.city))];
  const cinemas = cinemasData.filter((cinema) => cinema.city === selectedCity);

  return (
    <div className=" min-h-screen p-4 text-white">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-4xl font-extrabold">DANH SÁCH RẠP</h1>
        <div className="relative">
          <select
            value={selectedCity}
            onChange={(e) => setSelectedCity(e.target.value)}
            className="text-lg bg-transparent border border-yellow-400 text-yellow-300 px-4 py-2 rounded cursor-pointer   transition"
          >
            {cities.map((city, idx) => (
              <option key={idx} value={city} className="text-black">
                {city}
              </option>
            ))}
          </select>
        </div>
      </div>

      {cinemas.map((cinema, index) => (
        <div
          key={index}
          className="bg-purple-700 rounded-lg p-8 mb-6 text-base sm:text-lg min-w-[900px]"
        >
          <h2 className="text-2xl font-extrabold text-yellow-400 mb-2">
            {cinema.name}
          </h2>
          <p className="mb-4 text-lg">{cinema.address}</p>

          {Object.entries(cinema.schedules).every(
            ([_, times]) => times.length === 0
          ) ? (
            <div className="p-4 border border-white text-white rounded flex items-center gap-2 ">
              <span>Hiện chưa có lịch chiếu</span>
            </div>
          ) : (
            Object.entries(cinema.schedules).map(([roomType, times]) => (
              <div key={roomType} className="mb-3 min-w-[300px]">
                <h3 className="font-semibold text-white mb-2 text-base">
                  {roomType}
                </h3>
                <div className="flex flex-wrap gap-3">
                  {times.map((show, idx) => (
                    <button
                      key={idx}
                      className={`border px-4 py-2 rounded text-base font-semibold transition ${
                        selectedShowtime.showTime.slice(0, 5) === show.showTime
                          ? "bg-yellow-400 text-black border-yellow-400"
                          : "border-white hover:bg-yellow-400 hover:text-black"
                      }`}
                      onClick={() => {
                        onSelectShowtime &&
                          onSelectShowtime(show.showTime, show.filmShowId);
                      }}
                    >
                      {show.showTime}
                    </button>
                  ))}
                </div>
              </div>
            ))
          )}
        </div>
      ))}
    </div>
  );
}
