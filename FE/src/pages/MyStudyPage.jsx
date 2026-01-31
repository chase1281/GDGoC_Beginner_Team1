import React, { useEffect, useState } from "react";
import { apiFetch } from "../api";

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
  status: b.status === "RECRUITING" ? "모집중" : "모집완료"
});

const MyStudyPage = () => {
  const [studies, setStudies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBoards = async () => {
      try {
        const data = await apiFetch("/boards/recruiting");
        const mapped = Array.isArray(data) ? data.map(mapBoardToStudy) : [];
        setStudies(mapped);
      } catch (e) {
        setStudies([]);
      } finally {
        setLoading(false);
      }
    };
    fetchBoards();
  }, []);

  return (
    <div style={{ padding: 32 }}>
      <h2>내 스터디</h2>
      {loading ? (
        <p>불러오는 중...</p>
      ) : studies.length === 0 ? (
        <p>모집 중인 스터디가 없습니다.</p>
      ) : (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(2, minmax(0, 1fr))", gap: 24, marginTop: 24 }}>
          {studies.map((study) => (
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
                border: "2px solid #e3e7ed",
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
      )}
    </div>
  );
};

export default MyStudyPage;
