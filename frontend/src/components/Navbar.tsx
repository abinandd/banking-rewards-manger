import React from 'react';
import { Bell, ShieldCheck, User as UserIcon, Sparkles } from 'lucide-react';
import { User } from '../types';

interface NavbarProps {
  currentUser: User | null;
  users: User[];
  onSelectUser: (user: User) => void;
  isAdmin: boolean;
  onToggleAdmin: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentUser,
  users,
  onSelectUser,
  isAdmin,
  onToggleAdmin,
}) => {
  return (
    <header className="bg-slate-900 text-white border-b border-slate-800 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-emerald-500 to-teal-400 flex items-center justify-center shadow-lg shadow-emerald-500/20">
            <Sparkles className="h-6 w-6 text-white" />
          </div>
          <div>
            <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-white via-slate-200 to-emerald-400 bg-clip-text text-transparent">
              RewardsBank
            </span>
            <span className="hidden sm:inline-block ml-2 text-xs uppercase tracking-wider px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 font-semibold border border-emerald-500/20">
              Mini Monolith
            </span>
          </div>
        </div>

        <div className="flex items-center space-x-4">
          {/* Admin Switch */}
          <button
            onClick={onToggleAdmin}
            className={`flex items-center space-x-2 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all border ${
              isAdmin
                ? 'bg-amber-500/20 text-amber-300 border-amber-500/30'
                : 'bg-slate-800 hover:bg-slate-700 text-slate-300 border-slate-700'
            }`}
          >
            <ShieldCheck className="h-4 w-4" />
            <span>{isAdmin ? 'Admin Console Active' : 'Switch to Admin'}</span>
          </button>

          {/* User Switcher */}
          {!isAdmin && (
            <div className="flex items-center space-x-2 bg-slate-800/80 px-3 py-1.5 rounded-lg border border-slate-700">
              <UserIcon className="h-4 w-4 text-emerald-400" />
              <select
                className="bg-transparent text-sm font-medium text-slate-200 focus:outline-none cursor-pointer"
                value={currentUser?.id || ''}
                onChange={(e) => {
                  const u = users.find(x => x.id === Number(e.target.value));
                  if (u) onSelectUser(u);
                }}
              >
                {users.map(u => (
                  <option key={u.id} value={u.id} className="bg-slate-900 text-slate-200">
                    {u.name} ({u.tier})
                  </option>
                ))}
              </select>
            </div>
          )}

          <div className="relative p-2 rounded-lg bg-slate-800 text-slate-400 hover:text-white cursor-pointer transition">
            <Bell className="h-5 w-5" />
            <span className="absolute top-1.5 right-1.5 h-2 w-2 rounded-full bg-emerald-400 ring-2 ring-slate-900"></span>
          </div>

          <div className="flex items-center space-x-3 pl-2 border-l border-slate-800">
            <div className="h-9 w-9 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center font-bold text-sm shadow-md">
              {currentUser ? currentUser.name.charAt(0) : 'A'}
            </div>
            <div className="hidden md:block">
              <div className="text-sm font-semibold leading-none">{currentUser?.name || 'Customer'}</div>
              <div className="text-xs text-slate-400 mt-1">{currentUser ? `${currentUser.tier} Tier` : 'System Admin'}</div>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};
