import { useState, useEffect, useRef } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { trackApi, commentApi } from '../api/api';
import { useAuth } from '../context/AuthContext';
import './TrackDetail.css';

export default function TrackDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const audioRef = useRef(null);

  const [track, setTrack] = useState(null);
  const [comments, setComments] = useState([]);
  const [comment, setComment] = useState('');
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [playing, setPlaying] = useState(false);
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    fetchTrack();
    fetchComments();
  }, [id]);

  const fetchTrack = async () => {
    try {
      const res = await trackApi.getTrack(id);
      setTrack(res.data);
      setLiked(res.data.likedByMe);
      setLikeCount(res.data.likeCount);
    } catch (err) { console.error(err); }
  };

  const fetchComments = async () => {
    try {
      const res = await commentApi.getComments(id);
      setComments(res.data);
    } catch (err) { console.error(err); }
  };

  const togglePlay = () => {
    if (!audioRef.current) return;
    if (playing) audioRef.current.pause();
    else audioRef.current.play();
    setPlaying(!playing);
  };

  const handleTimeUpdate = () => {
    if (!audioRef.current) return;
    const pct = (audioRef.current.currentTime / audioRef.current.duration) * 100;
    setProgress(pct || 0);
  };

  const handleSeek = (e) => {
    if (!audioRef.current) return;
    const rect = e.currentTarget.getBoundingClientRect();
    const pct = (e.clientX - rect.left) / rect.width;
    audioRef.current.currentTime = pct * audioRef.current.duration;
  };

  const handleLike = async () => {
    if (!user) return alert('로그인이 필요합니다.');
    const res = await trackApi.toggleLike(id);
    setLiked(res.data.liked);
    setLikeCount(prev => res.data.liked ? prev + 1 : prev - 1);
  };

  const handleComment = async (e) => {
    e.preventDefault();
    if (!user) return alert('로그인이 필요합니다.');
    if (!comment.trim()) return;
    await commentApi.addComment(id, { content: comment });
    setComment('');
    fetchComments();
  };

  const handleDelete = async () => {
    if (!window.confirm('삭제하시겠습니까?')) return;
    await trackApi.delete(id);
    navigate('/');
  };

  if (!track) return <div className="loading">로딩 중...</div>;

  return (
    <div className="track-detail">
      {/* 상단 플레이어 영역 */}
      <div className="player-section">
        <div className="player-cover">
          {track.coverImage ? (
            <img src={`http://localhost:8080${track.coverImage}`} alt="cover" />
          ) : (
            <div className="default-cover-lg">🎵</div>
          )}
        </div>

        <div className="player-info">
          <h1>{track.title}</h1>
          <Link to={`/profile/${track.uploader?.id}`} className="artist-link">
            {track.uploader?.displayName || track.uploader?.username}
          </Link>
          {track.genre && <span className="genre-tag">{track.genre}</span>}

          {/* 오디오 컨트롤 */}
          <audio
            ref={audioRef}
            src={`http://localhost:8080${track.audioUrl}`}
            onTimeUpdate={handleTimeUpdate}
            onEnded={() => setPlaying(false)}
          />

          <div className="player-controls">
            <button className="btn-play-lg" onClick={togglePlay}>
              {playing ? '⏸' : '▶'}
            </button>
            <div className="progress-bar" onClick={handleSeek}>
              <div className="progress-fill" style={{ width: `${progress}%` }} />
            </div>
          </div>

          {/* 액션 */}
          <div className="player-actions">
            <button className={`btn-like-lg ${liked ? 'liked' : ''}`} onClick={handleLike}>
              ♥ {likeCount}
            </button>
            <span className="plays-count">▶ {track.playCount} 재생</span>
            {user?.username === track.uploader?.username && (
              <button onClick={handleDelete} className="btn-delete">삭제</button>
            )}
          </div>

          {track.description && <p className="track-desc">{track.description}</p>}
        </div>
      </div>

      {/* 댓글 */}
      <div className="comments-section">
        <h3>댓글 {comments.length}</h3>

        <form onSubmit={handleComment} className="comment-form">
          <input
            placeholder={user ? '댓글을 입력하세요...' : '로그인 후 댓글을 작성할 수 있습니다.'}
            value={comment}
            onChange={e => setComment(e.target.value)}
            disabled={!user}
          />
          <button type="submit" disabled={!user}>등록</button>
        </form>

        <div className="comment-list">
          {comments.map(c => (
            <div key={c.id} className="comment-item">
              <Link to={`/profile/${c.user?.id}`} className="comment-user">
                {c.user?.displayName || c.user?.username}
              </Link>
              <p>{c.content}</p>
              <span className="comment-date">
                {new Date(c.createdAt).toLocaleDateString('ko-KR')}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
