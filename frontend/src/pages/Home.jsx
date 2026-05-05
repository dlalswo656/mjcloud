import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { trackApi } from '../api/api';
import TrackCard from '../components/TrackCard';
import './Home.css';

const GENRES = ['전체', '팝', '록', '힙합', '재즈', '클래식', '일렉트로닉', 'R&B', '인디'];

export default function Home() {
  const [tracks, setTracks] = useState([]);
  const [popular, setPopular] = useState([]);
  const [selectedGenre, setSelectedGenre] = useState('전체');
  const [tab, setTab] = useState('latest'); // latest | popular
  const [loading, setLoading] = useState(false);
  const [searchParams] = useSearchParams();
  const keyword = searchParams.get('keyword');

  useEffect(() => {
    fetchTracks();
  }, [selectedGenre, tab, keyword]);

  const fetchTracks = async () => {
    setLoading(true);
    try {
      if (tab === 'popular') {
        const res = await trackApi.getPopular();
        setTracks(res.data);
      } else {
        const params = {};
        if (selectedGenre !== '전체') params.genre = selectedGenre;
        if (keyword) params.keyword = keyword;
        const res = await trackApi.getFeed(params);
        setTracks(res.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="home">
      {/* 검색 결과 헤더 */}
      {keyword && (
        <div className="search-header">
          <h2>"{keyword}" 검색 결과</h2>
        </div>
      )}

      {/* 탭 */}
      <div className="home-tabs">
        <button
          className={tab === 'latest' ? 'tab active' : 'tab'}
          onClick={() => setTab('latest')}
        >
          최신
        </button>
        <button
          className={tab === 'popular' ? 'tab active' : 'tab'}
          onClick={() => setTab('popular')}
        >
          인기
        </button>
      </div>

      {/* 장르 필터 */}
      {tab === 'latest' && (
        <div className="genre-filter">
          {GENRES.map(g => (
            <button
              key={g}
              className={selectedGenre === g ? 'genre-btn active' : 'genre-btn'}
              onClick={() => setSelectedGenre(g)}
            >
              {g}
            </button>
          ))}
        </div>
      )}

      {/* 트랙 목록 */}
      {loading ? (
        <div className="loading">불러오는 중...</div>
      ) : tracks.length === 0 ? (
        <div className="empty">트랙이 없습니다. 첫 번째로 업로드해보세요! 🎵</div>
      ) : (
        <div className="track-list">
          {tracks.map(track => (
            <TrackCard key={track.id} track={track} />
          ))}
        </div>
      )}
    </div>
  );
}
