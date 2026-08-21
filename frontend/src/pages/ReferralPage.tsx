import React, { useState, useRef } from 'react';
import gsap from 'gsap';
import { useGSAP } from '@gsap/react';
import { Gift, Copy, Check, UserPlus, CheckCircle2, Clock } from 'lucide-react';
import { User, Referral } from '../types';

interface ReferralPageProps {
 user: User;
 referrals: Referral[];
 onInviteFriend: (friendName: string, friendEmail: string) => Promise<void>;
 onCompleteReferral: (id: number) => Promise<void>;
}

export const ReferralPage: React.FC<ReferralPageProps> = ({
 user,
 referrals,
 onInviteFriend,
 onCompleteReferral,
}) => {
 const [copied, setCopied] = useState(false);
 const [friendName, setFriendName] = useState('');
 const [friendEmail, setFriendEmail] = useState('');
 const [loading, setLoading] = useState(false);
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

 const handleCopy = () => {
 navigator.clipboard.writeText(user.referralCode || 'REWARD500ABC');
 setCopied(true);
 setTimeout(() => setCopied(false), 2000);
 };

 const handleInvite = async (e: React.FormEvent) => {
 e.preventDefault();
 if (!friendName || !friendEmail) return;

 try {
 setLoading(true);
 await onInviteFriend(friendName, friendEmail);
 setFriendName('');
 setFriendEmail('');
 } catch (err) {
 alert((err as Error).message || 'Failed to send invite. Please try again.');
 } finally {
 setLoading(false);
 }
 };

 return (
 <div ref={containerRef} className="space-y-6">
 <div className="gsap-fade-up">
 <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">
 Invite Friends &amp; Earn
 </h1>
 <p className="text-sm text-slate-500 mt-1">
 Share your referral link. Earn 500 bonus reward points for every successful joining.
 </p>
 </div>

 {/* Hero Referral Code Card */}
 <div className="bg-gradient-to-br from-emerald-600 to-teal-800 rounded-3xl p-8 text-white flex flex-col md:flex-row md:items-center justify-between gap-6 relative overflow-hidden gsap-fade-up">
 <div>
 <div className="flex items-center space-x-2 text-emerald-200">
 <Gift className="h-5 w-5"/>
 <span className="text-xs font-semibold uppercase tracking-wider">Your Referral Code</span>
 </div>
 <div className="text-3xl sm:text-4xl font-extrabold mt-2 tracking-widest bg-white/10 px-4 py-2 rounded-2xl -white/20 inline-block">
 {user.referralCode || 'REWARD500ABC'}
 </div>
 <p className="text-xs text-emerald-100 mt-2">
 Friends get 250 bonus points on first card spend.
 </p>
 </div>

 <button
 onClick={handleCopy}
 className="flex items-center justify-center space-x-2 px-6 py-3 bg-white text-emerald-800 hover:bg-emerald-50 font-semibold rounded-2xl transition active:scale-95"
 >
 {copied ? <Check className="h-5 w-5 text-emerald-600"/> : <Copy className="h-5 w-5"/>}
 <span>{copied ? 'Code Copied!' : 'Copy Referral Code'}</span>
 </button>
 </div>

 {/* Invite Form & History Grid */}
 <div className="grid grid-cols-1 md:grid-cols-3 gap-6 gsap-fade-up">
 {/* Invite Form */}
 <div className="bg-white rounded-3xl p-6 md:col-span-1">
 <h2 className="text-base font-semibold text-slate-900 flex items-center space-x-2">
 <UserPlus className="h-5 w-5 text-emerald-600"/>
 <span>Send Direct Invite</span>
 </h2>
 <p className="text-xs text-slate-500 mt-1 mb-4">
 Invite colleagues directly by entering their contact details.
 </p>

 <form onSubmit={handleInvite} className="space-y-3">
 <div>
 <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1">
 Friend's Name
 </label>
 <input
 type="text"
 required
 className="w-full px-3.5 py-2 text-sm rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500"
 value={friendName}
 onChange={(e) => setFriendName(e.target.value)}
 placeholder="e.g. Rahul Verma"
 />
 </div>

 <div>
 <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1">
 Friend's Email
 </label>
 <input
 type="email"
 required
 className="w-full px-3.5 py-2 text-sm rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500"
 value={friendEmail}
 onChange={(e) => setFriendEmail(e.target.value)}
 placeholder="rahul@example.com"
 />
 </div>

 <button
 type="submit"
 disabled={loading}
 className="w-full py-2.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-xl transition active:scale-95 disabled:opacity-50 mt-2"
 >
 {loading ? 'Sending...' : 'Send Invitation (+500 PTS)'}
 </button>
 </form>
 </div>

 {/* Referrals Table */}
 <div className="bg-white rounded-3xl md:col-span-2 overflow-hidden flex flex-col justify-between">
 <div>
 <div className="p-6">
 <h2 className="text-base font-semibold text-slate-900">Your Referrals &amp; Payouts</h2>
 <p className="text-xs text-slate-500">Track registration milestones and points credited</p>
 </div>

 <div className="divide-y divide-slate-100">
 {referrals.length === 0 ? (
 <div className="p-8 text-center text-slate-400 text-sm">
 No friends invited yet. Start sharing to earn 500 bonus points!
 </div>
 ) : (
 referrals.map((ref) => (
 <div key={ref.id} className="p-4 flex items-center justify-between hover:bg-slate-50/50">
 <div className="flex items-center space-x-3">
 <div className="h-9 w-9 rounded-xl bg-slate-100 flex items-center justify-center font-semibold text-slate-700 text-sm">
 {ref.referredUserName.charAt(0)}
 </div>
 <div>
 <div className="text-sm font-semibold text-slate-900">{ref.referredUserName}</div>
 <div className="text-xs text-slate-400">{ref.referredUserEmail}</div>
 </div>
 </div>

 <div className="flex items-center space-x-4">
 {ref.status === 'COMPLETED' ? (
 <div className="text-right">
 <span className="px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-800 text-xs font-semibold flex items-center space-x-1">
 <CheckCircle2 className="h-3 w-3"/>
 <span>Joined (+500 PTS)</span>
 </span>
 </div>
 ) : (
 <div className="flex items-center space-x-2">
 <span className="px-2.5 py-0.5 rounded-full bg-amber-100 text-amber-800 text-xs font-semibold flex items-center space-x-1">
 <Clock className="h-3 w-3"/>
 <span>Pending</span>
 </span>
 <button
 onClick={() => onCompleteReferral(ref.id)}
 className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-medium rounded-lg"
 >
 Simulate Join
 </button>
 </div>
 )}
 </div>
 </div>
 ))
 )}
 </div>
 </div>
 </div>
 </div>
 </div>
 );
};
