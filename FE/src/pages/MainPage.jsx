import React from 'react';
import { Link } from 'react-router-dom';

function MainPage() {
  return (
    <div>
      <h1>메인 페이지</h1>
      {/* TODO: 로그인/회원가입 성공 시, 사용자 정보 받아오기 (BE 연동 필요) */}
      <Link to="/login">
        <button>로그인</button>
      </Link>
      <Link to="/register">
        <button>회원가입</button>
      </Link>
    </div>
  );
}

export default MainPage;
