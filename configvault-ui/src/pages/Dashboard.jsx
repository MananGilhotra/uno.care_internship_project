import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { propertyService } from '../api/propertyService';
import { motion } from 'framer-motion';
import { Activity, ShieldAlert, List, Zap, ArrowUpRight, Server, Search, Globe } from 'lucide-react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, AreaChart, Area, XAxis, YAxis, CartesianGrid } from 'recharts';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#64748b'];

const generateApiData = () => {
  const data = [];
  const now = new Date();
  for (let i = 14; i >= 0; i--) {
    data.push({
      date: new Date(now.getTime() - i * 24 * 60 * 60 * 1000).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
      requests: Math.floor(Math.random() * 5000) + 1000,
    });
  }
  return data;
};

export const Dashboard = () => {
  const { data, isLoading } = useQuery({
    queryKey: ['properties', 0, 1000],
    queryFn: () => propertyService.getProperties(0, 1000),
  });

  const properties = data?.content || [];
  const activeCount = properties.filter(p => p.isActive).length;
  const restrictedCount = properties.filter(p => p.isRestricted).length;
  
  // Category distribution
  const categories = properties.reduce((acc, curr) => {
    const cat = curr.category || 'UNCATEGORIZED';
    acc[cat] = (acc[cat] || 0) + 1;
    return acc;
  }, {});
  
  const pieData = Object.keys(categories).map(key => ({
    name: key,
    value: categories[key],
  })).sort((a, b) => b.value - a.value);

  const apiData = React.useMemo(() => generateApiData(), []);

  const container = { hidden: { opacity: 0 }, show: { opacity: 1, transition: { staggerChildren: 0.05 } } };
  const item = { hidden: { opacity: 0, y: 15 }, show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 300, damping: 24 } } };

  if (isLoading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[var(--accent)]"></div>
      </div>
    );
  }

  return (
    <motion.div variants={container} initial="hidden" animate="show" className="space-y-6">
      <motion.div variants={item}>
        <h1 className="text-2xl font-semibold tracking-tight">Overview</h1>
        <p className="text-[14px] text-[var(--text-muted)] mt-1">Manage and monitor your configuration infrastructure.</p>
      </motion.div>

      {/* Stats Row */}
      <motion.div variants={item} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {[
          { label: 'Active Properties', value: activeCount, icon: Zap, color: 'text-amber-500', bg: 'bg-amber-50 dark:bg-amber-500/10' },
          { label: 'Restricted Keys', value: restrictedCount, icon: ShieldAlert, color: 'text-red-500', bg: 'bg-red-50 dark:bg-red-500/10' },
          { label: 'Total Configurations', value: properties.length, icon: List, color: 'text-blue-500', bg: 'bg-blue-50 dark:bg-blue-500/10' },
          { label: 'API Requests (14d)', value: '45.2K', icon: Activity, color: 'text-emerald-500', bg: 'bg-emerald-50 dark:bg-emerald-500/10', trend: '+12%' },
        ].map((stat, i) => (
          <div key={i} className="card p-5 group hover:shadow-md transition-all duration-300">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-[13px] font-medium text-[var(--text-muted)]">{stat.label}</p>
                <h3 className="text-2xl font-semibold mt-1">{stat.value}</h3>
                {stat.trend && (
                  <p className="text-[12px] text-emerald-600 dark:text-emerald-400 mt-1 flex items-center gap-1 font-medium">
                    <ArrowUpRight size={12} /> {stat.trend}
                  </p>
                )}
              </div>
              <div className={`p-2.5 rounded-xl ${stat.bg}`}>
                <stat.icon size={18} className={stat.color} />
              </div>
            </div>
          </div>
        ))}
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* API Usage Chart */}
        <motion.div variants={item} className="card p-5 lg:col-span-2 flex flex-col">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-[15px] font-semibold flex items-center gap-2"><Globe size={16} className="text-[var(--text-muted)]"/> API Traffic</h3>
              <p className="text-[12px] text-[var(--text-muted)] mt-0.5">Configuration fetch requests over time</p>
            </div>
          </div>
          <div className="h-[280px] w-full flex-1">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={apiData} margin={{ top: 5, right: 0, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorRequests" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--accent)" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="var(--accent)" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border-color)" opacity={0.5} />
                <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fontSize: 11, fill: 'var(--text-muted)' }} dy={10} />
                <YAxis axisLine={false} tickLine={false} tick={{ fontSize: 11, fill: 'var(--text-muted)' }} />
                <Tooltip 
                  contentStyle={{ backgroundColor: 'var(--bg-secondary)', borderColor: 'var(--border-color)', borderRadius: '8px', fontSize: '12px' }}
                  itemStyle={{ color: 'var(--text-primary)' }}
                />
                <Area type="monotone" dataKey="requests" stroke="var(--accent)" strokeWidth={2} fillOpacity={1} fill="url(#colorRequests)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* Category Distribution */}
        <motion.div variants={item} className="card p-5 flex flex-col">
          <div>
            <h3 className="text-[15px] font-semibold flex items-center gap-2"><Server size={16} className="text-[var(--text-muted)]"/> Categories</h3>
            <p className="text-[12px] text-[var(--text-muted)] mt-0.5">Properties by logical grouping</p>
          </div>
          <div className="flex-1 min-h-[280px] relative flex items-center justify-center mt-4">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={pieData.length ? pieData : [{ name: 'Empty', value: 1 }]}
                  cx="50%"
                  cy="50%"
                  innerRadius={65}
                  outerRadius={85}
                  paddingAngle={5}
                  dataKey="value"
                  stroke="none"
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip 
                  contentStyle={{ backgroundColor: 'var(--bg-secondary)', borderColor: 'var(--border-color)', borderRadius: '8px', fontSize: '12px' }}
                />
              </PieChart>
            </ResponsiveContainer>
            <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
              <span className="text-3xl font-bold">{properties.length}</span>
              <span className="text-[10px] text-[var(--text-muted)] uppercase tracking-wider font-semibold">Total</span>
            </div>
          </div>
        </motion.div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Search Analytics */}
        <motion.div variants={item} className="card p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-[15px] font-semibold flex items-center gap-2"><Search size={16} className="text-[var(--text-muted)]"/> Top Searches</h3>
            <span className="badge-gray text-[11px]">Last 7 days</span>
          </div>
          <div className="space-y-3">
            {[
              { query: 'DATABASE_URL', count: 124 },
              { query: 'JWT_SECRET', count: 89 },
              { query: 'SMTP', count: 56 },
              { query: 'API_KEY', count: 42 },
              { query: 'MAX_USERS', count: 28 },
            ].map((item, i) => (
              <div key={i} className="flex items-center justify-between p-2 rounded-lg hover:bg-[var(--bg-tertiary)] transition-colors">
                <span className="text-[13px] font-mono text-[var(--text-secondary)]">{item.query}</span>
                <span className="text-[12px] font-medium bg-[var(--bg-secondary)] border px-2 py-0.5 rounded-full">{item.count}</span>
              </div>
            ))}
          </div>
        </motion.div>

        {/* Recent Activity */}
        <motion.div variants={item} className="card p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-[15px] font-semibold flex items-center gap-2"><Activity size={16} className="text-[var(--text-muted)]"/> Recent Updates</h3>
          </div>
          <div className="space-y-4">
            {[
              { user: 'admin', action: 'updated', key: 'LOG_LEVEL', time: '10m ago' },
              { user: 'system', action: 'created', key: 'FEATURE_FLAGS', time: '2h ago' },
              { user: 'admin', action: 'deleted', key: 'OLD_API_KEY', time: '5h ago' },
              { user: 'viewer', action: 'accessed', key: 'RESTRICTED_KEYS', time: '1d ago' },
            ].map((activity, i) => (
              <div key={i} className="flex gap-3 relative before:absolute before:left-3.5 before:top-7 before:bottom-[-16px] before:w-px before:bg-zinc-200 dark:before:bg-zinc-800 last:before:hidden">
                <div className="w-7 h-7 rounded-full bg-[var(--bg-tertiary)] border flex items-center justify-center shrink-0 z-10 text-[10px] font-bold">
                  {activity.user[0].toUpperCase()}
                </div>
                <div className="pt-1.5 pb-2">
                  <p className="text-[13px] text-[var(--text-secondary)]">
                    <span className="font-medium text-[var(--text-primary)]">{activity.user}</span> {activity.action} <span className="font-mono text-[var(--text-primary)]">{activity.key}</span>
                  </p>
                  <p className="text-[11px] text-[var(--text-muted)] mt-0.5">{activity.time}</p>
                </div>
              </div>
            ))}
          </div>
        </motion.div>
      </div>
    </motion.div>
  );
};
