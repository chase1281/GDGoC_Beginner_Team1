import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Auth.css";

const API_BASE_URL = "http://localhost:8080";

function RegisterPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [password2, setPassword2] = useState("");

  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [isRegistered, setIsRegistered] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg("");
    setSuccessMsg("");

    if (!email.trim() || !name.trim() || !password.trim() || !password2.trim()) {
      setErrorMsg("이메일, 이름, 비밀번호를 입력해주세요.");
      return;
    }
    if (name.length > 20) {
      setErrorMsg("이름은 20자 이하로 입력해주세요.");
      return;
    }
    if (password.length < 8) {
      setErrorMsg("비밀번호는 8자 이상으로 입력해주세요.");
      return;
    }
    if (password !== password2) {
      setErrorMsg("비밀번호를 다시 확인해주세요.");
      return;
    }

    try {
      setLoading(true);

      const res = await fetch(`${API_BASE_URL}/members/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, name, password }),
      });

      const text = await res.text().catch(() => "");

      if (!res.ok) {
        let msg = text;
        try {
          const json = JSON.parse(text);
          msg = json?.message || msg;
        } catch {}
        throw new Error(msg || "회원가입 실패");
      }

      setSuccessMsg(text || "회원가입이 성공했습니다.");
      setIsRegistered(true);
    } catch (err) {
      setErrorMsg(err?.message || "오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h1 className="auth-title">회원가입</h1>

      {!isRegistered ? (
        <form onSubmit={handleSubmit} className="auth-form">
          <label className="auth-label">
            이메일
            <input
              className="auth-input"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="email@example.com"
              autoComplete="email"
              disabled={loading}
            />
          </label>

          <label className="auth-label">
            이름
            <input
              className="auth-input"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="1~20자"
              maxLength={20}
              autoComplete="name"
              disabled={loading}
            />
          </label>

          <label className="auth-label">
            비밀번호
            <input
              className="auth-input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="8자 이상"
              autoComplete="new-password"
              disabled={loading}
            />
          </label>

          <label className="auth-label">
            비밀번호 확인
            <input
              className="auth-input"
              type="password"
              value={password2}
              onChange={(e) => setPassword2(e.target.value)}
              placeholder="비밀번호 확인"
              autoComplete="new-password"
              disabled={loading}
            />
          </label>

          {errorMsg && <p className="auth-msg-error">{errorMsg}</p>}
          {successMsg && <p className="auth-msg-success">{successMsg}</p>}

          <div className="auth-actions">
            <button className="auth-button" type="submit" disabled={loading}>
              {loading ? "가입중.." : "회원가입"}
            </button>

            <button
              className="auth-button"
              type="button"
              onClick={() => navigate("/")}
              disabled={loading}
            >
              메인으로 돌아가기
            </button>
          </div>
        </form>
      ) : (
        <div className="auth-actions">
          <p className="auth-msg-success">{successMsg || "회원가입이 성공했습니다."}</p>

          <button className="auth-button" type="button" onClick={() => navigate("/login")}>
            로그인하러가기
          </button>

          <button className="auth-button" type="button" onClick={() => navigate("/")}>
            홈으로 돌아가기
          </button>
        </div>
      )}
    </div>
  );
}

export default RegisterPage;
