import React, { createContext } from 'react';
import { useNavigate } from 'react-router-dom';

export const AuthContext = createContext(undefined);

export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();

  // Hardcode user as ADMIN since authentication has been removed
  const user = { username: 'admin', role: 'ROLE_ADMIN' };

  const login = async () => {
    navigate('/');
  };

  const logout = () => {
    navigate('/');
  };

  return (
    <AuthContext.Provider 
      value={{ 
        user, 
        isAuthenticated: true, 
        isLoading: false, 
        login, 
        logout,
        isAdmin: true
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
