import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

function MainPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  // 페이지 들어올 때 localStorage에서 로그인 정보 가져오기
  useEffect(() => {
    const saved = localStorage.getItem("user");
    if (saved) {
      try {
        setUser(JSON.parse(saved));
      } catch {
        setUser(null);
      }
    }
  }, []);

  // 로그아웃
  const handleLogout = () => {
    localStorage.removeItem("user");
    setUser(null);
    navigate("/login");
  };

  return (
    <div style={{ maxWidth: 520, margin: "40px auto", padding: 16 }}>
      <h1>메인 페이지</h1>

      {user ? (
        <>
          <p style={{ marginTop: 10 }}>
            <b>{user.name}</b>님 환영합니다!
          </p>
          <button onClick={handleLogout} style={{ marginTop: 16, padding: 12 }}>
            로그아웃
          </button>
        </>
      ) : (
        <>
          
          <div style={{ display: "flex", gap: 10, marginTop: 16 }}>
            <Link to="/login">
              <button style={{ padding: 12 }}>로그인</button>
            </Link>

            <Link to="/register">
              <button style={{ padding: 12 }}>회원가입</button>
            </Link>
          </div>
          <p style={{ marginTop: 10, color: "#666" }}>
            로그인 후 서비스를 이용할 수 있습니다.
          </p>

        </>
      )}
    </div>
  );
}

export default MainPage;
