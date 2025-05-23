// Spinner.jsx
const Spinner = ({ size = 20, color = "border-purple-600" }) => (
  <div
    className={`animate-spin rounded-full border-t-2 border-r-2 border-gray-200 ${color}`}
    style={{ width: size, height: size }}
  />
);

export default Spinner;
