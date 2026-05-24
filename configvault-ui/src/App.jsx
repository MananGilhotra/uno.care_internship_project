import { Routes, Route, Navigate } from 'react-router-dom';
import { AdminLayout } from './components/layout/AdminLayout';
import { ProtectedRoute } from './components/ProtectedRoute';

import { Dashboard } from './pages/Dashboard';
import { PropertiesList } from './pages/properties/PropertiesList';
import { PropertyForm } from './pages/properties/PropertyForm';
import { RestrictedKeys } from './pages/properties/RestrictedKeys';
import { ActivityLogs } from './pages/ActivityLogs';
import { Settings } from './pages/Settings';

function App() {
  return (
    <Routes>
      
      <Route element={<ProtectedRoute />}>
        <Route element={<AdminLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/properties" element={<PropertiesList />} />
          <Route path="/restricted-keys" element={<RestrictedKeys />} />
          <Route path="/activity" element={<ActivityLogs />} />
          <Route path="/settings" element={<Settings />} />
          
          {/* Admin only */}
          <Route element={<ProtectedRoute requireAdmin={true} />}>
            <Route path="/properties/create" element={<PropertyForm />} />
            <Route path="/properties/edit/:key" element={<PropertyForm />} />
          </Route>
          
          <Route path="/unauthorized" element={
            <div className="card p-12 text-center max-w-md mx-auto mt-20">
              <p className="text-4xl mb-4">🚫</p>
              <h2 className="text-lg font-semibold mb-2">Access Denied</h2>
              <p className="text-[13px] text-[var(--text-muted)]">You don't have permission to access this resource.</p>
            </div>
          } />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
