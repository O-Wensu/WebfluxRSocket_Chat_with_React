import React from 'react';
import { Link } from 'react-router-dom';

function Home() {
  return (
    <div>
      <h1>홈 페이지</h1>
      {/* 로그인 버튼을 Link 컴포넌트로 감싸줍니다. */}
      {/* <Link to="/login">
        <button>로그인</button>
      </Link> */}

      <Link to="/chat">
        <button>채팅하러가입시다 (。・∀・)ノ</button>
      </Link>
    </div>
  );
}

export default Home;
