import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="nav-logo">
        <span className="logo-icon">🎵</span>
        <span className="logo-text">MJ Cloud</span>
      </Link>

      <div className="nav-center">
        <input
          className="nav-search"
          placeholder="트랙, 아티스트 검색..."
          onKeyDown={(e) => {
            if (e.key === 'Enter') navigate(`/?keyword=${e.target.value}`);
          }}
        />
      </div>

      <div className="nav-right">
        {user ? (
          <>
            <Link to="/upload" className="btn-upload">+ 업로드</Link>
            <Link to={`/profile/${user.userId}`} className="nav-username">
              {user.username}
            </Link>
            <button onClick={handleLogout} className="btn-logout">로그아웃</button>
          </>
        ) : (
          <>
            <Link to="/login" className="btn-login">로그인</Link>
            <Link to="/signup" className="btn-signup">회원가입</Link>
          </>
        )}
      </div>
    </nav>
  );
}
