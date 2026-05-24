import React from 'react';
import { Clock, User, Settings, ShieldAlert, ArrowUpRight } from 'lucide-react';
import { motion } from 'framer-motion';

const logs = [
  { id: 1, action: 'PROPERTY_CREATED', user: 'admin', details: 'Created APP_THEME property', time: '10 min ago', type: 'create' },
  { id: 2, action: 'PROPERTY_UPDATED', user: 'admin', details: 'Updated LOG_LEVEL → DEBUG', time: '1 hour ago', type: 'update' },
  { id: 3, action: 'LOGIN_SUCCESS', user: 'viewer', details: 'Login from 192.168.1.5', time: '2 hours ago', type: 'auth' },
  { id: 4, action: 'PROPERTY_DELETED', user: 'admin', details: 'Soft-deleted MAX_USERS', time: '5 hours ago', type: 'delete' },
  { id: 5, action: 'ACCESS_DENIED', user: 'viewer', details: 'Attempted modify on JWT_SECRET', time: '1 day ago', type: 'security' },
  { id: 6, action: 'PROPERTY_CREATED', user: 'admin', details: 'Created REDIS_HOST property', time: '1 day ago', type: 'create' },
  { id: 7, action: 'LOGIN_SUCCESS', user: 'admin', details: 'Login from 10.0.0.1', time: '2 days ago', type: 'auth' },
];

const typeConfig = {
  create: { color: 'text-emerald-600 dark:text-emerald-400', bg: 'bg-emerald-50 dark:bg-emerald-950', icon: ArrowUpRight },
  update: { color: 'text-blue-600 dark:text-blue-400', bg: 'bg-blue-50 dark:bg-blue-950', icon: Settings },
  delete: { color: 'text-red-600 dark:text-red-400', bg: 'bg-red-50 dark:bg-red-950', icon: Settings },
  auth: { color: 'text-purple-600 dark:text-purple-400', bg: 'bg-purple-50 dark:bg-purple-950', icon: User },
  security: { color: 'text-amber-600 dark:text-amber-400', bg: 'bg-amber-50 dark:bg-amber-950', icon: ShieldAlert },
};

const container = { hidden: { opacity: 0 }, show: { opacity: 1, transition: { staggerChildren: 0.06 } } };
const item = { hidden: { opacity: 0, x: -10 }, show: { opacity: 1, x: 0 } };

export const ActivityLogs = () => {
  return (
    <motion.div variants={container} initial="hidden" animate="show" className="max-w-3xl mx-auto space-y-5">
      <motion.div variants={item}>
        <h1 className="text-xl font-semibold tracking-tight">Activity Log</h1>
        <p className="text-[13px] text-[var(--text-muted)] mt-0.5">System-wide event tracking (mock data)</p>
      </motion.div>

      <div className="card overflow-hidden">
        {logs.map((log) => {
          const cfg = typeConfig[log.type] || typeConfig.update;
          const Icon = cfg.icon;
          return (
            <motion.div key={log.id} variants={item} className="flex items-start gap-3 px-5 py-4 border-b last:border-0 hover:bg-[var(--bg-tertiary)]/30 transition-colors">
              <div className={`p-1.5 rounded-lg shrink-0 mt-0.5 ${cfg.bg}`}>
                <Icon size={14} className={cfg.color} />
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <span className="text-[13px] font-semibold">{log.action}</span>
                  <span className={`badge ${log.type === 'security' ? 'badge-red' : log.type === 'create' ? 'badge-green' : log.type === 'delete' ? 'badge-red' : 'badge-gray'}`}>{log.type}</span>
                </div>
                <p className="text-[13px] text-[var(--text-secondary)] mt-0.5">{log.details}</p>
                <div className="flex items-center gap-3 mt-1.5 text-[11px] text-[var(--text-muted)]">
                  <span className="flex items-center gap-1"><User size={10} /> @{log.user}</span>
                  <span className="flex items-center gap-1"><Clock size={10} /> {log.time}</span>
                </div>
              </div>
            </motion.div>
          );
        })}
      </div>
    </motion.div>
  );
};
