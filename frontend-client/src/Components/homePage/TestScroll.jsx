import { useRef } from "react";

export default function ScrollTest() {
  const scrollRef = useRef(null);

  const handleNext = () => {
    const container = scrollRef.current;
    container.style.transition = "transform 0.5s ease-in-out";
    container.style.transform = "translateX(-300px)";
  };

  return (
    <div className="p-10">
      <button
        onClick={handleNext}
        className="mb-4 px-4 py-2 bg-blue-500 text-white"
      >
        Scroll Right
      </button>

      <div
        ref={scrollRef}
        className="flex gap-4 overflow-x-hidden"
        style={{ scrollBehavior: "smooth", width: "800px" }}
      >
        {Array.from({ length: 10 }).map((_, i) => (
          <div
            key={i}
            className="min-w-[256px] h-32 bg-yellow-300 flex items-center justify-center text-2xl font-bold"
          >
            Box {i + 1}
          </div>
        ))}
      </div>
    </div>
  );
}
