import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { apiFetch } from "../api";

function MainPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [createOpen, setCreateOpen] = useState(false);

  const [form, setForm] = useState({
    title: "",
    description: "",
    maxMembers: 6,
    recruitmentStartDate: "",
    recruitmentEndDate: "",
  });

  //화면에 보여줄 모집글 목록 데이터
  const [studies,setStudies] = useState([]);

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

  //모집글 목록 불러오기 
  useEffect(() => {
    const fetchBoards = async () => {
      try {
        const data = await apiFetch("/boards/recruiting");
        console.log("recruiting data:", data);

        const mapped = Array.isArray(data) ? data.map(mapBoardToStudy) : [];
        setStudies(mapped);
      } catch (e) {
        console.error("fetchBoards error:", e);
      }
    };

    fetchBoards();
  }, []);

  // 로그아웃
  const handleLogout = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setUser(null);
    navigate("/");
};

  const mapBoardToStudy = (b) => ({
    id: b.boardId ?? b.id,
    title: b.title ?? "",
    description: b.content ?? b.description ?? "",
    leader: b.leader ?? b.writerName ?? b.authorName ?? "알 수 없음",
    members: b.currentCount ?? b.members ?? 0,
    maxMembers: b.capacity ?? b.maxMembers ?? 0,
    date:
      b.recruitmentStartDate && b.recruitmentEndDate
        ? `${b.recruitmentStartDate} ~ ${b.recruitmentEndDate}`
        : "",
    status: b.status === "RECRUITING" ? "모집중":"모집완료"
  });

  // 모달 상태: 어떤 스터디를 보여줄지 id로 관리
  const [modalStudyId, setModalStudyId] = useState(null);
  const modalStudy = studies.find(s => s.id === modalStudyId);

  // 스터디 가입 버튼 클릭 핸들러
  const handleJoinStudy = () => {
    if (!user) {
      navigate("/login");
      return;
    }

    // 실제 가입 로직은 추후 구현
    alert("스터디 가입 기능은 아직 구현되지 않았습니다.");
  };

  //글 작성 등록 버튼 클릭 핸들러 
  const handleCreateStudy = async () => {
    if (!user) {
      alert("로그인 후 작성할 수 있습니다.");
      navigate("/login");
      return;
    }

    if (!form.title.trim()) return alert("제목을 입력하세요.");
    if (!form.description.trim()) return alert("설명을 입력하세요.");
    if (!form.maxMembers || form.maxMembers < 1)
      return alert("최대 인원은 1명 이상이어야 해요.");

    if (!form.recruitmentStartDate) return alert("모집 시작일을 입력하세요.");
    if (!form.recruitmentEndDate) return alert("모집 종료일을 입력하세요.");
    if (form.recruitmentEndDate < form.recruitmentStartDate)
      return alert("모집 종료일은 시작일 이후여야 해요.");


    try {
      const payload = {
        title: form.title,
        content: form.description,                  
        capacity: Number(form.maxMembers),         
        recruitmentStartDate: `${form.recruitmentStartDate}T00:00:00`,
        recruitmentEndDate: `${form.recruitmentEndDate}T23:59:59`,
      };

      await apiFetch("/boards/create", {
        method: "POST",
        body: JSON.stringify(payload),
      });

      const latest = await apiFetch("/boards/recruiting");
      setStudies(Array.isArray(latest) ? latest.map(mapBoardToStudy) : []);

      setCreateOpen(false);
      setForm({
         title: "", 
         description: "", 
         maxMembers: 6, 
         recruitmentStartDate: "",
         recruitmentEndDate: "",});

      alert("모집글이 등록되었습니다!");
    } catch (e) {
      alert(e?.message || "모집글 등록 실패");
    }
  };


  return (
    <div className="main-container" style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
      <header style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "20px 40px", borderBottom: "1px solid #eee", background: "#f8f9fa" }}>
        <div style={{ fontWeight: "bold", fontSize: 22 }}>
          GDGoC 스터디 게시판
        </div>

        <nav style={{ display: "flex", gap: 24 }}>
          <Link to="/">홈</Link>
          <Link to="/my">내 스터디</Link>
        </nav>

        <div style={{ display: "flex", gap: 10 }}>
          {user ? (
            <>
              <span style={{ fontWeight: "bold" }}>{user.name}님</span>
              <button onClick={handleLogout} style={{ padding: "8px 16px" }}>로그아웃</button>
            </>
          ) : (
            <>
              <button onClick={() => navigate("/login")} style={{ padding: "8px 16px" }}>로그인</button>
              <button onClick={() => navigate("/register")} style={{ padding: "8px 16px" }}>회원가입</button>
            </>
          )}
        </div>
      </header>

      <div style={{ width: "100%", background: "#e9ecef", padding: "12px 0", textAlign: "center", fontSize: 16, color: "#555" }}>
        GDGoC 스터디 모집글을 확인하고, 원하는 스터디에 지원하세요!
      </div>

      <main style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "flex-start", background: "#fff", paddingTop: 32 }}>
        <section style={{ width: "100%", maxWidth: 900, margin: "0 auto 40px auto", background: "#fff", borderRadius: 8, boxShadow: "0 2px 8px rgba(0,0,0,0.04)", padding: 24 }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 16 }}>
          <h3 style={{ margin: 0, fontSize: 22 }}>
            스터디 모집 게시판
          </h3>

          <button
            onClick={() => {
              if (!user) {
                alert("로그인 후 작성할 수 있습니다.");
                navigate("/login");
                return;
              }
              setCreateOpen(true);
            }}
            style={{
              padding: "10px 16px",
              background: "#1976d2",
              color: "#fff",
              border: "none",
              borderRadius: 8,
              cursor: "pointer",
              fontWeight: "bold",
            }}
          >
            모집글 작성
          </button>
        </div>
          
          {/* 스터디 카드 목록 */}
          <div style={{ display: "flex", gap: 24, justifyContent: "center",flexWrap:"wrap" }}>
        
            {studies.length === 0? (
              <div style={{textAlign:"center", color:"#888", padding:20}}>
                아직 모집글이 없습니다
              </div>  
            ): studies.map(study => (
              <div
                key={study.id}
                style={{
                  background: "#f4f6fa",
                  borderRadius: 10,
                  boxShadow: "0 1px 4px rgba(0,0,0,0.06)",
                  padding: 24,
                  minWidth: 260,
                  cursor: "pointer",
                  transition: "box-shadow 0.2s",
                  border: "2px solid #e3e7ed"
                }}

                onClick={() => {
                  if (!user) {
                    alert("상세보기 및 가입은 로그인 후 가능합니다.");
                    navigate("/login");
                  }
                  
                  else {
                    setModalStudyId(study.id);
                  }
                }}
              >
                <div style={{ fontWeight: "bold", fontSize: 18, marginBottom: 8 }}>{study.title}</div>
                <div style={{ color: "#666", fontSize: 14, marginBottom: 6 }}>{(study.description ?? "").slice(0, 40)}...</div>
                <div style={{ fontSize: 13, marginBottom: 4 }}><b>리더:</b> {study.leader}</div>
                <div style={{ fontSize: 13, marginBottom: 4 }}><b>인원:</b> {study.members} / {study.maxMembers}</div>
                <div style={{ fontSize: 13, marginBottom: 4 }}><b>모집 기간:</b> {study.date}</div>
                <div style={{ fontSize: 13, color: study.status === "모집중" ? "#2b8a3e" : "#888" }}><b>{study.status}</b></div>
              </div>
            ))}
          </div>

          {/* 상세 모달 */}
          {modalStudy && (
            <div style={{
              position: "fixed",
              top: 0,
              left: 0,
              width: "100vw",
              height: "100vh",
              background: "rgba(0,0,0,0.3)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              zIndex: 1000
            }}
              onClick={() => setModalStudyId(null)}
            >

              <div style={{ background: "#fff", borderRadius: 12, padding: 32, minWidth: 340, boxShadow: "0 2px 12px rgba(0,0,0,0.12)" }} onClick={e => e.stopPropagation()}>
                <h2 style={{ marginBottom: 12 }}>{modalStudy.title}</h2>
                <p style={{ color: "#555", marginBottom: 16 }}>{modalStudy.description}</p>

                <div style={{ fontSize: 14, marginBottom: 6 }}><b>리더:</b> {modalStudy.leader}</div>
                <div style={{ fontSize: 14, marginBottom: 6 }}><b>인원:</b> {modalStudy.members} / {modalStudy.maxMembers}</div>
                <div style={{ fontSize: 14, marginBottom: 6 }}><b>기간:</b> {modalStudy.date}</div>
                <div style={{ fontSize: 14, color: modalStudy.status === "모집중" ? "#2b8a3e" : "#888", marginBottom: 16 }}><b>{modalStudy.status}</b></div>
                <div style={{ display: "flex", gap: 12 }}>
                  
                  <button onClick={handleJoinStudy} style={{ padding: "8px 20px", background: "#1976d2", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>
                    스터디 가입
                  </button>
                  <button onClick={() => setModalStudyId(null)} style={{ padding: "8px 20px", background: "#2b8a3e", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>닫기</button>
                </div>
              </div>
            </div>
          )}

          {/* 글 작성 모달 */}
          {createOpen && (
            <div
              style={{
                position: "fixed",
                top: 0,
                left: 0,
                width: "100vw",
                height: "100vh",
                background: "rgba(0,0,0,0.3)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                zIndex: 1000,
              }}
              onClick={() => setCreateOpen(false)}
            >
              <div
                style={{
                  background: "#fff",
                  borderRadius: 12,
                  padding: 32,
                  width: 420,
                  boxShadow: "0 2px 12px rgba(0,0,0,0.12)",
                  maxHeight:"80vh",
                  overflowY:"auto",
                }}
                onClick={(e) => e.stopPropagation()}
              >
                <h2 style={{ marginBottom: 16 }}>모집글 작성</h2>

                {/* 제목 */}
                <div style={{ marginBottom: 12 }}>
                  <div style={{ fontSize: 14, fontWeight: "bold", marginBottom: 6 }}>
                    스터디 제목
                  </div>
                  <input
                    value={form.title}
                    onChange={(e) => setForm({ ...form, title: e.target.value })}
                    placeholder="예) Spring Boot 스터디"
                    style={{
                      width: "100%",
                      padding: 10,
                      borderRadius: 8,
                      border: "1px solid #ddd",
                      fontSize: 14,
                    }}
                  />
                </div>

                {/* 설명 */}
                <div style={{ marginBottom: 12 }}>
                  <div style={{ fontSize: 14, fontWeight: "bold", marginBottom: 6 }}>
                    스터디 설명
                  </div>
                  <textarea
                    value={form.description}
                    onChange={(e) => setForm({ ...form, description: e.target.value })}
                    placeholder="스터디 소개를 적어주세요!"
                    rows={5}
                    style={{
                      width: "100%",
                      padding: 10,
                      borderRadius: 8,
                      border: "1px solid #ddd",
                      fontSize: 14,
                      resize: "none",
                    }}
                  />
                </div>

                {/* 최대 인원 */}
                <div style={{ marginBottom: 12 }}>
                  <div style={{ fontSize: 14, fontWeight: "bold", marginBottom: 6 }}>
                    최대 인원
                  </div>
                  <input
                    type="number"
                    min={1}
                    value={form.maxMembers}
                    onChange={(e) => setForm({ ...form, maxMembers: Number(e.target.value) })}
                    placeholder="예) 8"
                    style={{
                      width: "100%",
                      padding: 10,
                      borderRadius: 8,
                      border: "1px solid #ddd",
                      fontSize: 14,
                    }}
                  />
                </div>

                {/* 모집 시작일 */}
                <div style={{ marginBottom: 12 }}>
                  <div style={{ fontSize: 14, fontWeight: "bold", marginBottom: 6 }}>
                    모집 시작일
                  </div>
                  <input
                    type="date"
                    value={form.recruitmentStartDate}
                    onChange={(e) =>
                      setForm({ ...form, recruitmentStartDate: e.target.value })
                    }
                    style={{
                      width: "100%",
                      padding: 10,
                      borderRadius: 8,
                      border: "1px solid #ddd",
                      fontSize: 14,
                    }}
                  />
                </div>

                {/* 모짐 종료일 */}
                <div style={{ marginBottom: 16 }}>
                  <div style={{ fontSize: 14, fontWeight: "bold", marginBottom: 6 }}>
                    모집 종료일
                  </div>
                  <input
                    type="date"
                    value={form.recruitmentEndDate}
                    onChange={(e) =>
                      setForm({ ...form, recruitmentEndDate: e.target.value })
                    }
                    style={{
                      width: "100%",
                      padding: 10,
                      borderRadius: 8,
                      border: "1px solid #ddd",
                      fontSize: 14,
                    }}
                  />
                </div>


                <div style={{display: "flex",gap: 12}}>
                  <button
                    onClick={handleCreateStudy}
                    style={{
                      padding: "8px 20px",
                      background: "#1976d2",
                      color: "#fff",
                      border: "none",
                      borderRadius: 6,
                      cursor: "pointer",
                    }}
                  >
                    등록
                  </button>

                 <button
                    onClick={() => {
                      setCreateOpen(false);
                      setForm({
                        title: "",
                        description: "",
                        maxMembers: 6,
                        recruitmentStartDate: "",
                        recruitmentEndDate: "",
                      });
                    }}
                    style={{
                      padding: "8px 20px",
                      background: "#2b8a3e",
                      color: "#fff",
                      border: "none",
                      borderRadius: 6,
                      cursor: "pointer",
                    }}
                  >
                    닫기
                  </button>

                </div>
              </div>
            </div>
          )}

        </section>
      </main>
    </div>
  );
}

export default MainPage;
