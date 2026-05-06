import { useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import { trackApi } from '../api/api';
import { useAuth } from '../context/AuthContext';
import './TrackCard.css';

export default function TrackCard({ track, onLikeChange }) {
  const { user } = useAuth();
  const [liked, setLiked] = useState(track.likedByMe);
  const [likeCount, setLikeCount] = useState(track.likeCount);
  const [playing, setPlaying] = useState(false);
  const audioRef = useRef(null);

  const togglePlay = () => {
    if (!audioRef.current) return;
    if (playing) {
      audioRef.current.pause();
    } else {
      audioRef.current.play();
    }
    setPlaying(!playing);
  };

  const handleLike = async (e) => {
    e.preventDefault();
    if (!user) return alert('로그인이 필요합니다.');
    try {
      const res = await trackApi.toggleLike(track.id);
      setLiked(res.data.liked);
      setLikeCount(prev => res.data.liked ? prev + 1 : prev - 1);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="track-card">
      {/* 커버 이미지 + 재생 버튼 */}
      <div className="track-cover" onClick={togglePlay}>
        {track.coverImage ? (
          <img src={`http://localhost:8080${track.coverImage}`} alt="cover" />
        ) : (
          <div className="default-cover">🎵</div>
        )}
        <div className={`play-overlay ${playing ? 'playing' : ''}`}>
          {playing ? '⏸' : '▶'}
        </div>
      </div>

      {/* 오디오/영상 엘리먼트 */}
      {track.audioUrl?.endsWith('.mp4') ? (
        <video
          ref={audioRef}
          src={`http://localhost:8080${track.audioUrl}`}
          onEnded={() => setPlaying(false)}
          style={{ display: 'none' }}
        />
      ) : (
        <audio
          ref={audioRef}
          src={`http://localhost:8080${track.audioUrl}`}
          onEnded={() => setPlaying(false)}
        />
      )}

      {/* 트랙 정보 */}
      <div className="track-info">
        <Link to={`/track/${track.id}`} className="track-title">
          {track.title}
        </Link>
        <Link to={`/profile/${track.uploader?.id}`} className="track-artist">
          {track.uploader?.displayName || track.uploader?.username}
        </Link>
        {track.genre && <span className="track-genre">{track.genre}</span>}
      </div>

      {/* 액션 버튼 */}
      <div className="track-actions">
        <span className="track-plays">▶ {track.playCount || 0}</span>
        <button
          className={`btn-like ${liked ? 'liked' : ''}`}
          onClick={handleLike}
        >
          ♥ {likeCount}
        </button>
        <span className="track-comments">💬 {track.commentCount || 0}</span>
      </div>
    </div>
  );
}
