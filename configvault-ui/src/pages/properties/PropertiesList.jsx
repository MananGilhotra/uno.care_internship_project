import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

import { propertyService } from '../../api/propertyService';
import { Search, Plus, Edit2, Trash2, KeyRound, Download, Filter, ChevronLeft, ChevronRight } from 'lucide-react';
import { motion } from 'framer-motion';
import { useAuth } from '../../hooks/useAuth';
import { Link, useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';

export const PropertiesList = () => {
  const { isAdmin } = useAuth();
  const [searchParams] = useSearchParams();
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState(searchParams.get('search') || '');
  const [category, setCategory] = useState('');

  const queryClient = useQueryClient();

  const { data: categories } = useQuery({
    queryKey: ['categories'],
    queryFn: propertyService.getCategories,
  });

  const { data, isLoading } = useQuery({
    queryKey: ['properties', page, 10, category],
    queryFn: () => propertyService.getProperties(page, 10, category || undefined),
  });

  const deleteMutation = useMutation({
    mutationFn: propertyService.deleteProperty,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['properties'] });
      toast.success('Property soft-deleted');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Delete failed');
    },
  });

  const properties = data?.content || [];
  const filtered = search
    ? properties.filter(p => p.key.toLowerCase().includes(search.toLowerCase()) || p.value.toLowerCase().includes(search.toLowerCase()))
    : properties;

  const handleDelete = (key) => {
    if (window.confirm(`Soft-delete "${key}"? This can be reversed.`)) {
      deleteMutation.mutate(key);
    }
  };

  const exportCSV = () => {
    if (!properties.length) return;
    const headers = ['Key', 'Value', 'Category', 'Active', 'Restricted', 'Created', 'Modified'];
    const rows = properties.map(p => [p.key, p.value, p.category, p.isActive, p.isRestricted, p.createdDate, p.lastModifiedDate]);
    const csv = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `configvault-properties-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    toast.success('CSV exported successfully');
  };

  const container = { hidden: { opacity: 0 }, show: { opacity: 1, transition: { staggerChildren: 0.03 } } };
  const row = { hidden: { opacity: 0 }, show: { opacity: 1 } };

  return (
    <div className="space-y-5">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold tracking-tight">Properties</h1>
          <p className="text-[13px] text-[var(--text-muted)] mt-0.5">
            {data ? `${data.totalElements} total properties` : 'Loading...'}
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button onClick={exportCSV} className="btn-secondary text-[13px] gap-1.5">
            <Download size={14} /> Export CSV
          </button>
          {isAdmin && (
            <Link to="/properties/create" className="btn-primary text-[13px] gap-1.5">
              <Plus size={14} /> Add Property
            </Link>
          )}
        </div>
      </div>

      {/* Filters */}
      <div className="card p-3 flex flex-col sm:flex-row gap-3 items-stretch sm:items-center">
        <div className="relative flex-1">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--text-muted)]" />
          <input
            type="text"
            className="input-field pl-9 text-[13px]"
            placeholder="Search keys or values..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-2">
          <Filter size={14} className="text-[var(--text-muted)] shrink-0" />
          <select
            className="input-field text-[13px] w-auto min-w-[140px]"
            value={category}
            onChange={(e) => { setCategory(e.target.value); setPage(0); }}
          >
            <option value="">All categories</option>
            {categories?.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                <th className="table-header">Key</th>
                <th className="table-header">Value</th>
                <th className="table-header">Category</th>
                <th className="table-header text-center">Status</th>
                {isAdmin && <th className="table-header text-right pr-6">Actions</th>}
              </tr>
            </thead>
            <motion.tbody variants={container} initial="hidden" animate="show">
              {isLoading ? (
                Array(6).fill(0).map((_, i) => (
                  <tr key={i} className="table-row">
                    <td className="table-cell"><div className="skeleton h-4 w-28" /></td>
                    <td className="table-cell"><div className="skeleton h-4 w-20" /></td>
                    <td className="table-cell"><div className="skeleton h-4 w-16" /></td>
                    <td className="table-cell text-center"><div className="skeleton h-4 w-4 mx-auto rounded-full" /></td>
                    {isAdmin && <td className="table-cell"><div className="skeleton h-4 w-14 ml-auto" /></td>}
                  </tr>
                ))
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={isAdmin ? 5 : 4} className="py-12 text-center text-[var(--text-muted)] text-sm">
                    No properties found
                  </td>
                </tr>
              ) : (
                filtered.map((prop) => (
                  <motion.tr key={prop.id} variants={row} className="table-row group">
                    <td className="table-cell">
                      <div className="flex items-center gap-1.5">
                        {prop.isRestricted && <KeyRound size={13} className="text-amber-500 shrink-0" />}
                        <span className="font-mono text-[13px] font-medium">{prop.key}</span>
                      </div>
                    </td>
                    <td className="table-cell font-mono text-[13px] text-[var(--text-secondary)] max-w-[200px] truncate">
                      {prop.value}
                    </td>
                    <td className="table-cell">
                      <span className="badge-gray">{prop.category || '—'}</span>
                    </td>
                    <td className="table-cell text-center">
                      <span className={`inline-block w-2 h-2 rounded-full ${prop.isActive ? 'bg-emerald-500' : 'bg-red-400'}`} />
                    </td>
                    {isAdmin && (
                      <td className="table-cell text-right pr-4">
                        <div className="flex items-center justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                          <Link to={`/properties/edit/${prop.key}`} className="btn-ghost p-1.5" title="Edit">
                            <Edit2 size={14} />
                          </Link>
                          <button onClick={() => handleDelete(prop.key)} className="btn-ghost p-1.5 text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950/30" title="Delete">
                            <Trash2 size={14} />
                          </button>
                        </div>
                      </td>
                    )}
                  </motion.tr>
                ))
              )}
            </motion.tbody>
          </table>
        </div>

        {/* Pagination */}
        {data && data.totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t text-[13px]">
            <span className="text-[var(--text-muted)]">
              Page {data.pageNumber + 1} of {data.totalPages}
            </span>
            <div className="flex gap-1">
              <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0} className="btn-ghost gap-1">
                <ChevronLeft size={14} /> Prev
              </button>
              <button onClick={() => setPage(p => Math.min(data.totalPages - 1, p + 1))} disabled={data.last} className="btn-ghost gap-1">
                Next <ChevronRight size={14} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
