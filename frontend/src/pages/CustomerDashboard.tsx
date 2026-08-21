import React from 'react';
import { 
  Wallet, 
  Award, 
  TrendingUp, 
  ShoppingBag, 
  Sparkles, 
  Plus, 
  ArrowRight
} from 'lucide-react';
import { User, RewardWallet, Transaction, Reward } from '../types';

interface CustomerDashboardProps {
  user: User;
  wallet: RewardWallet | null;
  transactions: Transaction[];
  rewards: Reward[];
  onOpenCreateTxn: () => void;
  onSelectTab: (tab: string) => void;
  onSelectTxn: (txn: Transaction) => void;
}

export const CustomerDashboard: React.FC<CustomerDashboardProps> = ({
  user,
  wallet,
  transactions,
  rewards,
  onOpenCreateTxn,
  onSelectTab,
  onSelectTxn,
}) => {
  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  };

  // Calculate total cashback earned this month
  const totalCashbackCredited = rewards
    .filter(r => r.type === 'CASHBACK' && r.status === 'CREDITED')
    .reduce((acc, r) => acc + Number(r.amount), 0);

  const recentTransactions = transactions.slice(0, 5);

  return (
    <div className="space-y-6">
      {/* Header Greeting */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
            {getGreeting()}, {user.name}
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Here is your live financial rewards ledger and active tier benefits.
          </p>
        </div>
        <div className="flex items-center space-x-3">
          <button
            onClick={onOpenCreateTxn}
            className="flex items-center space-x-2 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold rounded-xl shadow-sm shadow-emerald-600/30 transition active:scale-95"
          >
            <Plus className="h-4 w-4" />
            <span>Make a Purchase</span>
          </button>
        </div>
      </div>

      {/* Main Balances Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Available Cashback Card */}
        <div className="bg-gradient-to-br from-emerald-600 to-teal-700 rounded-3xl p-6 text-white shadow-xl shadow-emerald-900/10 relative overflow-hidden">
          <div className="absolute top-0 right-0 -mr-8 -mt-8 w-36 h-36 bg-white/10 rounded-full blur-2xl pointer-events-none"></div>
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-emerald-100">
              Available Cashback
            </span>
            <div className="p-2 rounded-xl bg-white/10 backdrop-blur-md">
              <Wallet className="h-5 w-5 text-white" />
            </div>
          </div>
          <div className="mt-4">
            <div className="text-3xl sm:text-4xl font-extrabold tracking-tight">
              ₹{wallet ? Number(wallet.cashbackBalance).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) : '0.00'}
            </div>
            <div className="flex items-center space-x-1.5 text-xs text-emerald-100 mt-2">
              <TrendingUp className="h-3.5 w-3.5" />
              <span>Available for instant bill payout or statement credit</span>
            </div>
          </div>
        </div>

        {/* Reward Points Card */}
        <div className="bg-gradient-to-br from-slate-900 to-slate-800 rounded-3xl p-6 text-white shadow-xl shadow-slate-950/10 relative overflow-hidden">
          <div className="absolute top-0 right-0 -mr-8 -mt-8 w-36 h-36 bg-emerald-500/10 rounded-full blur-2xl pointer-events-none"></div>
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">
              Reward Points
            </span>
            <div className="p-2 rounded-xl bg-slate-800 border border-slate-700">
              <Award className="h-5 w-5 text-amber-400" />
            </div>
          </div>
          <div className="mt-4">
            <div className="text-3xl sm:text-4xl font-extrabold tracking-tight text-white">
              {wallet ? Number(wallet.pointsBalance).toLocaleString('en-IN') : '0'}
              <span className="text-base font-medium text-slate-400 ml-2">PTS</span>
            </div>
            <div className="flex items-center space-x-1.5 text-xs text-slate-400 mt-2">
              <Sparkles className="h-3.5 w-3.5 text-amber-400" />
              <span>{user.tier} Tier multiplier active</span>
            </div>
          </div>
        </div>

        {/* Tier Status Card */}
        <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                Membership Tier
              </span>
              <span className={`px-2.5 py-1 text-xs font-bold rounded-full ${
                user.tier === 'PLATINUM'
                  ? 'bg-purple-100 text-purple-700'
                  : user.tier === 'GOLD'
                  ? 'bg-amber-100 text-amber-700'
                  : 'bg-slate-100 text-slate-700'
              }`}>
                {user.tier} ⭐
              </span>
            </div>
            <div className="mt-3">
              <div className="text-lg font-bold text-slate-900">
                {user.tier === 'GOLD' ? '2.5% Maximum Reward Rate' : user.tier === 'PLATINUM' ? '3.5% Maximum Reward Rate' : '1.5% Standard Reward Rate'}
              </div>
              <p className="text-xs text-slate-500 mt-1">
                Enjoy automated multiplier boosts across all retail transactions.
              </p>
            </div>
          </div>
          <button
            onClick={() => onSelectTab('tiers')}
            className="mt-4 flex items-center justify-between text-xs font-bold text-emerald-600 hover:text-emerald-700 transition pt-3 border-t border-slate-100"
          >
            <span>View Tier Privileges</span>
            <ArrowRight className="h-4 w-4" />
          </button>
        </div>
      </div>

      {/* Progress / Monthly Growth Banner */}
      <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="text-xs font-semibold uppercase tracking-wider text-slate-400">
              Cashback this month
            </div>
            <div className="text-2xl font-black text-slate-900 mt-1">
              ₹{totalCashbackCredited.toFixed(2)}
            </div>
            <div className="text-xs font-medium text-emerald-600 mt-1 flex items-center space-x-1">
              <TrendingUp className="h-3.5 w-3.5" />
              <span>+18.4% vs last billing cycle</span>
            </div>
          </div>
          <div className="w-full sm:w-1/2">
            <div className="flex justify-between text-xs font-semibold text-slate-500 mb-1.5">
              <span>Monthly Target ₹500</span>
              <span>{Math.min(100, Math.round((totalCashbackCredited / 500) * 100))}%</span>
            </div>
            <div className="w-full bg-slate-100 rounded-full h-3 overflow-hidden">
              <div
                className="bg-emerald-500 h-3 rounded-full transition-all duration-500"
                style={{ width: `${Math.min(100, (totalCashbackCredited / 500) * 100)}%` }}
              ></div>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Transactions & Reward Inspection */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Recent Transactions &amp; Audit Trail</h2>
            <p className="text-xs text-slate-500">Click any transaction to inspect exact multi-rule calculations or trigger a refund reversal</p>
          </div>
          <button
            onClick={() => onSelectTab('transactions')}
            className="text-xs font-bold text-emerald-600 hover:text-emerald-700 flex items-center space-x-1"
          >
            <span>View All</span>
            <ArrowRight className="h-4 w-4" />
          </button>
        </div>

        <div className="divide-y divide-slate-100">
          {recentTransactions.length === 0 ? (
            <div className="p-8 text-center text-slate-400 text-sm">
              No transactions recorded yet. Make a purchase above to trigger the Reward Engine!
            </div>
          ) : (
            recentTransactions.map((txn) => {
              const matchedRewards = rewards.filter(r => r.transactionId === txn.id);
              const cashbackReward = matchedRewards.find(r => r.type === 'CASHBACK');
              const isRefunded = txn.status === 'REFUNDED';

              return (
                <div
                  key={txn.id}
                  onClick={() => onSelectTxn(txn)}
                  className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50/80 cursor-pointer transition"
                >
                  <div className="flex items-center space-x-4">
                    <div className={`p-3 rounded-2xl ${
                      isRefunded ? 'bg-rose-100 text-rose-600' : 'bg-slate-100 text-slate-700'
                    }`}>
                      <ShoppingBag className="h-5 w-5" />
                    </div>
                    <div>
                      <div className="flex items-center space-x-2">
                        <span className="font-bold text-slate-900 text-sm">{txn.merchantName}</span>
                        <span className="text-[11px] px-2 py-0.5 rounded-md bg-slate-100 text-slate-600 font-medium">
                          {txn.category}
                        </span>
                        {isRefunded ? (
                          <span className="text-[11px] px-2 py-0.5 rounded-md bg-rose-100 text-rose-700 font-bold">
                            REFUNDED
                          </span>
                        ) : (
                          <span className="text-[11px] px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-700 font-bold">
                            COMPLETED
                          </span>
                        )}
                      </div>
                      <div className="text-xs text-slate-400 mt-1 flex items-center space-x-2">
                        <span>TXN-{txn.id}</span>
                        <span>•</span>
                        <span>{new Date(txn.createdAt).toLocaleDateString('en-IN', { month: 'short', day: 'numeric', year: 'numeric' })}</span>
                      </div>
                    </div>
                  </div>

                  <div className="text-right">
                    <div className="text-sm font-black text-slate-900">
                      ₹{Number(txn.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </div>
                    {cashbackReward && (
                      <div className={`text-xs font-bold mt-0.5 ${
                        isRefunded ? 'text-rose-500 line-through' : 'text-emerald-600'
                      }`}>
                        +{Number(cashbackReward.amount).toFixed(2)} Cashback
                      </div>
                    )}
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
};
