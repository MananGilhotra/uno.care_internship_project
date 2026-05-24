import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { propertyService } from '../../api/propertyService';
import { KeyRound, AlertTriangle } from 'lucide-react';
import { motion } from 'framer-motion';

const container = { hidden: { opacity: 0 }, show: { opacity: 1, transition: { staggerChildren: 0.08 } } };
const item = { hidden: { opacity: 0, y: 12 }, show: { opacity: 1, y: 0 } };

export const RestrictedKeys = () => {
  const { data, isLoading } = useQuery({
    queryKey: ['properties', 0, 100],
    queryFn: () => propertyService.getProperties(0, 100),
  });

  const restricted = data?.content.filter(p => p.isRestricted) || [];

  return (
    <motion.div variants={container} initial="hidden" animate="show" className="space-y-5">
      <motion.div variants={item}>
        <h1 className="text-xl font-semibold tracking-tight">Restricted Keys</h1>
        <p className="text-[13px] text-[var(--text-muted)] mt-0.5">Sensitive properties with masked values</p>
      </motion.div>

      <motion.div variants={item} className="flex items-start gap-3 card p-4 border-amber-200 dark:border-amber-800 bg-amber-50/50 dark:bg-amber-950/20">
        <AlertTriangle size={16} className="text-amber-500 shrink-0 mt-0.5" />
        <div>
          <p className="text-[13px] font-medium text-amber-800 dark:text-amber-300">Security Notice</p>
          <p className="text-[12px] text-amber-700 dark:text-amber-400 mt-0.5">These values are masked in API responses and protected from modification.</p>
        </div>
      </motion.div>

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {[1, 2, 3].map(i => <div key={i} className="card p-5"><div className="skeleton h-4 w-32 mb-4" /><div className="skeleton h-10 w-full" /></div>)}
        </div>
      ) : restricted.length === 0 ? (
        <div className="card p-12 text-center text-[var(--text-muted)] text-sm">No restricted keys found.</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {restricted.map((prop) => (
            <motion.div key={prop.id} variants={item} className="card-hover p-5 relative overflow-hidden group">
              <div className="absolute top-3 right-3 opacity-[0.06] group-hover:opacity-[0.1] transition-opacity">
                <KeyRound size={48} />
              </div>
              <div className="flex items-center gap-2 mb-3">
                <KeyRound size={14} className="text-[var(--accent)]" />
                <h3 className="font-mono text-[13px] font-semibold">{prop.key}</h3>
              </div>
              <div className="bg-[var(--bg-tertiary)] rounded-lg p-3 border">
                <p className="text-[11px] text-[var(--text-muted)] mb-1">Masked Value</p>
                <p className="font-mono text-sm">{prop.value}</p>
              </div>
              <div className="flex items-center justify-between mt-3 text-[11px] text-[var(--text-muted)]">
                <span className="badge-amber">{prop.category}</span>
                <span>ID #{prop.id}</span>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </motion.div>
  );
};
