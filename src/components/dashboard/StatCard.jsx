function StatCard({ label, value, description, icon: Icon, tone = "default" }) {
  return (
    <div className={`stat-card stat-card-${tone}`}>
      <div className="stat-card-top">
        <div className="stat-card-icon">
          <Icon size={20} strokeWidth={1.8} />
        </div>
      </div>

      <div className="stat-card-value">{value}</div>

      <div className="stat-card-label">{label}</div>

      <div className="stat-card-description">{description}</div>
    </div>
  );
}

export default StatCard;