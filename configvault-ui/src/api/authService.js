import axiosInstance from './axiosInstance';

export const authService = {
  login: async (credentials) => {
    const response = await axiosInstance.post('/auth/login', credentials);
    return response.data.data;
  },
  
  register: async (data) => {
    const response = await axiosInstance.post('/auth/register', data);
    return response.data.data;
  },
  
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }
};
