import { HeartHandshake, ArrowRight } from "lucide-react";

function FamilyMissions() {
  return (
    <section className="dashboard-card family-missions-card">
      <div className="family-missions-content">
        <div className="family-missions-icon">
          <HeartHandshake size={24} />
        </div>

        <div className="family-missions-text">
          <span className="eyebrow">FAMILY LEARNING MISSIONS</span>

          <h2>Make learning a family activity</h2>

          <p>
            Create short educational activities that students and
            parents can complete together at home.
          </p>

          <button className="primary-button">
            Create Family Mission
            <ArrowRight size={16} />
          </button>
        </div>

        <div className="family-mission-stat">
          <strong>8</strong>
          <span>Active missions</span>
        </div>
      </div>
    </section>
  );
}

export default FamilyMissions;