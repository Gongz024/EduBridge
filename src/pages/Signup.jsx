import { useState } from "react";
import { Link, Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";

const MIN_PASSWORD_LENGTH = 8;

function Signup() {
  const { user, loading, signup } = useAuth();

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (loading) return null;

  if (user) {
    return <Navigate to="/" replace />;
  }

  const updateField = (field) => (event) =>
    setForm((current) => ({ ...current, [field]: event.target.value }));

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    if (form.password.length < MIN_PASSWORD_LENGTH) {
      setError(`Password must be at least ${MIN_PASSWORD_LENGTH} characters.`);
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setSubmitting(true);

    try {
      await signup({
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        password: form.password,
      });
    } catch (err) {
      setError(err.message);
      setSubmitting(false);
    }
  }

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleSubmit}>
        <div className="login-brand">
          <div className="brand-mark">E</div>
          <div>
            <div className="brand-name">EduBridge</div>
            <div className="brand-role">Teacher Workspace</div>
          </div>
        </div>

        <h1>Create your account</h1>
        <p className="page-description">
          Set up a teacher account to start using EduBridge.
        </p>

        <div className="form-row">
          <label className="form-field">
            <span>First name</span>
            <input
              type="text"
              value={form.firstName}
              onChange={updateField("firstName")}
              autoComplete="given-name"
              required
            />
          </label>

          <label className="form-field">
            <span>Last name</span>
            <input
              type="text"
              value={form.lastName}
              onChange={updateField("lastName")}
              autoComplete="family-name"
              required
            />
          </label>
        </div>

        <label className="form-field">
          <span>Email</span>
          <input
            type="email"
            value={form.email}
            onChange={updateField("email")}
            autoComplete="username"
            required
          />
        </label>

        <label className="form-field">
          <span>Password</span>
          <input
            type="password"
            value={form.password}
            onChange={updateField("password")}
            autoComplete="new-password"
            minLength={MIN_PASSWORD_LENGTH}
            required
          />
        </label>

        <label className="form-field">
          <span>Confirm password</span>
          <input
            type="password"
            value={form.confirmPassword}
            onChange={updateField("confirmPassword")}
            autoComplete="new-password"
            required
          />
        </label>

        {error && <p className="form-error">{error}</p>}

        <button type="submit" className="primary-button" disabled={submitting}>
          {submitting ? "Creating account..." : "Create account"}
        </button>

        <p className="auth-switch">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </form>
    </div>
  );
}

export default Signup;
