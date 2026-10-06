import { CalendarCheck } from "lucide-react";

function AttendanceCard() {
  return (
    <section className="dashboard-card attendance-card">
      <div className="dashboard-card-header">
        <div>
          <h2>Attendance</h2>
          <p>Today's attendance overview</p>
        </div>

        <CalendarCheck size={20} />
      </div>

      <div className="attendance-main">
        <div className="attendance-percentage">95%</div>
        <span>Overall attendance</span>
      </div>

      <div className="attendance-stats">
        <div>
          <strong>30</strong>
          <span>Present</span>
        </div>

        <div>
          <strong>2</strong>
          <span>Absent</span>
        </div>

        <div>
          <strong>1</strong>
          <span>Late</span>
        </div>
      </div>
    </section>
  );
}

export default AttendanceCard;