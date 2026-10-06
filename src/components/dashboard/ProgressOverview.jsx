function ProgressBar({ label, value }) {
  return (
    <div className="progress-row">
      <div className="progress-row-header">
        <span>{label}</span>
        <strong>{value}%</strong>
      </div>

      <div className="progress-track">
        <div
          className="progress-fill"
          style={{ width: `${value}%` }}
        ></div>
      </div>
    </div>
  );
}

function ProgressOverview() {
  return (
    <section className="dashboard-card">
      <div className="dashboard-card-header">
        <div>
          <h2>Student Progress</h2>
          <p>Overall performance across your classes</p>
        </div>

        <button className="text-button">View progress</button>
      </div>

      <div className="progress-content">
        <ProgressBar label="Average Grade" value={89} />
        <ProgressBar label="Attendance" value={95} />
        <ProgressBar label="Activity Completion" value={87} />
      </div>
    </section>
  );
}

export default ProgressOverview;