import React, { useState, useEffect } from "react";
import {
  FaFilm,
  FaFileExport,
  FaShopify,
  FaCreditCard,
  FaUtensils,
} from "react-icons/fa";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import LineChartComponent from "../../components/Statistic/ColumnChart";
import PieCharts from "../../components/Statistic/PieChart";
import {
  getCinemas,
  getTiketServeRate,
  getTiketCategoryRate,
  getMonthlyStatisticsByTheater,
  getHotFilmStatistics,
  getFilmStatistics,
  getDailyStatisticsByTheater,
  getBestSellingItem,
  getAdditionalItemsRate,
  getTiketRateByFilm,
} from "../../config/api";
import axios from "axios";
import { Theater } from "lucide-react";
import { get } from "lodash";
import { use } from "react";

const StatisticPage = () => {
  const [selectedCinemaId, setSelectedCinemaId] = useState(""); // rạp đang chọn
  const [cinemas, setCinemas] = useState([]); // danh sách rạp
  const [selectedDate, setSelectedDate] = useState(
    new Date().toISOString().split("T")[0] // Định dạng yyyy-mm-dd
  );
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [ticketTypeData, setTicketTypeData] = useState([]);
  const [ticketMovieData, setTicketMovieData] = useState([]);
  const [itemData, setItemData] = useState([]);

  const [revenueDataByYear, setRevenueDataByYear] = useState({});

  const [statistics, setStatistics] = useState({
    totalNetRevenue: 0,
    totalEffectiveRevenue: 0,
    totalTicketRevenue: 0,
    totalOtherItemsRevenue: 0,
  });

  useEffect(() => {
    const fetchCinemas = async () => {
      try {
        const res = await getCinemas(); // hoặc endpoint tương ứng

        setCinemas(res.data._embedded.theaterResponseDtoList);
        setSelectedCinemaId(
          res.data._embedded.theaterResponseDtoList[0]?.id || ""
        );
      } catch (err) {
        console.error("Lỗi lấy danh sách rạp:", err);
      }
    };

    fetchCinemas();
  }, []);

  //biểu đồ loại vé
  const fetchticketType = async (day) => {
    try {
      const response = await getTiketCategoryRate({
        date: day,
        theaterId: selectedCinemaId,
      });

      const data =
        response?.data?._embedded?.ticketCategoryRevenueResponseDtoList || [];

      console.log("loại vé: ", data);

      const groupedData = data?.reduce((acc, item) => {
        acc[item.name] = (acc[item.name] || 0) + item.totalRevenue;
        return acc;
      }, {});

      const transformedData = Object.entries(groupedData).map(
        ([name, value]) => ({
          name,
          value,
        })
      );

      setTicketTypeData(transformedData);
    } catch (error) {
      console.log(error);
    }
  };

  //biểu đồ phim
  const fetchticketMovie = async (day) => {
    try {
      const response = await getTiketRateByFilm({
        date: day,
        theaterId: selectedCinemaId,
      });

      const data = response?.data?._embedded?.ticketRateOfFilmResponseDtoList;

      console.log("vé theo phim: ", data);

      const transformedData = data?.map((item) => ({
        name: item.filmName,
        value: item.totalTicket,
      }));

      setTicketMovieData(transformedData || []);
    } catch (error) {
      alert("Thao tác thất bại, lỗi: " + error.response.data.msg);
    }
  };

  //biểu đồ snar phẩm kahcs
  const fetchadditionalItem = async (day) => {
    try {
      const response = await getAdditionalItemsRate({
        date: day,
        theaterId: selectedCinemaId,
      });

      const data = response?.data?._embedded?.itemRevenueResponseDtoList;

      console.log("sản phẩm khác: ", data);

      const transformedData = data?.map((item) => ({
        name: item.name,
        value: item.totalRevenue,
      }));
      setItemData(transformedData);
    } catch (error) {
      console.log(error);
    }
  };

  const fetchView = async (day) => {
    try {
      const response = await getDailyStatisticsByTheater(day, selectedCinemaId);

      const data = response?.data;
      setStatistics((prev) => ({
        ...prev, // Giữ lại các giá trị cũ
        totalNetRevenue: data?.totalNetRevenue,
        totalEffectiveRevenue: data?.totalEffectiveRevenue,
        totalTicketRevenue: data?.totalTicketRevenue,
        totalOtherItemsRevenue: data?.totalItemRevenue,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  //vé đã bán
  const fetchTicket = async (day) => {
    try {
      console.log(day);

      const response = await getTiketServeRate({
        date: day,
        theaterId: selectedCinemaId,
      });

      const data = response?.data;
      setStatistics((prev) => ({
        ...prev, // Giữ lại các giá trị cũ
        totalTicket: data?.totalTickets,
        sservedTickets: data?.servedTickets,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  //phim hot nhất nagfy
  const fetchHotFilm = async (day) => {
    try {
      console.log(day);

      const response = await getHotFilmStatistics({
        date: day,
        theaterId: selectedCinemaId,
      });

      const data = response?.data;

      setStatistics((prev) => ({
        ...prev, // Giữ lại các giá trị cũ
        hotFilmName: data?.filmName,
        hotFilmTotalSeat: data?.totalSeat,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  //sản phẩm hot nhất nagfy
  const fetchBestSeller = async (day) => {
    try {
      console.log(day);

      const response = await getBestSellingItem({
        date: day,
        theaterId: selectedCinemaId,
      });

      const data = response?.data;
      setStatistics((prev) => ({
        ...prev, // Giữ lại các giá trị cũ
        bestSellerName: data?.productName,
        bestSellerTotal: data?.totalQuantity,
      }));
    } catch (error) {
      alert("Thao tác thất bại, lỗi: " + error.response.data.msg);
    }
  };

  const fetchData = async (year) => {
    try {
      const response = await getMonthlyStatisticsByTheater(
        year,
        selectedCinemaId
      );

      const transformedData = transformApiDataToRevenueData(
        response?.data,
        year
      );
      setRevenueDataByYear((prev) => ({
        ...prev,
        [year]: transformedData,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    fetchView(selectedDate);
    fetchTicket(selectedDate);
    fetchticketType(selectedDate);
    fetchticketMovie(selectedDate);
    fetchadditionalItem(selectedDate);
    fetchHotFilm(selectedDate);
    fetchBestSeller(selectedDate);
    fetchData(selectedYear);
  }, [selectedDate, selectedYear, selectedCinemaId]);

  const transformApiDataToRevenueData = (apiData, year) => {
    return apiData.map((item) => {
      const { month, totalNetRevenue, totalEffectiveRevenue } = item;
      const monthName = new Date(0, month - 1).toLocaleString("en-US", {
        month: "2-digit",
      });

      return {
        month: monthName,
        thuần: totalNetRevenue,
        thựctế: totalEffectiveRevenue,
      };
    });
  };
  const handleExport = () => {
    const csvData = revenueDataByYear[selectedYear];
    const csvString = [
      ["Month", "Net", "Effective"],
      ...csvData.map((row) => [row.month, row.net, row.effective]),
    ]
      .map((e) => e.join(","))
      .join("\n");

    const blob = new Blob([csvString], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute("href", url);
      link.setAttribute("download", `revenue-data-${selectedYear}.csv`);
      link.style.visibility = "hidden";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  // console.log(statistics);

  return (
    <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Báo cáo hằng ngày</h1>

        <div className="flex items-center space-x-4">
          <select
            value={selectedCinemaId}
            onChange={(e) => setSelectedCinemaId(e.target.value)}
            className="p-2 border rounded-md"
          >
            {cinemas.map((cinema) => (
              <option key={cinema.id} value={cinema.id}>
                {cinema.name}
              </option>
            ))}
          </select>
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => {
              const localDate = e.target.value; // Lấy trực tiếp giá trị yyyy-mm-dd
              setSelectedDate(localDate); // Cập nhật state
              console.log(localDate);
            }}
            className="p-2 border rounded-md"
          />
          {/* <button
            onClick={handleExport}
            className="flex items-center space-x-2 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
          >
            <FaFileExport />
            <span>Export CSV</span>
          </button> */}
        </div>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-8">
        <div className="bg-blue-50 p-6 rounded-lg">
          <div className="flex items-center space-x-4">
            <FaFilm className="text-4xl text-blue-600" />
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Số vé đã bán ra
              </h3>
              <p className="text-3xl font-bold text-blue-600">
                {statistics?.totalTicket}
              </p>
            </div>
          </div>
        </div>
        <div className="bg-blue-50 p-6 rounded-lg">
          <div className="flex items-center space-x-4">
            <FaShopify className="text-4xl text-yellow-600" />
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Tổng doanh thu ngày (thuần)
              </h3>
              <p className="text-3xl font-bold text-yellow-600">
                {Number(statistics?.totalNetRevenue.toFixed(0)).toLocaleString(
                  "vi-VN"
                )}
              </p>
            </div>
          </div>
        </div>
        <div className="bg-blue-50 p-6 rounded-lg">
          <div className="flex items-center space-x-4">
            <FaShopify className="text-4xl text-yellow-600" />
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Tổng doanh thu ngày (thực tế)
              </h3>
              <p className="text-3xl font-bold text-yellow-600">
                {Number(
                  statistics?.totalEffectiveRevenue.toFixed(0)
                ).toLocaleString("vi-VN")}
              </p>
            </div>
          </div>
        </div>
        <div className="bg-green-50 p-6 rounded-lg">
          <div className="flex items-center space-x-4">
            <FaCreditCard className="text-4xl text-green-600" />
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Doanh thu từ vé (thuần)
              </h3>
              <p className="text-3xl font-bold text-green-600">
                {Number(
                  statistics?.totalTicketRevenue.toFixed(0)
                ).toLocaleString("vi-VN")}
              </p>
            </div>
          </div>
        </div>
        <div className="bg-purple-50 p-6 rounded-lg">
          <div className="flex items-center space-x-4">
            <FaUtensils className="text-4xl text-purple-600" />
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Doanh thu từ các sản phẩm ngoài (thuần)
              </h3>
              <p className="text-3xl font-bold text-purple-600">
                {Number(
                  statistics?.totalOtherItemsRevenue.toFixed(0)
                ).toLocaleString("vi-VN")}
              </p>
            </div>
          </div>
        </div>
      </div>
      <div className="my-4 text-lg font-medium text-gray-800">
        <p>
          Phim <span className="text-red-600 font-bold">hot</span> nhất ngày:
          <span className="font-bold"> {statistics?.hotFilmName}</span> -
          <span> {statistics?.hotFilmTotalSeat} ghế đặt.</span>
        </p>
        <p>
          Sản phẩm bán chạy:
          <span className="font-bold"> {statistics?.bestSellerName}</span> -
          <span> {statistics?.bestSellerTotal} bán ra.</span>
        </p>
      </div>

      <PieCharts
        movieData={ticketMovieData}
        ticketStatusData={itemData}
        ticketTypeData={ticketTypeData}
      />
      <LineChartComponent
        revenueDataByYear={revenueDataByYear}
        selectedYear={selectedYear}
        setSelectedYear={setSelectedYear}
      />
    </div>
  );
};

export default StatisticPage;
