import React from 'react';
import {
 LayoutDashboard,
 Wallet,
 ArrowLeftRight,
 Sparkles,
 Award,
 Users,
 Sliders,
 BarChart3,
 Flame,
 ShieldCheck,
 User as UserIcon,
 LogOut
} from 'lucide-react';
import { User } from '../types';

interface SidebarProps {
 currentTab: string;
 onSelectTab: (tab: string) => void;
 isAdmin: boolean;
 onToggleAdmin: () => void;
 currentUser: User | null;
 users: User[];
 onSelectUser: (user: User) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
 currentTab,
 onSelectTab,
 isAdmin,
 onToggleAdmin,
 currentUser,
 users,
 onSelectUser,
}) => {
 const customerNavItems = [
 { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
 { id: 'wallet', label: 'Rewards Wallet', icon: Wallet },
 { id: 'transactions', label: 'Transactions & Rewards', icon: ArrowLeftRight },
 { id: 'offers', label: 'Offers & Campaigns', icon: Flame },
 { id: 'tiers', label: 'Tier & Loyalty', icon: Award },
 { id: 'referrals', label: 'Invite & Earn', icon: Users },
 ];

 const adminNavItems = [
 { id: 'admin-dashboard', label: 'Admin Analytics', icon: BarChart3 },
 { id: 'admin-rules', label: 'Reward Rules Engine', icon: Sliders },
 { id: 'admin-campaigns', label: 'Campaign Manager', icon: Flame },
 { id: 'admin-transactions', label: 'All Transactions & Refunds', icon: ArrowLeftRight },
 { id: 'admin-users', label: 'Customer Management', icon: Users },
 ];

 const items = isAdmin ? adminNavItems : customerNavItems;

 return (
 <aside className="w-72 bg-slate-900 h-full overflow-y-auto flex flex-col justify-between p-4 flex-shrink-0 text-slate-300">
 <div>
 {/* Branding */}
 <div className="flex items-center space-x-3 mb-8 px-2 mt-2">
 <div>
 <span className="text-xl font-medium tracking-tight bg-gradient-to-r from-white via-slate-200 to-emerald-400 bg-clip-text text-transparent block">
 VaultRewards
 </span>
 <span className="text-[10px] uppercase tracking-wider text-emerald-400 font-medium">
 Rewards Engine
 </span>
 </div>
 </div>

 <div className="space-y-1">
 <div className="px-3 py-2 text-[10px] font-medium uppercase tracking-wider text-slate-500 mb-1">
 {isAdmin ? 'Financial Admin Console' : 'Banking & Rewards'}
 </div>
 {items.map((item) => {
 const Icon = item.icon;
 const isActive = currentTab === item.id;
 return (
 <button
 key={item.id}
 onClick={() => onSelectTab(item.id)}
 className={`w-full flex items-center space-x-3 px-3 py-3 rounded-xl text-sm font-normal transition-all ${
 isActive
 ? 'bg-emerald-500/10 text-emerald-400 font-normal'
 : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
 }`}
 >
 <Icon className={`h-5 w-5 ${isActive ? 'text-emerald-400' : 'text-slate-500'}`} />
 <span>{item.label}</span>
 </button>
 );
 })}
 </div>
 </div>

 <div className="mt-8 space-y-4">
 {/* User Switcher */}
 {!isAdmin && (
 <div className="flex flex-col space-y-2 bg-slate-800/50 p-3 rounded-xl">
 <span className="text-[10px] font-medium uppercase tracking-wider text-slate-500">Active Customer</span>
 <div className="flex items-center space-x-2">
 <UserIcon className="h-4 w-4 text-emerald-400"/>
 <select
 className="bg-transparent text-sm font-normal text-slate-200 focus:outline-none cursor-pointer w-full"
 value={currentUser?.id || ''}
 onChange={(e) => {
 const u = users.find(x => x.id === Number(e.target.value));
 if (u) onSelectUser(u);
 }}
 >
 {users.map(u => (
 <option key={u.id} value={u.id} className="bg-slate-900 text-slate-200">
 {u.name}
 </option>
 ))}
 </select>
 </div>
 </div>
 )}

 {/* Admin Switch */}
 <button
 onClick={onToggleAdmin}
 className={`w-full flex items-center justify-center space-x-2 px-3 py-3 rounded-xl text-xs font-medium transition-all ${
 isAdmin
 ? 'bg-amber-500/20 text-amber-300 hover:bg-amber-500/30'
 : 'bg-slate-800 hover:bg-slate-700 text-slate-300 '
 }`}
 >
 <ShieldCheck className="h-4 w-4"/>
 <span>{isAdmin ? 'Exit Admin Console' : 'Switch to Admin'}</span>
 </button>

 {/* User Profile & Logout */}
 <div className="flex items-center justify-between p-3 bg-slate-950/50 rounded-xl">
 <div className="flex items-center space-x-3">
 <div className="h-9 w-9 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center font-medium text-sm text-white">
 {isAdmin ? 'A' : (currentUser ? currentUser.name.charAt(0) : 'U')}
 </div>
 <div>
 <div className="text-sm font-medium text-slate-200 leading-none">
 {isAdmin ? 'System Admin' : (currentUser?.name || 'Customer')}
 </div>
 <div className="text-[10px] text-emerald-400 font-normal mt-1">
 {isAdmin ? 'Full Access' : (currentUser ? `${currentUser.tier} TIER` : '')}
 </div>
 </div>
 </div>
 <button className="p-2 text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 rounded-lg transition"title="Logout">
 <LogOut className="h-4 w-4"/>
 </button>
 </div>
 </div>
 </aside>
 );
};
