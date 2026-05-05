import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { trackApi } from '../api/api';
import './Upload.css';

const GENRES = ['팝', '록', '힙합', '재즈', '클래식', '일렉트로닉', 'R&B', '인디', '기타'];

export default function Upload() {
  const [form, setForm] = useState({ title: '', description: '', genre: '' });
  const [audioFile, setAudioFile] = useState(null);
  const [coverFile, setCoverFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleCoverChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setCoverFile(file);
      setPreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!audioFile) { setError('음악 파일을 선택해주세요.'); return; }
    if (!form.title.trim()) { setError('제목을 입력해주세요.'); return; }

    setLoading(true);
    setError('');
    try {
      const formData = new FormData();
      formData.append('audio', audioFile);
      if (coverFile) formData.append('cover', coverFile);
      formData.append('title', form.title);
      formData.append('description', form.description);
      formData.append('genre', form.genre);

      const res = await trackApi.upload(formData);
      navigate(`/track/${res.data.id}`);
    } catch (err) {
      setError(err.response?.data?.message || '업로드 실패');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-container">
      <h2>🎵 트랙 업로드</h2>
      {error && <div className="upload-error">{error}</div>}

      <form onSubmit={handleSubmit} className="upload-form">
        {/* 커버 이미지 */}
        <div className="cover-upload">
          <label htmlFor="cover-input">
            {preview ? (
              <img src={preview} alt="cover" className="cover-preview" />
            ) : (
              <div className="cover-placeholder">
                <span>🖼️</span>
                <p>커버 이미지 선택</p>
              </div>
            )}
          </label>
          <input id="cover-input" type="file" accept="image/*" onChange={handleCoverChange} hidden />
        </div>

        {/* 오디오 파일 */}
        <div className="audio-upload">
          <label className="audio-label">
            <span>🎵</span>
            {audioFile ? audioFile.name : '음악 파일 선택 (MP3, WAV)'}
            <input
              type="file"
              accept="audio/*"
              onChange={e => setAudioFile(e.target.files[0])}
              hidden
            />
          </label>
        </div>

        <input
          placeholder="트랙 제목 *"
          value={form.title}
          onChange={e => setForm({ ...form, title: e.target.value })}
          required
        />

        <textarea
          placeholder="설명을 입력하세요..."
          value={form.description}
          onChange={e => setForm({ ...form, description: e.target.value })}
          rows={3}
        />

        <select
          value={form.genre}
          onChange={e => setForm({ ...form, genre: e.target.value })}
        >
          <option value="">장르 선택</option>
          {GENRES.map(g => <option key={g} value={g}>{g}</option>)}
        </select>

        <button type="submit" disabled={loading}>
          {loading ? '업로드 중...' : '업로드'}
        </button>
      </form>
    </div>
  );
}
