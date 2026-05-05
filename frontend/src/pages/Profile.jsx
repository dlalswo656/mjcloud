import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { userApi, trackApi } from '../api/api';
import { useAuth } from '../context/AuthContext';
import TrackCard from '../components/TrackCard';
import './Profile.css';

export default function Profile() {
  const { id } = useParams();
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [tracks, setTracks] = useState([]);
  const [followed, setFollowed] = useState(false);

  useEffect(() => {
    fetchProfile();
    fetchTracks();
  }, [id]);

  const fetchProfile = async () => {
    try {
      const res = await userApi.getProfile(id);
      setProfile(res.data);
      setFollowed(res.data.followedByMe);
    } catch (err) { console.error(err); }
  };

  const fetchTracks = async () => {
    try {
      const res = await trackApi.getUserTracks(id);
      setTracks(res.data);
    } catch (err) { console.error(err); }
  };

  const handleFollow = async () => {
    if (!user) return alert('로그인이 필요합니다.');
    try {
      await userApi.follow(id);
      setFollowed(!followed);
      setProfile(prev => ({
        ...prev,
        followerCount: followed ? prev.followerCount - 1 : prev.followerCount + 1
      }));
    } catch (err) { console.error(err); }
  };

  if (!profile) return <div className="loading">로딩 중...</div>;

  const isMe = user?.userId == id;

  return (
    <div className="profile-page">
      {/* 프로필 헤더 */}
      <div className="profile-header">
        <div className="profile-avatar">
          {profile.profileImage ? (
            <img src={`http://localhost:8080${profile.profileImage}`} alt="avatar" />
          ) : (
            <div className="default-avatar">
              {profile.displayName?.[0]?.toUpperCase() || profile.username?.[0]?.toUpperCase()}
            </div>
          )}
        </div>

        <div className="profile-info">
          <h2>{profile.displayName || profile.username}</h2>
          <p className="profile-username">@{profile.username}</p>
          {profile.bio && <p className="profile-bio">{profile.bio}</p>}

          <div className="profile-stats">
            <span><strong>{profile.followerCount}</strong> 팔로워</span>
            <span><strong>{profile.followingCount}</strong> 팔로잉</span>
            <span><strong>{profile.trackCount}</strong> 트랙</span>
          </div>

          {!isMe && (
            <button
              className={followed ? 'btn-following' : 'btn-follow'}
              onClick={handleFollow}
            >
              {followed ? '팔로잉' : '팔로우'}
            </button>
          )}
        </div>
      </div>

      {/* 트랙 목록 */}
      <div className="profile-tracks">
        <h3>트랙 ({tracks.length})</h3>
        {tracks.length === 0 ? (
          <p className="empty">업로드한 트랙이 없습니다.</p>
        ) : (
          <div className="track-list">
            {tracks.map(track => (
              <TrackCard key={track.id} track={track} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
