import React, { useState } from "react";

const allCinemas = [
  {
    city: "TP.HCM",
    name: "Cinestar Satra Quận 6 (TP.HCM)",
    address:
      "Tầng 6, TTTM Satra Võ Văn Kiệt, 1466 Võ Văn Kiệt, Phường 1, Quận 6, TP.HCM",
    schedules: {
      Standard: [
        "08:20",
        "09:50",
        "10:50",
        "13:20",
        "14:15",
        "16:40",
        "19:30",
        "21:00",
        "23:30",
      ],
    },
  },
  {
    city: "TP.HCM",
    name: "Cinestar Quận 1 (TP.HCM)",
    address: "135 Hai Bà Trưng, Quận 1, TP.HCM",
    schedules: {
      Standard: ["09:15", "11:45", "14:10", "19:30"],
    },
  },
  {
    city: "Hà Nội",
    name: "CGV Times City",
    address: "458 Minh Khai, Hai Bà Trưng, Hà Nội",
    schedules: {
      Standard: ["10:00", "13:00", "16:00", "20:00"],
    },
  },
];

export default function CinemaScheduleList() {
  const [selectedCity, setSelectedCity] = useState("TP.HCM");

  const cities = [...new Set(allCinemas.map((cinema) => cinema.city))];
  const cinemas = allCinemas.filter((cinema) => cinema.city === selectedCity);

  return (
    <div className=" min-h-screen p-4 text-white">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-4xl font-extrabold">DANH SÁCH RẠP</h1>
        <div className="relative">
          <select
            value={selectedCity}
            onChange={(e) => setSelectedCity(e.target.value)}
            className="text-lg bg-transparent border border-yellow-400 text-yellow-300 px-4 py-2 rounded cursor-pointer hover:bg-yellow-500 hover:text-black transition"
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
          className="bg-purple-700 rounded-lg p-8 mb-6 text-base sm:text-lg"
        >
          <h2 className="text-2xl font-extrabold text-yellow-400 mb-2">
            {cinema.name}
          </h2>
          <p className="mb-4 text-lg">{cinema.address}</p>

          {Object.entries(cinema.schedules).map(([roomType, times]) => (
            <div key={roomType} className="mb-3">
              <h3 className="font-semibold text-white mb-2 text-base">
                {roomType}
              </h3>
              <div className="flex flex-wrap gap-3">
                {times.map((time, idx) => (
                  <button
                    key={idx}
                    className="border border-white px-4 py-2 rounded text-base font-semibold hover:bg-yellow-400 hover:text-black transition"
                  >
                    {time}
                  </button>
                ))}
              </div>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
}
