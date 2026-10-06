import { Clock, BookOpen } from "lucide-react";

const lessons = [
  {
    time: "9:00 AM",
    subject: "Mathematics",
    className: "IT 4A",
  },
  {
    time: "10:15 AM",
    subject: "English",
    className: "IT 4A",
  },
  {
    time: "11:30 AM",
    subject: "Science",
    className: "IT 4B",
  },
];

function UpcomingLessons() {
  return (
    <section className="dashboard-card upcoming-lessons">
      <div className="dashboard-card-header">
        <div>
          <h2>Upcoming Lessons</h2>
          <p>Your scheduled classes for today</p>
        </div>

        <button className="text-button">View schedule</button>
      </div>

      <div className="lesson-list">
        {lessons.map((lesson) => (
          <div className="lesson-item" key={`${lesson.time}-${lesson.subject}`}>
            <div className="lesson-time">
              <Clock size={16} />
              <span>{lesson.time}</span>
            </div>

            <div className="lesson-icon">
              <BookOpen size={18} />
            </div>

            <div className="lesson-details">
              <strong>{lesson.subject}</strong>
              <span>{lesson.className}</span>
            </div>

            <span className="lesson-status">Upcoming</span>
          </div>
        ))}
      </div>
    </section>
  );
}

export default UpcomingLessons;