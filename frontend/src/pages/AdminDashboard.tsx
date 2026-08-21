import React, { useState } from 'react';
import { 

 Users, 
 Wallet, 
 ArrowLeftRight, 
 Sliders, 
 Flame, 
 TrendingUp, 
 Plus, 
 Trash2, 
 Power, 


} from 'lucide-react';
import { 
 User, 
 Transaction, 
 Reward, 
 RewardRule, 
 Campaign, 
 Referral, 

} from '../types';

interface AdminDashboardProps {
 currentTab: string;
 users: User[];
 transactions: Transaction[];
 rewards: Reward[];
 rules: RewardRule[];
 campaigns: Campaign[];
 referrals: Referral[];
 onToggleRule: (id: number) => Promise<void>;
 onCreateRule: (rule: Partial<RewardRule>) => Promise<void>;
 onDeleteRule: (id: number) => Promise<void>;
 onToggleCampaign: (id: number) => Promise<void>;
 onCreateCampaign: (campaign: Partial<Campaign>) => Promise<void>;
 onDeleteCampaign: (id: number) => Promise<void>;
 onRefundTxn: (id: number) => Promise<void>;
 onCreateUser: (name: string, email: string, tier: string) => Promise<void>;
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({
 currentTab,
 users,
 transactions,
 rewards,
 rules,
 campaigns,
 referrals,
 onToggleRule,
 onCreateRule,
 onDeleteRule,
 onToggleCampaign,
 onCreateCampaign,
 onDeleteCampaign,
 onRefundTxn,
 onCreateUser,
}) => {
 // Aggregate Metrics
 const totalCashbackDisbursed = rewards
 .filter(r => r.type === 'CASHBACK' && r.status === 'CREDITED')
 .reduce((acc, r) => acc + Number(r.amount), 0);

 const totalPointsDisbursed = rewards
 .filter(r => r.type === 'POINTS' && r.status === 'CREDITED')
 .reduce((acc, r) => acc + Number(r.points), 0);

 const totalVolume = transactions.reduce((acc, t) => acc + Number(t.amount), 0);

 // New Rule Form State
 const [showRuleModal, setShowRuleModal] = useState(false);
 const [newRule, setNewRule] = useState<Partial<RewardRule>>({
 category: 'GROCERIES',
 merchant: 'ALL',
 rewardType: 'CASHBACK',
 rewardValue: 2.0,
 maxReward: 200,
 active: true,
 });

 // New Campaign Form State
 const [showCampaignModal, setShowCampaignModal] = useState(false);
 const [newCampaign, setNewCampaign] = useState<Partial<Campaign>>({
 name: 'Weekend Dining Boost',
 description: 'Get extra 4% cashback on all dining transactions',
 category: 'DINING',
 merchant: 'ALL',
 bonusPercentage: 4.0,
 minTransactionAmount: 500,
 maxReward: 300,
 bonusPoints: 100,
 active: true,
 });

 // New User Form State
 const [showUserModal, setShowUserModal] = useState(false);
 const [userName, setUserName] = useState('');
 const [userEmail, setUserEmail] = useState('');
 const [userTier, setUserTier] = useState('SILVER');

 return (
 <div className="space-y-6">
 {/* Header */}
 <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
 <div>
 <div className="flex items-center space-x-2 text-amber-600 text-xs font-semibold uppercase tracking-wider">
 <Sliders className="h-4 w-4"/>
 <span>Financial Controls &amp; Engine Management</span>
 </div>
 <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight mt-1">
 RewardsBank Admin Console
 </h1>
 </div>
 </div>

 {/* Top Metrics Cards */}
 <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
 <div className="bg-white rounded-3xl p-6 shadow-sm">
 <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
 <span>Total Customers</span>
 <Users className="h-5 w-5 text-indigo-500"/>
 </div>
 <div className="text-3xl font-extrabold text-slate-900 mt-2">{users.length}</div>
 <div className="text-xs text-slate-500 mt-1">{referrals.length} referrals processed</div>
 </div>

 <div className="bg-white rounded-3xl p-6 shadow-sm">
 <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
 <span>Total Transaction Volume</span>
 <ArrowLeftRight className="h-5 w-5 text-blue-500"/>
 </div>
 <div className="text-3xl font-extrabold text-slate-900 mt-2">
 ₹{totalVolume.toLocaleString('en-IN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })}
 </div>
 <div className="text-xs text-slate-500 mt-1">{transactions.length} total orders</div>
 </div>

 <div className="bg-white rounded-3xl p-6 shadow-sm">
 <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
 <span>Cashback Disbursed</span>
 <Wallet className="h-5 w-5 text-emerald-500"/>
 </div>
 <div className="text-3xl font-extrabold text-emerald-600 mt-2">
 ₹{totalCashbackDisbursed.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
 </div>
 <div className="text-xs text-slate-500 mt-1">Ledger verified</div>
 </div>

 <div className="bg-white rounded-3xl p-6 shadow-sm">
 <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
 <span>Points Accrued</span>
 <TrendingUp className="h-5 w-5 text-amber-500"/>
 </div>
 <div className="text-3xl font-extrabold text-slate-900 mt-2">
 {totalPointsDisbursed.toLocaleString('en-IN')} <span className="text-sm font-normal text-slate-400">PTS</span>
 </div>
 <div className="text-xs text-slate-500 mt-1">Active across tiers</div>
 </div>
 </div>

 {/* SECTION 1: REWARD RULES ENGINE */}
 {(currentTab === 'admin-dashboard' || currentTab === 'admin-rules') && (
 <div className="bg-white rounded-3xl shadow-sm overflow-hidden">
 <div className="p-6 flex items-center justify-between">
 <div>
 <h2 className="text-lg font-semibold text-slate-900 flex items-center space-x-2">
 <Sliders className="h-5 w-5 text-emerald-600"/>
 <span>Dynamic Reward Rules Engine</span>
 </h2>
 <p className="text-xs text-slate-500">Live evaluation matrix for merchants, categories, and reward caps</p>
 </div>
 <button
 onClick={() => setShowRuleModal(true)}
 className="flex items-center space-x-1.5 px-3.5 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-semibold shadow-sm"
 >
 <Plus className="h-4 w-4"/>
 <span>Create Rule</span>
 </button>
 </div>

 <div className="overflow-x-auto">
 <table className="w-full text-left text-xs">
 <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider">
 <tr>
 <th className="py-3.5 px-4 font-semibold">Category</th>
 <th className="py-3.5 px-4 font-semibold">Merchant Partner</th>
 <th className="py-3.5 px-4 font-semibold">Reward Value</th>
 <th className="py-3.5 px-4 font-semibold">Max Cap</th>
 <th className="py-3.5 px-4 font-semibold">Status</th>
 <th className="py-3.5 px-4 font-semibold text-right">Actions</th>
 </tr>
 </thead>
 <tbody className="divide-y divide-slate-100">
 {rules.map((r) => (
 <tr key={r.id} className="hover:bg-slate-50/60">
 <td className="py-3.5 px-4 font-semibold text-slate-900">{r.category}</td>
 <td className="py-3.5 px-4 text-slate-700">{r.merchant || 'ALL'}</td>
 <td className="py-3.5 px-4 font-bold text-emerald-600">
 {r.rewardType === 'CASHBACK' ? `${r.rewardValue}% Cashback` : `${r.rewardValue}x Points`}
 </td>
 <td className="py-3.5 px-4 text-slate-600">{r.maxReward ? `₹${r.maxReward}` : 'No Cap'}</td>
 <td className="py-3.5 px-4">
 <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
 r.active ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-600'
 }`}>
 {r.active ? 'Active' : 'Disabled'}
 </span>
 </td>
 <td className="py-3.5 px-4 text-right space-x-2">
 <button
 onClick={() => onToggleRule(r.id)}
 className={`p-1.5 rounded-lg transition ${
 r.active ? 'text-amber-600 hover:bg-amber-50' : 'text-emerald-600 hover:bg-emerald-50'
 }`}
 title={r.active ? 'Disable Rule' : 'Enable Rule'}
 >
 <Power className="h-4 w-4"/>
 </button>
 <button
 onClick={() => onDeleteRule(r.id)}
 className="p-1.5 rounded-lg text-rose-600 hover:bg-rose-50 transition"
 title="Delete Rule"
 >
 <Trash2 className="h-4 w-4"/>
 </button>
 </td>
 </tr>
 ))}
 </tbody>
 </table>
 </div>
 </div>
 )}

 {/* SECTION 2: CAMPAIGN MANAGEMENT */}
 {(currentTab === 'admin-dashboard' || currentTab === 'admin-campaigns') && (
 <div className="bg-white rounded-3xl shadow-sm overflow-hidden">
 <div className="p-6 flex items-center justify-between">
 <div>
 <h2 className="text-lg font-semibold text-slate-900 flex items-center space-x-2">
 <Flame className="h-5 w-5 text-amber-500"/>
 <span>Active Campaign Promotions</span>
 </h2>
 <p className="text-xs text-slate-500">Scheduled boosters and time-bound multiplier events</p>
 </div>
 <button
 onClick={() => setShowCampaignModal(true)}
 className="flex items-center space-x-1.5 px-3.5 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-semibold shadow-sm"
 >
 <Plus className="h-4 w-4"/>
 <span>Launch Campaign</span>
 </button>
 </div>

 <div className="overflow-x-auto">
 <table className="w-full text-left text-xs">
 <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider">
 <tr>
 <th className="py-3.5 px-4 font-semibold">Campaign Name</th>
 <th className="py-3.5 px-4 font-semibold">Category / Target</th>
 <th className="py-3.5 px-4 font-semibold">Booster Benefit</th>
 <th className="py-3.5 px-4 font-semibold">Validity Window</th>
 <th className="py-3.5 px-4 font-semibold">Status</th>
 <th className="py-3.5 px-4 font-semibold text-right">Actions</th>
 </tr>
 </thead>
 <tbody className="divide-y divide-slate-100">
 {campaigns.map((c) => (
 <tr key={c.id} className="hover:bg-slate-50/60">
 <td className="py-3.5 px-4">
 <div className="font-semibold text-slate-900">{c.name}</div>
 <div className="text-slate-400 text-[11px]">{c.description}</div>
 </td>
 <td className="py-3.5 px-4 text-slate-700">{c.category} • {c.merchant || 'ALL'}</td>
 <td className="py-3.5 px-4 font-bold text-emerald-600">
 +{c.bonusPercentage}% Extra {c.bonusPoints ? `& +${c.bonusPoints} PTS` : ''}
 </td>
 <td className="py-3.5 px-4 text-slate-500">{c.startDate} to {c.endDate}</td>
 <td className="py-3.5 px-4">
 <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
 c.active ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-600'
 }`}>
 {c.active ? 'Live' : 'Paused'}
 </span>
 </td>
 <td className="py-3.5 px-4 text-right space-x-2">
 <button
 onClick={() => onToggleCampaign(c.id)}
 className="p-1.5 rounded-lg text-amber-600 hover:bg-amber-50 transition"
 >
 <Power className="h-4 w-4"/>
 </button>
 <button
 onClick={() => onDeleteCampaign(c.id)}
 className="p-1.5 rounded-lg text-rose-600 hover:bg-rose-50 transition"
 >
 <Trash2 className="h-4 w-4"/>
 </button>
 </td>
 </tr>
 ))}
 </tbody>
 </table>
 </div>
 </div>
 )}

 {/* SECTION 3: ALL TRANSACTIONS & REFUNDS */}
 {(currentTab === 'admin-dashboard' || currentTab === 'admin-transactions') && (
 <div className="bg-white rounded-3xl shadow-sm overflow-hidden">
 <div className="p-6 flex items-center justify-between">
 <div>
 <h2 className="text-lg font-semibold text-slate-900 flex items-center space-x-2">
 <ArrowLeftRight className="h-5 w-5 text-blue-600"/>
 <span>Transaction &amp; Reversal Ledger</span>
 </h2>
 <p className="text-xs text-slate-500">Audit every purchase across all users with 1-click refund reversal</p>
 </div>
 </div>

 <div className="overflow-x-auto">
 <table className="w-full text-left text-xs">
 <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider">
 <tr>
 <th className="py-3.5 px-4 font-semibold">TXN ID</th>
 <th className="py-3.5 px-4 font-semibold">User</th>
 <th className="py-3.5 px-4 font-semibold">Merchant / Category</th>
 <th className="py-3.5 px-4 font-semibold">Amount</th>
 <th className="py-3.5 px-4 font-semibold">Status</th>
 <th className="py-3.5 px-4 font-semibold text-right">Action</th>
 </tr>
 </thead>
 <tbody className="divide-y divide-slate-100">
 {transactions.map((t) => {
 const userObj = users.find(u => u.id === t.userId);
 const isRefunded = t.status === 'REFUNDED';
 return (
 <tr key={t.id} className="hover:bg-slate-50/60">
 <td className="py-3.5 px-4 font-semibold text-slate-900">TXN-{t.id}</td>
 <td className="py-3.5 px-4">
 <div className="font-semibold text-slate-900">{userObj?.name || `User #${t.userId}`}</div>
 <div className="text-[11px] text-slate-400">{userObj?.tier || 'SILVER'}</div>
 </td>
 <td className="py-3.5 px-4">
 <div className="font-medium text-slate-800">{t.merchantName}</div>
 <div className="text-[11px] text-slate-400">{t.category}</div>
 </td>
 <td className="py-3.5 px-4 font-bold text-slate-900">
 ₹{Number(t.amount).toFixed(2)}
 </td>
 <td className="py-3.5 px-4">
 <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
 isRefunded ? 'bg-rose-100 text-rose-800' : 'bg-emerald-100 text-emerald-800'
 }`}>
 {t.status}
 </span>
 </td>
 <td className="py-3.5 px-4 text-right">
 {!isRefunded ? (
 <button
 onClick={() => onRefundTxn(t.id)}
 className="px-3 py-1 bg-rose-50 hover:bg-rose-100 text-rose-700 font-semibold rounded-lg text-xs transition"
 >
 Refund &amp; Reverse
 </button>
 ) : (
 <span className="text-[11px] font-medium text-slate-400">Reversed</span>
 )}
 </td>
 </tr>
 );
 })}
 </tbody>
 </table>
 </div>
 </div>
 )}

 {/* SECTION 4: USER MANAGEMENT */}
 {(currentTab === 'admin-dashboard' || currentTab === 'admin-users') && (
 <div className="bg-white rounded-3xl shadow-sm overflow-hidden">
 <div className="p-6 flex items-center justify-between">
 <div>
 <h2 className="text-lg font-semibold text-slate-900 flex items-center space-x-2">
 <Users className="h-5 w-5 text-indigo-600"/>
 <span>Customer Accounts</span>
 </h2>
 <p className="text-xs text-slate-500">View user tiers and referral invite codes</p>
 </div>
 <button
 onClick={() => setShowUserModal(true)}
 className="flex items-center space-x-1.5 px-3.5 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-semibold shadow-sm"
 >
 <Plus className="h-4 w-4"/>
 <span>Register User</span>
 </button>
 </div>

 <div className="overflow-x-auto">
 <table className="w-full text-left text-xs">
 <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider">
 <tr>
 <th className="py-3.5 px-4 font-semibold">User</th>
 <th className="py-3.5 px-4 font-semibold">Email</th>
 <th className="py-3.5 px-4 font-semibold">Tier</th>
 <th className="py-3.5 px-4 font-semibold">Referral Code</th>
 <th className="py-3.5 px-4 font-semibold">Joined</th>
 </tr>
 </thead>
 <tbody className="divide-y divide-slate-100">
 {users.map((u) => (
 <tr key={u.id} className="hover:bg-slate-50/60">
 <td className="py-3.5 px-4 font-semibold text-slate-900">{u.name}</td>
 <td className="py-3.5 px-4 text-slate-600">{u.email}</td>
 <td className="py-3.5 px-4">
 <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
 u.tier === 'PLATINUM'
 ? 'bg-purple-100 text-purple-800'
 : u.tier === 'GOLD'
 ? 'bg-amber-100 text-amber-800'
 : 'bg-slate-100 text-slate-800'
 }`}>
 {u.tier}
 </span>
 </td>
 <td className="py-3.5 px-4 font-mono font-semibold text-slate-700">{u.referralCode}</td>
 <td className="py-3.5 px-4 text-slate-400">{new Date(u.createdAt).toLocaleDateString()}</td>
 </tr>
 ))}
 </tbody>
 </table>
 </div>
 </div>
 )}

 {/* CREATE RULE MODAL */}
 {showRuleModal && (
 <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
 <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl space-y-4">
 <h3 className="text-lg font-semibold text-slate-900">Create Reward Rule</h3>
 <div className="space-y-3 text-xs">
 <div>
 <label className="font-semibold text-slate-700 block mb-1">Category</label>
 <select
 className="w-full p-2.5 rounded-xl"
 value={newRule.category}
 onChange={(e) => setNewRule({ ...newRule, category: e.target.value })}
 >
 <option value="GROCERIES">GROCERIES</option>
 <option value="ELECTRONICS">ELECTRONICS</option>
 <option value="DINING">DINING</option>
 <option value="TRAVEL">TRAVEL</option>
 <option value="FUEL">FUEL</option>
 <option value="ALL">ALL</option>
 </select>
 </div>

 <div>
 <label className="font-semibold text-slate-700 block mb-1">Merchant Partner</label>
 <input
 type="text"
 className="w-full p-2.5 rounded-xl"
 value={newRule.merchant}
 onChange={(e) => setNewRule({ ...newRule, merchant: e.target.value })}
 placeholder="e.g. Amazon or ALL"
 />
 </div>

 <div className="grid grid-cols-2 gap-3">
 <div>
 <label className="font-semibold text-slate-700 block mb-1">Reward Value (%)</label>
 <input
 type="number"
 step="0.1"
 className="w-full p-2.5 rounded-xl font-semibold"
 value={newRule.rewardValue}
 onChange={(e) => setNewRule({ ...newRule, rewardValue: Number(e.target.value) })}
 />
 </div>
 <div>
 <label className="font-semibold text-slate-700 block mb-1">Max Cap (₹)</label>
 <input
 type="number"
 className="w-full p-2.5 rounded-xl font-semibold"
 value={newRule.maxReward || ''}
 onChange={(e) => setNewRule({ ...newRule, maxReward: Number(e.target.value) })}
 placeholder="200"
 />
 </div>
 </div>
 </div>

 <div className="flex justify-end space-x-2 pt-3">
 <button
 onClick={() => setShowRuleModal(false)}
 className="px-4 py-2 text-xs font-semibold text-slate-500 hover:bg-slate-100 rounded-xl"
 >
 Cancel
 </button>
 <button
 onClick={async () => {
 await onCreateRule(newRule);
 setShowRuleModal(false);
 }}
 className="px-4 py-2 text-xs font-semibold bg-emerald-600 text-white rounded-xl shadow"
 >
 Save Rule
 </button>
 </div>
 </div>
 </div>
 )}

 {/* CREATE CAMPAIGN MODAL */}
 {showCampaignModal && (
 <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
 <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl space-y-4">
 <h3 className="text-lg font-semibold text-slate-900">Launch Campaign Booster</h3>
 <div className="space-y-3 text-xs">
 <div>
 <label className="font-semibold text-slate-700 block mb-1">Campaign Name</label>
 <input
 type="text"
 className="w-full p-2.5 rounded-xl"
 value={newCampaign.name}
 onChange={(e) => setNewCampaign({ ...newCampaign, name: e.target.value })}
 />
 </div>

 <div>
 <label className="font-semibold text-slate-700 block mb-1">Description</label>
 <input
 type="text"
 className="w-full p-2.5 rounded-xl"
 value={newCampaign.description}
 onChange={(e) => setNewCampaign({ ...newCampaign, description: e.target.value })}
 />
 </div>

 <div className="grid grid-cols-2 gap-3">
 <div>
 <label className="font-semibold text-slate-700 block mb-1">Category</label>
 <select
 className="w-full p-2.5 rounded-xl"
 value={newCampaign.category}
 onChange={(e) => setNewCampaign({ ...newCampaign, category: e.target.value })}
 >
 <option value="GROCERIES">GROCERIES</option>
 <option value="DINING">DINING</option>
 <option value="TRAVEL">TRAVEL</option>
 <option value="ELECTRONICS">ELECTRONICS</option>
 <option value="ALL">ALL</option>
 </select>
 </div>
 <div>
 <label className="font-semibold text-slate-700 block mb-1">Bonus Cashback (%)</label>
 <input
 type="number"
 step="0.5"
 className="w-full p-2.5 rounded-xl font-semibold"
 value={newCampaign.bonusPercentage}
 onChange={(e) => setNewCampaign({ ...newCampaign, bonusPercentage: Number(e.target.value) })}
 />
 </div>
 </div>
 </div>

 <div className="flex justify-end space-x-2 pt-3">
 <button
 onClick={() => setShowCampaignModal(false)}
 className="px-4 py-2 text-xs font-semibold text-slate-500 hover:bg-slate-100 rounded-xl"
 >
 Cancel
 </button>
 <button
 onClick={async () => {
 await onCreateCampaign(newCampaign);
 setShowCampaignModal(false);
 }}
 className="px-4 py-2 text-xs font-semibold bg-amber-600 text-white rounded-xl shadow"
 >
 Launch Campaign
 </button>
 </div>
 </div>
 </div>
 )}

 {/* CREATE USER MODAL */}
 {showUserModal && (
 <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
 <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl space-y-4">
 <h3 className="text-lg font-semibold text-slate-900">Register New Customer</h3>
 <div className="space-y-3 text-xs">
 <div>
 <label className="font-semibold text-slate-700 block mb-1">Full Name</label>
 <input
 type="text"
 className="w-full p-2.5 rounded-xl"
 value={userName}
 onChange={(e) => setUserName(e.target.value)}
 placeholder="e.g. Vikram Malhotra"
 />
 </div>

 <div>
 <label className="font-semibold text-slate-700 block mb-1">Email Address</label>
 <input
 type="email"
 className="w-full p-2.5 rounded-xl"
 value={userEmail}
 onChange={(e) => setUserEmail(e.target.value)}
 placeholder="vikram@example.com"
 />
 </div>

 <div>
 <label className="font-semibold text-slate-700 block mb-1">Loyalty Tier</label>
 <select
 className="w-full p-2.5 rounded-xl font-semibold"
 value={userTier}
 onChange={(e) => setUserTier(e.target.value)}
 >
 <option value="SILVER">SILVER</option>
 <option value="GOLD">GOLD</option>
 <option value="PLATINUM">PLATINUM</option>
 </select>
 </div>
 </div>

 <div className="flex justify-end space-x-2 pt-3">
 <button
 onClick={() => setShowUserModal(false)}
 className="px-4 py-2 text-xs font-semibold text-slate-500 hover:bg-slate-100 rounded-xl"
 >
 Cancel
 </button>
 <button
 onClick={async () => {
 await onCreateUser(userName, userEmail, userTier);
 setShowUserModal(false);
 setUserName('');
 setUserEmail('');
 }}
 className="px-4 py-2 text-xs font-semibold bg-indigo-600 text-white rounded-xl shadow"
 >
 Create Account
 </button>
 </div>
 </div>
 </div>
 )}
 </div>
 );
};
