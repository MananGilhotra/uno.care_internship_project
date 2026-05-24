import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { propertyService } from '../../api/propertyService';
import { ArrowLeft, Save, Loader2 } from 'lucide-react';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';

export const PropertyForm = () => {
  const { key } = useParams();
  const isEditMode = !!key;
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [formData, setFormData] = useState({ key: '', value: '', category: '' });

  const { data: property, isLoading: isFetching } = useQuery({
    queryKey: ['property', key],
    queryFn: () => propertyService.getPropertyByKey(key),
    enabled: isEditMode,
  });

  useEffect(() => {
    if (property) {
      setFormData({ key: property.key || '', value: property.value || '', category: property.category || '' });
    }
  }, [property]);

  const mutation = useMutation({
    mutationFn: (data) => isEditMode ? propertyService.updateProperty(data) : propertyService.createProperty(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['properties'] });
      queryClient.invalidateQueries({ queryKey: ['categories'] });
      toast.success(`Property ${isEditMode ? 'updated' : 'created'}`);
      navigate('/properties');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to save');
    },
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.key.trim() || !formData.value.trim()) {
      toast.error('Key and Value are required');
      return;
    }
    mutation.mutate(formData);
  };

  const handleChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  if (isEditMode && isFetching) {
    return <div className="flex justify-center items-center h-64"><Loader2 className="animate-spin h-6 w-6 text-[var(--accent)]" /></div>;
  }

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="max-w-2xl mx-auto space-y-5">
      <div className="flex items-center gap-3">
        <Link to="/properties" className="p-1.5 rounded-lg border hover:bg-[var(--bg-tertiary)] transition-colors">
          <ArrowLeft size={16} />
        </Link>
        <div>
          <h1 className="text-xl font-semibold tracking-tight">{isEditMode ? 'Edit Property' : 'Create Property'}</h1>
          <p className="text-[13px] text-[var(--text-muted)] mt-0.5">
            {isEditMode ? `Editing ${key}` : 'Add a new configuration entry'}
          </p>
        </div>
      </div>

      <div className="card p-6">
        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label htmlFor="key" className="block text-[12px] font-medium text-[var(--text-muted)] uppercase tracking-wide mb-1.5">
              Property Key <span className="text-red-500">*</span>
            </label>
            <input id="key" name="key" type="text" value={formData.key} onChange={handleChange} disabled={isEditMode}
              className={`input-field font-mono ${isEditMode ? 'opacity-60 cursor-not-allowed' : ''}`} placeholder="e.g. DATABASE_URL" />
            {isEditMode && <p className="text-[11px] text-[var(--text-muted)] mt-1">Keys cannot be modified after creation</p>}
          </div>

          <div>
            <label htmlFor="value" className="block text-[12px] font-medium text-[var(--text-muted)] uppercase tracking-wide mb-1.5">
              Value <span className="text-red-500">*</span>
            </label>
            <input id="value" name="value" type="text" value={formData.value} onChange={handleChange}
              className="input-field" placeholder="Enter the configuration value" />
          </div>

          <div>
            <label htmlFor="category" className="block text-[12px] font-medium text-[var(--text-muted)] uppercase tracking-wide mb-1.5">
              Category
            </label>
            <input id="category" name="category" type="text" value={formData.category} onChange={handleChange}
              className="input-field" placeholder="e.g. DATABASE, SECURITY" list="categoryList" />
            <datalist id="categoryList">
              <option value="APPLICATION" /><option value="DATABASE" /><option value="SECURITY" /><option value="CACHE" /><option value="EMAIL" /><option value="LOGGING" />
            </datalist>
          </div>

          <div className="pt-4 border-t flex justify-end gap-2">
            <Link to="/properties" className="btn-secondary">Cancel</Link>
            <button type="submit" className="btn-primary gap-1.5" disabled={mutation.isPending}>
              {mutation.isPending ? <><Loader2 size={14} className="animate-spin" /> Saving...</> : <><Save size={14} /> Save</>}
            </button>
          </div>
        </form>
      </div>
    </motion.div>
  );
};
