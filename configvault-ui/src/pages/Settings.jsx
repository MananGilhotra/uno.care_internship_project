import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { Moon, Sun, Monitor, Globe, Bell, ShieldCheck, Palette } from 'lucide-react';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';

export const Settings = () => {
  const { user, isAdmin } = useAuth();
  const [theme, setTheme] = useState('system');

  useEffect(() => {
    const stored = localStorage.theme;
    if (stored === 'dark') setTheme('dark');
    else if (stored === 'light') setTheme('light');
    else setTheme('system');
  }, []);

  const applyTheme = (mode) => {
    setTheme(mode);
    if (mode === 'dark') {
      document.documentElement.classList.add('dark');
      localStorage.theme = 'dark';
    } else if (mode === 'light') {
      document.documentElement.classList.remove('dark');
      localStorage.theme = 'light';
    } else {
      localStorage.removeItem('theme');
      if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    }
    toast.success(`Theme set to ${mode}`);
  };

  const themeOptions = [
    { value: 'light', icon: Sun, label: 'Light' },
    { value: 'dark', icon: Moon, label: 'Dark' },
    { value: 'system', icon: Monitor, label: 'System' },
  ];

  const container = { hidden: { opacity: 0 }, show: { opacity: 1, transition: { staggerChildren: 0.08 } } };
  const item = { hidden: { opacity: 0, y: 12 }, show: { opacity: 1, y: 0 } };

  return (
    <motion.div variants={container} initial="hidden" animate="show" className="max-w-2xl mx-auto space-y-6">
      <motion.div variants={item}>
        <h1 className="text-xl font-semibold tracking-tight">Settings</h1>
        <p className="text-[13px] text-[var(--text-muted)] mt-0.5">Manage your preferences</p>
      </motion.div>

      {/* Profile */}
      <motion.div variants={item} className="card p-5">
        <div className="flex items-center gap-3 mb-4">
          <ShieldCheck size={16} className="text-[var(--text-muted)]" />
          <h3 className="text-sm font-semibold">Account</h3>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-[12px] font-medium text-[var(--text-muted)] uppercase tracking-wide">Username</label>
            <p className="text-sm font-medium mt-1">{user?.username}</p>
          </div>
          <div>
            <label className="text-[12px] font-medium text-[var(--text-muted)] uppercase tracking-wide">Role</label>
            <p className="text-sm font-medium mt-1">
              <span className={isAdmin ? 'badge-blue' : 'badge-gray'}>{user?.role.replace('ROLE_', '')}</span>
            </p>
          </div>
        </div>
      </motion.div>

      {/* Appearance */}
      <motion.div variants={item} className="card p-5">
        <div className="flex items-center gap-3 mb-4">
          <Palette size={16} className="text-[var(--text-muted)]" />
          <h3 className="text-sm font-semibold">Appearance</h3>
        </div>
        <p className="text-[13px] text-[var(--text-muted)] mb-4">Choose how ConfigVault looks to you</p>
        <div className="grid grid-cols-3 gap-3">
          {themeOptions.map(opt => (
            <button
              key={opt.value}
              onClick={() => applyTheme(opt.value)}
              className={`flex flex-col items-center gap-2 p-4 rounded-xl border-2 transition-all ${
                theme === opt.value
                  ? 'border-[var(--accent)] bg-[var(--accent-muted)]'
                  : 'border-transparent bg-[var(--bg-tertiary)] hover:border-zinc-300 dark:hover:border-zinc-600'
              }`}
            >
              <opt.icon size={20} className={theme === opt.value ? 'text-[var(--accent)]' : 'text-[var(--text-muted)]'} />
              <span className={`text-[12px] font-medium ${theme === opt.value ? 'text-[var(--accent)]' : 'text-[var(--text-secondary)]'}`}>
                {opt.label}
              </span>
            </button>
          ))}
        </div>
      </motion.div>

      {/* API */}
      <motion.div variants={item} className="card p-5">
        <div className="flex items-center gap-3 mb-4">
          <Globe size={16} className="text-[var(--text-muted)]" />
          <h3 className="text-sm font-semibold">API Configuration</h3>
        </div>
        <div className="space-y-3">
          <div>
            <label className="text-[12px] font-medium text-[var(--text-muted)] uppercase tracking-wide">Base URL</label>
            <div className="mt-1 font-mono text-[13px] bg-[var(--bg-tertiary)] rounded-lg px-3 py-2 border">
              http://localhost:8080/api/v1
            </div>
          </div>
          <div>
            <label className="text-[12px] font-medium text-[var(--text-muted)] uppercase tracking-wide">Status</label>
            <div className="flex items-center gap-2 mt-1">
              <span className="w-2 h-2 rounded-full bg-emerald-500" />
              <span className="text-[13px] font-medium text-emerald-600 dark:text-emerald-400">Connected</span>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Notifications */}
      <motion.div variants={item} className="card p-5">
        <div className="flex items-center gap-3 mb-4">
          <Bell size={16} className="text-[var(--text-muted)]" />
          <h3 className="text-sm font-semibold">Notifications</h3>
        </div>
        <div className="space-y-3">
          {['Property changes', 'Security alerts', 'System updates'].map((label) => (
            <label key={label} className="flex items-center justify-between cursor-pointer group">
              <span className="text-[13px] text-[var(--text-secondary)] group-hover:text-[var(--text-primary)] transition-colors">{label}</span>
              <div className="relative">
                <input type="checkbox" defaultChecked className="sr-only peer" />
                <div className="w-9 h-5 bg-zinc-300 dark:bg-zinc-700 rounded-full peer-checked:bg-[var(--accent)] transition-colors" />
                <div className="absolute left-0.5 top-0.5 w-4 h-4 bg-white rounded-full shadow peer-checked:translate-x-4 transition-transform" />
              </div>
            </label>
          ))}
        </div>
      </motion.div>
    </motion.div>
  );
};
