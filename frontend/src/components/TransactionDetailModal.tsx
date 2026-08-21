import React, { useState } from 'react';
import { 
 X, 
 RotateCcw, 
 CheckCircle, 
 Receipt, 
 Sparkles, 
 AlertCircle
} from 'lucide-react';
import { Transaction, Reward, RewardRuleBreakdown } from '../types';

interface TransactionDetailModalProps {
 transaction: Transaction | null;
 rewards: Reward[];
 onClose: () => void;
 onRefund: (txnId: number) => Promise<void>;
}

export const TransactionDetailModal: React.FC<TransactionDetailModalProps> = ({
 transaction,
 rewards,
 onClose,
 onRefund,
}) => {
 const [loadingRefund, setLoadingRefund] = useState(false);

 if (!transaction) return null;

 const txnRewards = rewards.filter((r) => r.transactionId === transaction.id);
 const cashbackReward = txnRewards.find((r) => r.type === 'CASHBACK');
 const pointsReward = txnRewards.find((r) => r.type === 'POINTS');

 // Parse breakdown JSON
 let breakdowns: RewardRuleBreakdown[] = [];
 if (cashbackReward?.breakdownJson) {
 try {
 breakdowns = JSON.parse(cashbackReward.breakdownJson);
 } catch (e) {
 console.error(e);
 }
 }

 const isRefunded = transaction.status === 'REFUNDED';

 const handleRefund = async () => {
 try {
 setLoadingRefund(true);
 await onRefund(transaction.id);
 } catch (err: any) {
 alert(err?.response?.data?.message || 'Failed to refund transaction');
 } finally {
 setLoadingRefund(false);
 }
 };

 return (
 <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
 <div className="bg-white rounded-3xl max-w-lg w-full overflow-hidden shadow-2xl animate-in fade-in zoom-in-95 duration-200">
 {/* Header */}
 <div className="p-6 bg-slate-900 text-white flex items-center justify-between">
 <div className="flex items-center space-x-3">
 <div className="p-2.5 bg-emerald-500/20 text-emerald-400 rounded-xl">
 <Receipt className="h-6 w-6"/>
 </div>
 <div>
 <h3 className="text-lg font-semibold">Transaction Inspection</h3>
 <p className="text-xs text-slate-400">TXN-{transaction.id} • Audit Ledger Detail</p>
 </div>
 </div>
 <button
 onClick={onClose}
 className="p-2 rounded-full hover:bg-slate-800 text-slate-400 hover:text-white transition"
 >
 <X className="h-5 w-5"/>
 </button>
 </div>

 <div className="p-6 space-y-6 max-h-[80vh] overflow-y-auto">
 {/* Main Transaction Summary */}
 <div className="flex items-center justify-between pb-5">
 <div>
 <div className="text-2xl font-extrabold text-slate-900">
 ₹{Number(transaction.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
 </div>
 <div className="flex items-center space-x-2 mt-1 text-xs text-slate-500">
 <span className="font-medium text-slate-800">{transaction.merchantName}</span>
 <span>•</span>
 <span className="bg-slate-100 px-2 py-0.5 rounded text-slate-700 font-medium">
 {transaction.category}
 </span>
 </div>
 </div>
 <div>
 {isRefunded ? (
 <span className="px-3 py-1 bg-rose-100 text-rose-700 text-xs font-semibold rounded-full">
 REFUNDED &amp; REVERSED
 </span>
 ) : (
 <span className="px-3 py-1 bg-emerald-100 text-emerald-700 text-xs font-semibold rounded-full">
 COMPLETED
 </span>
 )}
 </div>
 </div>

 {/* Engine Multi-Rule Breakdown */}
 <div className="space-y-3">
 <div className="flex items-center space-x-2 text-slate-800 font-semibold text-sm">
 <Sparkles className="h-4 w-4 text-emerald-600"/>
 <span>Reward Engine Calculation Breakdown</span>
 </div>

 {breakdowns.length > 0 ? (
 <div className="bg-slate-50 rounded-2xl p-4 space-y-3">
 {breakdowns.map((b, idx) => (
 <div key={idx} className="flex items-start justify-between text-xs pb-2 last:-0 last:pb-0">
 <div>
 <div className="font-semibold text-slate-800">{b.ruleName}</div>
 <div className="text-slate-500 text-[11px]">{b.description}</div>
 </div>
 <div className="text-right">
 {b.calculatedAmount > 0 && (
 <div className="font-bold text-emerald-600">
 +₹{Number(b.calculatedAmount).toFixed(2)}
 {b.percentage > 0 && <span className="text-[10px] text-slate-400 ml-1">({b.percentage}%)</span>}
 </div>
 )}
 {b.calculatedPoints > 0 && (
 <div className="font-bold text-amber-600">
 +{b.calculatedPoints} PTS
 </div>
 )}
 </div>
 </div>
 ))}
 </div>
 ) : (
 <div className="text-xs text-slate-500 bg-slate-50 p-3 rounded-xl">
 Base reward rules were evaluated for this purchase.
 </div>
 )}

 {/* Total Reward Outcome */}
 <div className="bg-gradient-to-r from-emerald-50 to-teal-50 rounded-2xl p-4 flex items-center justify-between">
 <div>
 <span className="text-xs font-semibold text-emerald-900">Total Net Reward</span>
 <p className="text-[11px] text-emerald-700">
 {isRefunded ? 'Original credit was automatically reversed' : 'Credited directly to wallet'}
 </p>
 </div>
 <div className="text-right">
 {cashbackReward && (
 <div className={`text-base font-extrabold ${isRefunded ? 'text-rose-600 line-through' : 'text-emerald-700'}`}>
 ₹{Number(cashbackReward.amount).toFixed(2)} Cashback
 </div>
 )}
 {pointsReward && (
 <div className={`text-xs font-semibold ${isRefunded ? 'text-rose-600 line-through' : 'text-amber-700'}`}>
 +{pointsReward.points} Points
 </div>
 )}
 </div>
 </div>
 </div>

 {/* Refund Actions */}
 <div className="pt-2">
 {!isRefunded ? (
 <div className="space-y-3">
 <div className="bg-amber-50 p-3 rounded-xl text-xs text-amber-800 flex items-start space-x-2">
 <AlertCircle className="h-4 w-4 text-amber-600 flex-shrink-0 mt-0.5"/>
 <span>
 Simulating a refund will mark transaction as REFUNDED, deduct the original cashback from the user's wallet, and record an immutable REVERSED ledger audit entry.
 </span>
 </div>
 <button
 onClick={handleRefund}
 disabled={loadingRefund}
 className="w-full flex items-center justify-center space-x-2 py-3 px-4 bg-rose-600 hover:bg-rose-700 text-white font-semibold rounded-xl shadow-md transition active:scale-98 disabled:opacity-50"
 >
 <RotateCcw className="h-4 w-4"/>
 <span>{loadingRefund ? 'Processing Refund...' : 'Refund Transaction & Reverse Reward'}</span>
 </button>
 </div>
 ) : (
 <div className="bg-rose-50 p-4 rounded-2xl text-center text-xs text-rose-800 space-y-1">
 <div className="font-semibold flex items-center justify-center space-x-1">
 <CheckCircle className="h-4 w-4 text-rose-600"/>
 <span>Transaction Refunded</span>
 </div>
 <p>₹{Number(transaction.amount).toFixed(2)} refunded • Cashback &amp; Points reversed in ledger.</p>
 </div>
 )}
 </div>
 </div>
 </div>
 </div>
 );
};
