import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Menu, LogOut, Moon, Sun, Search, X, Settings, ChevronDown } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

export const Navbar = ({ onMenuClick }) => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [showSearch, setShowSearch] = useState(false);
  const [showProfile, setShowProfile] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const profileRef = useRef(null);
  const searchRef = useRef(null);

  useEffect(() => {
    if (localStorage.theme === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      setIsDarkMode(true);
      document.documentElement.classList.add('dark');
    }
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (profileRef.current && !profileRef.current.contains(e.target)) {
        setShowProfile(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Keyboard shortcut: Cmd+K to toggle search
  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        setShowSearch(true);
        setTimeout(() => searchRef.current?.focus(), 100);
      }
      if (e.key === 'Escape') setShowSearch(false);
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, []);

  const toggleDarkMode = () => {
    if (isDarkMode) {
      document.documentElement.classList.remove('dark');
      localStorage.theme = 'light';
    } else {
      document.documentElement.classList.add('dark');
      localStorage.theme = 'dark';
    }
    setIsDarkMode(!isDarkMode);
  };

  return (
    <>
      <header className="h-14 bg-[var(--bg-secondary)]/80 backdrop-blur-md border-b flex items-center justify-between px-4 sm:px-6 sticky top-0 z-20">
        <div className="flex items-center gap-3">
          <button onClick={onMenuClick} className="p-1.5 rounded-md hover:bg-[var(--bg-tertiary)] lg:hidden">
            <Menu size={20} />
          </button>

          {/* Global search trigger */}
          <button
            onClick={() => { setShowSearch(true); setTimeout(() => searchRef.current?.focus(), 100); }}
            className="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-[var(--bg-tertiary)] border rounded-lg text-[13px] text-[var(--text-muted)] hover:text-[var(--text-secondary)] hover:border-zinc-300 dark:hover:border-zinc-600 transition-all w-64"
          >
            <Search size={14} />
            <span>Search properties...</span>
            <kbd className="ml-auto text-[10px] bg-[var(--bg-secondary)] border rounded px-1.5 py-0.5 font-mono">⌘K</kbd>
          </button>
        </div>

        <div className="flex items-center gap-1">
          <button
            onClick={toggleDarkMode}
            className="p-2 text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-[var(--bg-tertiary)] rounded-lg transition-colors"
          >
            {isDarkMode ? <Sun size={18} /> : <Moon size={18} />}
          </button>

          <div className="w-px h-5 bg-[var(--border-color)] mx-1" />

          {/* Profile dropdown */}
          <div className="relative" ref={profileRef}>
            <button
              onClick={() => setShowProfile(!showProfile)}
              className="flex items-center gap-2 px-2 py-1.5 rounded-lg hover:bg-[var(--bg-tertiary)] transition-colors"
            >
              <div className="w-7 h-7 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-xs font-bold">
                {(user?.username?.[0] || 'U').toUpperCase()}
              </div>
              <span className="hidden sm:block text-[13px] font-medium">{user?.username}</span>
              <ChevronDown size={14} className={`hidden sm:block text-[var(--text-muted)] transition-transform ${showProfile ? 'rotate-180' : ''}`} />
            </button>

            {showProfile && (
              <div className="absolute right-0 mt-1.5 w-56 bg-[var(--bg-secondary)] border rounded-xl shadow-lg py-1.5 animate-fade-in z-50">
                <div className="px-3 py-2 border-b mb-1">
                  <p className="text-[13px] font-medium">{user?.username}</p>
                  <p className="text-[11px] text-[var(--text-muted)]">{isAdmin ? 'Administrator' : 'Viewer'}</p>
                </div>
                <button onClick={() => { navigate('/settings'); setShowProfile(false); }} className="w-full flex items-center gap-2 px-3 py-2 text-[13px] text-[var(--text-secondary)] hover:bg-[var(--bg-tertiary)] transition-colors">
                  <Settings size={14} /> Settings
                </button>
                <div className="border-t mt-1 pt-1">
                  <button onClick={logout} className="w-full flex items-center gap-2 px-3 py-2 text-[13px] text-red-500 hover:bg-red-50 dark:hover:bg-red-950/30 transition-colors">
                    <LogOut size={14} /> Sign out
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Command-palette style search overlay */}
      {showSearch && (
        <div className="fixed inset-0 z-50 flex items-start justify-center pt-[15vh]" onClick={() => setShowSearch(false)}>
          <div className="fixed inset-0 bg-black/50 backdrop-blur-sm" />
          <div
            className="relative w-full max-w-lg bg-[var(--bg-secondary)] border rounded-xl shadow-2xl overflow-hidden animate-slide-up"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center gap-3 px-4 border-b">
              <Search size={18} className="text-[var(--text-muted)] shrink-0" />
              <input
                ref={searchRef}
                type="text"
                className="w-full py-3.5 bg-transparent text-sm outline-none placeholder:text-[var(--text-muted)]"
                placeholder="Search properties, categories, keys..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && searchQuery.trim()) {
                    navigate(`/properties?search=${encodeURIComponent(searchQuery)}`);
                    setShowSearch(false);
                    setSearchQuery('');
                  }
                }}
              />
              <button onClick={() => setShowSearch(false)} className="p-1 rounded hover:bg-[var(--bg-tertiary)]">
                <X size={16} className="text-[var(--text-muted)]" />
              </button>
            </div>
            <div className="p-3 text-[12px] text-[var(--text-muted)]">
              Press <kbd className="px-1.5 py-0.5 bg-[var(--bg-tertiary)] border rounded text-[10px] font-mono mx-0.5">Enter</kbd> to search · <kbd className="px-1.5 py-0.5 bg-[var(--bg-tertiary)] border rounded text-[10px] font-mono mx-0.5">Esc</kbd> to close
            </div>
          </div>
        </div>
      )}
    </>
  );
};
