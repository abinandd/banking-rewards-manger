import React from 'react';
import { Search, Bell, Settings, HelpCircle } from 'lucide-react';

interface HeaderProps {
  currentTab: string;
  isAdmin: boolean;
}

export const Header: React.FC<HeaderProps> = ({ currentTab, isAdmin }) => {
  const getTabName = () => {
    switch (currentTab) {
      case 'dashboard': return 'Dashboard Overview';
      case 'wallet': return 'Rewards Wallet & Ledger';
      case 'transactions': return 'Transactions History';
      case 'offers': return 'Offers & Campaigns';
      case 'tiers': return 'Tier & Loyalty Status';
      case 'referrals': return 'Referrals & Invites';
      case 'admin-dashboard': return 'Admin Analytics';
      case 'admin-rules': return 'Reward Rules Engine';
      case 'admin-campaigns': return 'Campaign Manager';
      case 'admin-transactions': return 'Global Audit Ledger';
      case 'admin-users': return 'Customer Management';
      default: return 'Overview';
    }
  };

  return (
    <header className="bg-white border-b border-slate-200 h-16 flex items-center justify-between px-4 sm:px-6 lg:px-8 flex-shrink-0 z-10 relative">
      <div className="flex items-center space-x-4">
        <h2 className="text-lg font-bold text-slate-900 tracking-tight">
          {getTabName()}
        </h2>
        <div className="hidden sm:flex items-center space-x-2 text-xs font-semibold uppercase tracking-wider text-slate-400 border-l border-slate-200 pl-4">
          {isAdmin ? 'Admin Console' : 'Customer View'}
        </div>
      </div>

      <div className="flex items-center space-x-4">
        <div className="hidden md:flex relative group">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-4 w-4 text-slate-400 group-focus-within:text-emerald-500 transition-colors" />
          </div>
          <input
            type="text"
            placeholder="Search..."
            className="pl-10 pr-4 py-1.5 w-64 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 transition-all"
          />
        </div>

        <div className="flex items-center space-x-3 text-slate-400 border-l border-slate-200 pl-4">
          <button className="hover:text-slate-600 transition relative">
            <Bell className="h-5 w-5" />
            <span className="absolute top-0 right-0 h-2 w-2 rounded-full bg-rose-500 ring-2 ring-white"></span>
          </button>
          <button className="hover:text-slate-600 transition">
            <Settings className="h-5 w-5" />
          </button>
          <button className="hover:text-slate-600 transition">
            <HelpCircle className="h-5 w-5" />
          </button>
        </div>
      </div>
    </header>
  );
};
