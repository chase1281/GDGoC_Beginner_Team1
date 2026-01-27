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
  const [studies, setStudies] = useState([]);

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

  // 모집글 목록 불러오기 (백엔드 연동 시 사용)
  useEffect(() => {
    const fetchBoards = async () => {
      try {
        const data = await apiFetch("/boards/recruiting");
        const mapped = Array.isArray(data) ? data.map(mapBoardToStudy) : [];
        // 서버에서 받아온 결과를 반영합니다. 빈 배열이면 화면을 비워둡니다.
        setStudies(mapped);
      } catch (e) {
        console.error("fetchBoards error:", e);
        // 오류 발생 시 빈 목록을 유지합니다.
        setStudies([]);
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


  // 필터 상태: 전체/모집중/모집완료
  const [filter, setFilter] = useState("전체");

  // 필터링된 스터디 목록
  const filteredStudies =
    filter === "전체"
      ? studies
      : studies.filter((s) => s.status === filter);

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

      // 새로 작성한 글을 로컬 상태에 즉시 추가합니다.
      const tempId = `local-${Date.now()}`;
      const localStudy = {
        id: tempId,
        title: form.title,
        description: form.description,
        leader: user?.name ?? "알 수 없음",
        members: 1,
        maxMembers: Number(form.maxMembers),
        date: `${form.recruitmentStartDate} ~ ${form.recruitmentEndDate}`,
        status: "모집중",
      };
      setStudies((prev) => [localStudy, ...(Array.isArray(prev) ? prev : [])]);

      // 서버에서 최신 목록을 받아오면 화면을 갱신합니다. 서버가 빈 배열이면 로컬 상태를 유지합니다.
      try {
        const latest = await apiFetch("/boards/recruiting");
        const mappedLatest = Array.isArray(latest) ? latest.map(mapBoardToStudy) : [];
        // 성공적으로 가져온 경우 서버 상태와 항상 동기화합니다.
        setStudies(mappedLatest);
      } catch (e) {
        console.error("Failed to refresh studies after create:", e);
        // 네트워크 실패 시 추가한 로컬 항목은 유지합니다.
      }

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

          {/* 필터 탭 */}
          <div style={{ display: "flex", gap: 0, marginBottom: 24, borderBottom: "2px solid #e3e7ed", width: 360 }}>
            {["전체", "모집중", "모집완료"].map((tab) => (
              <button
                key={tab}
                onClick={() => setFilter(tab)}
                style={{
                  flex: 1,
                  padding: "10px 0",
                  background: filter === tab ? "#fff" : "#f4f6fa",
                  border: "none",
                  borderBottom: filter === tab ? "2.5px solid #1976d2" : "2.5px solid transparent",
                  color: filter === tab ? "#1976d2" : "#888",
                  fontWeight: filter === tab ? "bold" : "normal",
                  fontSize: 16,
                  cursor: "pointer",
                  outline: "none",
                  transition: "all 0.15s",
                }}
              >
                {tab}
              </button>
            ))}
          </div>

          {/* 스터디 카드 목록 */}
          <div style={{ display: "grid", gridTemplateColumns: "repeat(2, minmax(0, 1fr))", gap: 24, justifyContent: "center", width: "100%" }}>
            {filteredStudies.length === 0 ? (
              <div style={{
                gridColumn: "1 / -1",
                minHeight: 180,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                color: "#888",
                fontSize: 16,
              }}>
                아직 모집글이 없습니다
              </div>
            ) : filteredStudies.map((study) => (
              <div
                key={study.id}
                style={{
                  background: "#f4f6fa",
                  borderRadius: 10,
                  boxShadow: "0 1px 4px rgba(0,0,0,0.06)",
                  padding: 24,
                  height: 220,
                  boxSizing: "border-box",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                  cursor: "pointer",
                  transition: "box-shadow 0.2s",
                  border: "2px solid #e3e7ed",
                }}
                onClick={() => {
                  if (!user) {
                    alert("상세보기 및 가입은 로그인 후 가능합니다.");
                    navigate("/login");
                  } else {
                    setModalStudyId(study.id);
                  }
                }}
              >
                <div style={{ fontWeight: "bold", fontSize: 18, marginBottom: 8 }}>{study.title}</div>
                <div style={{ color: "#666", fontSize: 14, marginBottom: 6, overflow: "hidden", textOverflow: "ellipsis", display: "-webkit-box", WebkitLineClamp: 2, WebkitBoxOrient: "vertical" }}>{study.description ?? ""}</div>
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

                {/* 모집 종료일 */}
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
