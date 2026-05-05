import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// 요청마다 JWT 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const authApi = {
  signup: (data) => api.post('/auth/signup', data),
  login:  (data) => api.post('/auth/login', data),
};

export const trackApi = {
  getFeed:      (params) => api.get('/tracks/feed', { params }),
  getPopular:   ()       => api.get('/tracks/popular'),
  getTrack:     (id)     => api.get(`/tracks/${id}`),
  getUserTracks:(userId) => api.get(`/tracks/user/${userId}`),
  upload:       (form)   => api.post('/tracks', form, { headers: { 'Content-Type': 'multipart/form-data' } }),
  toggleLike:   (id)     => api.post(`/tracks/${id}/like`),
  delete:       (id)     => api.delete(`/tracks/${id}`),
};

export const userApi = {
  getProfile: (id)   => api.get(`/users/${id}`),
  updateProfile:(data)=> api.put('/users/me', data),
  follow:     (id)   => api.post(`/users/${id}/follow`),
};

export const commentApi = {
  getComments: (trackId)       => api.get(`/comments/track/${trackId}`),
  addComment:  (trackId, data) => api.post(`/comments/track/${trackId}`, data),
  delete:      (id)            => api.delete(`/comments/${id}`),
};

export default api;
