import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { List, ShieldAlert, Activity, Settings, Shield, ChevronRight } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

const navItems = [
  { to: '/dashboard', icon: Activity, label: 'Dashboard' },
  { to: '/properties', icon: List, label: 'Properties' },
  { to: '/restricted-keys', icon: ShieldAlert, label: 'Restricted Keys' },
  { to: '/activity', icon: Activity, label: 'Activity' },
  { to: '/settings', icon: Settings, label: 'Settings' },
];

export const Sidebar = () => {
  const { user, isAdmin } = useAuth();
  const location = useLocation();

  return (
    <aside className="w-[260px] hidden lg:flex flex-col h-screen bg-[var(--bg-secondary)] border-r sticky top-0 z-10">
      {/* Brand */}
      <div className="h-14 flex items-center px-5 border-b shrink-0">
        <div className="w-7 h-7 bg-[var(--accent)] rounded-lg flex items-center justify-center mr-2.5">
          <Shield className="w-4 h-4 text-white" />
        </div>
        <span className="text-[15px] font-semibold tracking-tight">ConfigVault</span>
        <span className="ml-auto badge-blue text-[10px] px-1.5">v1.0</span>
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto py-4 px-3 space-y-0.5">
        <p className="text-[11px] font-semibold text-[var(--text-muted)] uppercase tracking-widest mb-3 px-2">
          Navigation
        </p>
        {navItems.map((item) => {
          const isActive = location.pathname.startsWith(item.to);
          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={`flex items-center gap-2.5 px-2.5 py-[7px] rounded-lg text-[13px] font-medium transition-all duration-150 group ${
                isActive
                  ? 'bg-[var(--accent-muted)] text-[var(--accent)] dark:text-blue-300'
                  : 'text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-[var(--bg-tertiary)]'
              }`}
            >
              <item.icon size={16} strokeWidth={isActive ? 2.2 : 1.8} />
              <span>{item.label}</span>
              {isActive && <ChevronRight size={14} className="ml-auto opacity-50" />}
            </NavLink>
          );
        })}
      </nav>

      {/* User card */}
      <div className="p-3 border-t shrink-0">
        <div className="flex items-center gap-3 px-2 py-2 rounded-lg hover:bg-[var(--bg-tertiary)] transition-colors cursor-pointer">
          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-xs font-bold shrink-0">
            {(user?.username?.[0] || 'U').toUpperCase()}
          </div>
          <div className="min-w-0">
            <p className="text-[13px] font-medium truncate">{user?.username}</p>
            <p className="text-[11px] text-[var(--text-muted)]">
              {isAdmin ? 'Administrator' : 'Viewer'}
            </p>
          </div>
        </div>
      </div>
    </aside>
  );
};
