import React, { createContext, useContext, useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { apiService, User } from '../services/api';
import { Linking } from 'react-native';

interface AuthContextType {
  currentUser: User | null;
  isLoading: boolean;
  isPremium: boolean;
  login: (email: string, username: string) => Promise<void>;
  signup: (email: string, username: string) => Promise<void>;
  logout: () => Promise<void>;
  checkPremiumStatus: () => Promise<void>;
  subscribe: () => Promise<string | null>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const USER_SESSION_KEY = '@finlit_user_session';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isPremium, setIsPremium] = useState<boolean>(false);

  const checkPremiumStatus = async () => {
    if (!currentUser || !currentUser.id) {
      setIsPremium(false);
      return;
    }
    try {
      const sub = await apiService.getSubscription(currentUser.id);
      if (sub && sub.status === 'PREMIUM') {
        setIsPremium(true);
      } else {
        setIsPremium(false);
      }
    } catch (error) {
      console.warn('Failed to check premium status', error);
      setIsPremium(false);
    }
  };

  const subscribe = async (): Promise<string | null> => {
    if (!currentUser || !currentUser.id) {
      throw new Error('User must be logged in to subscribe.');
    }
    try {
      const result = await apiService.initializeSubscription(currentUser.id);
      if (result && result.authorization_url) {
        await Linking.openURL(result.authorization_url);
        return result.authorization_url;
      }
      return null;
    } catch (error) {
      console.error('Failed to initialize subscription', error);
      throw error;
    }
  };

  useEffect(() => {
    if (currentUser) {
      checkPremiumStatus();
    } else {
      setIsPremium(false);
    }
  }, [currentUser]);

  useEffect(() => {
    const loadUserSession = async () => {
      try {
        const session = await AsyncStorage.getItem(USER_SESSION_KEY);
        if (session) {
          setCurrentUser(JSON.parse(session));
        }
      } catch (error) {
        console.error('Failed to load user session', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadUserSession();
  }, []);

  const login = async (email: string, username: string) => {
    setIsLoading(true);
    try {
      // 1. Sync with the Spring Boot backend
      let user: User | undefined;
      try {
        const users = await apiService.getUsers();
        user = users.find(u => u.username.toLowerCase() === username.toLowerCase() || u.email.toLowerCase() === email.toLowerCase());
      } catch (backendError) {
        console.warn('Backend server not reachable, using local mock session', backendError);
      }

      // If user not found on backend, we will auto-register them for demo purposes
      if (!user) {
        try {
          user = await apiService.createUser(username, email);
        } catch (createError) {
          console.warn('Backend user creation failed, mock user used');
          user = { username, email, id: Date.now() };
        }
      }

      // 2. Save user session locally
      await AsyncStorage.setItem(USER_SESSION_KEY, JSON.stringify(user));
      setCurrentUser(user);
    } catch (error) {
      console.error('Login failed', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const signup = async (email: string, username: string) => {
    setIsLoading(true);
    try {
      // 1. Create user in Spring Boot backend
      let user: User;
      try {
        user = await apiService.createUser(username, email);
      } catch (backendError) {
        console.warn('Backend server not reachable, creating local mock session', backendError);
        user = { username, email, id: Date.now() };
      }

      // 2. Save user session locally
      await AsyncStorage.setItem(USER_SESSION_KEY, JSON.stringify(user));
      setCurrentUser(user);
    } catch (error) {
      console.error('Signup failed', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    setIsLoading(true);
    try {
      await AsyncStorage.removeItem(USER_SESSION_KEY);
      setCurrentUser(null);
    } catch (error) {
      console.error('Logout failed', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthContext.Provider value={{ currentUser, isLoading, login, signup, logout, isPremium, checkPremiumStatus, subscribe }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
