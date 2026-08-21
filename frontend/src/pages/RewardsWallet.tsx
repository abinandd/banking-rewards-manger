import React, { useState, useRef } from 'react';
import { Wallet, Award, ArrowUpRight, ArrowDownLeft } from 'lucide-react';
import gsap from 'gsap';
import { useGSAP } from '@gsap/react';
import { RewardWallet, Reward, User } from '../types';

interface RewardsWalletProps {
 user: User;
 wallet: RewardWallet | null;
 rewards: Reward[];
}

export const RewardsWallet: React.FC<RewardsWalletProps> = ({ user: _user, wallet, rewards }) => {
 const [filterType, setFilterType] = useState<string>('ALL');
 const containerRef = useRef<HTMLDivElement>(null);

 useGSAP(() => {
 gsap.from('.gsap-fade-up', {
 y: 20,
 opacity: 0,
 duration: 0.5,
 stagger: 0.1,
 ease: 'power2.out'
 });
 }, { scope: containerRef });

 const filteredRewards = rewards.filter((r) => {
 if (filterType === 'CASHBACK') return r.type === 'CASHBACK';
 if (filterType === 'POINTS') return r.type === 'POINTS';
 if (filterType === 'REVERSED') return r.status === 'REVERSED';
 return true;
 });

 return (
 <div ref={containerRef} className="space-y-6">
 <div className="gsap-fade-up">
 <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">
 Rewards Wallet &amp; Ledger
 </h1>
 <p className="text-sm text-slate-500 mt-1">
 Full double-entry audit history of every cashback credit, points accrual, and refund reversal.
 </p>
 </div>


 {/* Wallet Balances Card */}
 <div className="grid grid-cols-1 md:grid-cols-2 gap-6 gsap-fade-up">
 <div className="bg-white rounded-3xl p-6 flex items-center justify-between">
 <div>
 <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">
 Available Cashback Balance
 </span>
 <div className="text-3xl font-extrabold text-emerald-600 mt-2">
 ₹{wallet ? Number(wallet.cashbackBalance).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) : '0.00'}
 </div>
 <p className="text-xs text-slate-400 mt-1">Directly redeemable to primary bank account</p>
 </div>
 <div className="p-4 bg-emerald-50 text-emerald-600 rounded-2xl">
 <Wallet className="h-8 w-8"/>
 </div>
 </div>

 <div className="bg-white rounded-3xl p-6 flex items-center justify-between">
 <div>
 <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">
 Reward Points Balance
 </span>
 <div className="text-3xl font-extrabold text-slate-900 mt-2">
 {wallet ? Number(wallet.pointsBalance).toLocaleString('en-IN') : '0'} <span className="text-lg font-medium text-slate-400">PTS</span>
 </div>
 <p className="text-xs text-slate-400 mt-1">Valid for gift cards, merchandise, and air miles</p>
 </div>
 <div className="p-4 bg-amber-50 text-amber-600 rounded-2xl">
 <Award className="h-8 w-8"/>
 </div>
 </div>
 </div>

 {/* Rewards Activity Ledger */}
 <div className="bg-white rounded-3xl overflow-hidden gsap-fade-up">
 <div className="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
 <div>
 <h2 className="text-lg font-semibold text-slate-900">Reward Activity History</h2>
 <p className="text-xs text-slate-500">Immutable ledger records showing status, type, and source</p>
 </div>

 <div className="flex items-center space-x-2">
 {['ALL', 'CASHBACK', 'POINTS', 'REVERSED'].map((filter) => (
 <button
 key={filter}
 onClick={() => setFilterType(filter)}
 className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
 filterType === filter
 ? 'bg-slate-900 text-white '
 : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
 }`}
 >
 {filter}
 </button>
 ))}
 </div>
 </div>

 <div className="divide-y divide-slate-100">
 {filteredRewards.length === 0 ? (
 <div className="p-8 text-center text-slate-400 text-sm">
 No reward records found under this filter.
 </div>
 ) : (
 filteredRewards.map((r) => {
 const isReversed = r.status === 'REVERSED';
 const isCashback = r.type === 'CASHBACK';
 const amountNum = Number(r.amount);

 return (
 <div key={r.id} className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50/50">
 <div className="flex items-center space-x-4">
 <div className={`p-3 rounded-2xl ${
 isReversed
 ? 'bg-rose-100 text-rose-600'
 : isCashback
 ? 'bg-emerald-100 text-emerald-600'
 : 'bg-amber-100 text-amber-600'
 }`}>
 {isReversed ? (
 <ArrowDownLeft className="h-5 w-5"/>
 ) : (
 <ArrowUpRight className="h-5 w-5"/>
 )}
 </div>
 <div>
 <div className="flex items-center space-x-2">
 <span className="font-semibold text-slate-900 text-sm">{r.description}</span>
 <span className={`text-[10px] uppercase tracking-wider px-2 py-0.5 rounded-md font-bold ${
 r.status === 'CREDITED'
 ? 'bg-emerald-100 text-emerald-800'
 : r.status === 'REVERSED'
 ? 'bg-rose-100 text-rose-800'
 : 'bg-slate-100 text-slate-800'
 }`}>
 {r.status}
 </span>
 </div>
 <div className="text-xs text-slate-400 mt-1 flex items-center space-x-2">
 <span>Reward #{r.id}</span>
 {r.transactionId && <span>• TXN-{r.transactionId}</span>}
 <span>•</span>
 <span>{new Date(r.createdAt).toLocaleString('en-IN')}</span>
 </div>
 </div>
 </div>

 <div className="text-right">
 {isCashback ? (
 <div className={`text-base font-extrabold ${
 isReversed ? 'text-rose-600' : 'text-emerald-600'
 }`}>
 {amountNum < 0 ? `- ₹${Math.abs(amountNum).toFixed(2)}` : `+ ₹${amountNum.toFixed(2)}`}
 </div>
 ) : (
 <div className={`text-base font-extrabold ${
 isReversed ? 'text-rose-600' : 'text-amber-600'
 }`}>
 {r.points < 0 ? `${r.points} PTS` : `+${r.points} PTS`}
 </div>
 )}
 <span className="text-[11px] font-medium text-slate-400 uppercase tracking-wider">
 {r.type}
 </span>
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
