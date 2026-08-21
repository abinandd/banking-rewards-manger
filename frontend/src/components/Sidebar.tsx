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
} from 'lucide-react';

interface SidebarProps {
  currentTab: string;
  onSelectTab: (tab: string) => void;
  isAdmin: boolean;
}

export const Sidebar: React.FC<SidebarProps> = ({ currentTab, onSelectTab, isAdmin }) => {
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
    <aside className="w-64 bg-white border-r border-slate-200 min-h-[calc(100vh-4rem)] flex flex-col justify-between p-4">
      <div className="space-y-1">
        <div className="px-3 py-2 text-xs font-semibold uppercase tracking-wider text-slate-400">
          {isAdmin ? 'Financial Admin Console' : 'Banking & Rewards'}
        </div>
        {items.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id)}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                isActive
                  ? 'bg-emerald-50 text-emerald-700 shadow-sm border border-emerald-200 font-semibold'
                  : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
              }`}
            >
              <Icon className={`h-5 w-5 ${isActive ? 'text-emerald-600' : 'text-slate-400'}`} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </div>

      <div className="bg-gradient-to-br from-slate-900 to-slate-800 p-4 rounded-2xl text-white shadow-md">
        <div className="flex items-center space-x-2 text-emerald-400 mb-1">
          <Sparkles className="h-4 w-4" />
          <span className="text-xs font-bold uppercase tracking-wider">Live Audit Ledger</span>
        </div>
        <p className="text-xs text-slate-300">
          Transactions trigger real-time multi-rule calculations with automatic immutable ledger entries &amp; reversal capability.
        </p>
      </div>
    </aside>
  );
};
