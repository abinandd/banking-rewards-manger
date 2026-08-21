import React, { useState, useRef } from 'react';
import gsap from 'gsap';
import { useGSAP } from '@gsap/react';
import { Award, CheckCircle2, Zap } from 'lucide-react';
import { User, RewardWallet } from '../types';

interface TierPageProps {
  user: User;
  wallet: RewardWallet | null;
  onUpgradeTier: (tier: string) => Promise<void>;
}

export const TierPage: React.FC<TierPageProps> = ({ user, wallet, onUpgradeTier }) => {
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

  const currentPoints = wallet ? wallet.pointsBalance : 0;
  const targetPoints = user.tier === 'SILVER' ? 5000 : user.tier === 'GOLD' ? 10000 : 25000;
  const progress = Math.min(100, Math.round((currentPoints / targetPoints) * 100));

  const tiers = [
    {
      name: 'SILVER',
      multiplier: '1x Points',
      cashbackBonus: 'Base 1.0%',
      minPoints: 0,
      perks: ['Standard cashback rate', '1 Point per ₹10 spent', 'Quarterly offers access'],
    },
    {
      name: 'GOLD',
      multiplier: '2x Points Boost',
      cashbackBonus: '+0.5% Loyalty Booster',
      minPoints: 5000,
      perks: ['2.5% effective cashback on Groceries', '2x Points acceleration', 'Priority transaction clearing', 'Exclusive dining discounts'],
    },
    {
      name: 'PLATINUM',
      multiplier: '3x Points Boost',
      cashbackBonus: '+1.0% Elite Booster',
      minPoints: 10000,
      perks: ['Up to 5% cashback on Travel & Airlines', '3x Points on all merchants', 'Zero forex markup waiver', 'Dedicated relationship rewards manager'],
    },
  ];

  const handleUpgrade = async (tier: string) => {
    try {
      setLoading(true);
      await onUpgradeTier(tier);
    } catch (err: any) {
      alert(err?.response?.data?.message || 'Failed to update tier');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div ref={containerRef} className="space-y-6">
      <div className="gsap-fade-up">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
          Rewards Tier &amp; Membership Status
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Your loyalty tier unlocks higher percentage boosters across the Reward Engine.
        </p>
      </div>

      {/* Hero Tier Card */}
      <div className="bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950 rounded-3xl p-8 text-white shadow-2xl relative overflow-hidden gsap-fade-up">
        <div className="absolute top-0 right-0 w-80 h-80 bg-amber-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="flex items-center space-x-2 text-amber-400">
              <Award className="h-5 w-5" />
              <span className="text-xs font-bold uppercase tracking-widest">Active Membership</span>
            </div>
            <div className="text-3xl sm:text-4xl font-black mt-2 tracking-tight">
              {user.tier} TIER MEMBER
            </div>
            <p className="text-xs text-slate-400 mt-1">
              Account: {user.name} ({user.email})
            </p>
          </div>

          <div className="bg-white/5 backdrop-blur-md p-5 rounded-2xl border border-white/10 w-full md:w-80">
            <div className="flex justify-between text-xs font-bold text-slate-300 mb-2">
              <span>Tier Progress</span>
              <span>{currentPoints} / {targetPoints} PTS</span>
            </div>
            <div className="w-full bg-slate-800 rounded-full h-3 overflow-hidden p-0.5 border border-slate-700">
              <div
                className="bg-gradient-to-r from-amber-400 to-emerald-400 h-full rounded-full transition-all duration-500"
                style={{ width: `${progress}%` }}
              ></div>
            </div>
            <div className="text-[11px] text-slate-400 mt-2 text-right">
              {Math.max(0, targetPoints - currentPoints)} points to next milestone
            </div>
          </div>
        </div>
      </div>

      {/* Tier Comparison Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 gsap-fade-up">
        {tiers.map((t) => {
          const isCurrent = user.tier === t.name;
          return (
            <div
              key={t.name}
              className={`rounded-3xl p-6 border transition flex flex-col justify-between ${
                isCurrent
                  ? 'bg-white border-2 border-emerald-500 shadow-lg shadow-emerald-500/10'
                  : 'bg-white border-slate-200 shadow-sm'
              }`}
            >
              <div>
                <div className="flex items-center justify-between">
                  <span className="text-lg font-black text-slate-900">{t.name}</span>
                  {isCurrent && (
                    <span className="px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold flex items-center space-x-1">
                      <CheckCircle2 className="h-3.5 w-3.5" />
                      <span>Current</span>
                    </span>
                  )}
                </div>

                <div className="mt-4 p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-1">
                  <div className="text-sm font-extrabold text-emerald-700">{t.cashbackBonus}</div>
                  <div className="text-xs font-bold text-slate-600">{t.multiplier}</div>
                </div>

                <div className="mt-5 space-y-2.5">
                  {t.perks.map((p, idx) => (
                    <div key={idx} className="flex items-start space-x-2 text-xs text-slate-600">
                      <Zap className="h-3.5 w-3.5 text-amber-500 flex-shrink-0 mt-0.5" />
                      <span>{p}</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-100">
                {!isCurrent ? (
                  <button
                    onClick={() => handleUpgrade(t.name)}
                    disabled={loading}
                    className="w-full py-2.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold rounded-xl transition active:scale-95 disabled:opacity-50"
                  >
                    Switch to {t.name}
                  </button>
                ) : (
                  <div className="text-center text-xs font-bold text-emerald-600 py-1">
                    ✓ Active on this account
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
