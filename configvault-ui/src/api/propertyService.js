import axiosInstance from './axiosInstance';

export const propertyService = {
  getProperties: async (page = 0, size = 10, category) => {
    const params = { page, size };
    const url = category ? `/properties/category/${category}` : '/properties';
    const response = await axiosInstance.get(url, { params });
    return response.data.data;
  },

  getPropertyByKey: async (key) => {
    const response = await axiosInstance.get(`/properties/${key}`);
    return response.data.data;
  },

  createProperty: async (data) => {
    const response = await axiosInstance.post('/properties', data);
    return response.data.data;
  },

  updateProperty: async (data) => {
    const response = await axiosInstance.post('/properties', data);
    return response.data.data;
  },

  deleteProperty: async (key) => {
    const response = await axiosInstance.delete(`/properties/${key}`);
    return response.data.data;
  },

  getCategories: async () => {
    const response = await axiosInstance.get('/properties/categories');
    return response.data.data;
  }
};
