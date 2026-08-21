import React from 'react';
import { Flame, Clock, ArrowRight } from 'lucide-react';
import { Campaign } from '../types';

interface OffersPageProps {
  campaigns: Campaign[];
  onOpenCreateTxn: () => void;
}

export const OffersPage: React.FC<OffersPageProps> = ({ campaigns, onOpenCreateTxn }) => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
          Special Offers &amp; Boosters 🔥
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Active seasonal promotions and merchant boost campaigns evaluated in real-time by the Reward Engine.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {campaigns.map((c) => (
          <div
            key={c.id}
            className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition flex flex-col justify-between relative overflow-hidden"
          >
            <div className="absolute top-0 right-0 w-24 h-24 bg-gradient-to-br from-amber-400/20 to-emerald-400/20 rounded-bl-full pointer-events-none"></div>

            <div>
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                  {c.category}
                </span>
                <span className="text-xs font-bold text-amber-600 flex items-center space-x-1">
                  <Flame className="h-3.5 w-3.5" />
                  <span>Active Booster</span>
                </span>
              </div>

              <div className="mt-4">
                <h3 className="text-lg font-extrabold text-slate-900 leading-snug">{c.name}</h3>
                <p className="text-xs text-slate-500 mt-2">{c.description}</p>
              </div>

              <div className="mt-5 space-y-2 text-xs text-slate-600 bg-slate-50 p-3 rounded-2xl border border-slate-100">
                <div className="flex justify-between">
                  <span>Bonus Cashback:</span>
                  <span className="font-extrabold text-emerald-600">+{c.bonusPercentage}% Extra</span>
                </div>
                {c.bonusPoints && c.bonusPoints > 0 ? (
                  <div className="flex justify-between">
                    <span>Bonus Points:</span>
                    <span className="font-extrabold text-amber-600">+{c.bonusPoints} PTS</span>
                  </div>
                ) : null}
                {c.minTransactionAmount && (
                  <div className="flex justify-between">
                    <span>Min Spend:</span>
                    <span className="font-semibold text-slate-800">₹{c.minTransactionAmount}</span>
                  </div>
                )}
                {c.maxReward && (
                  <div className="flex justify-between">
                    <span>Cap Limit:</span>
                    <span className="font-semibold text-slate-800">₹{c.maxReward}</span>
                  </div>
                )}
              </div>
            </div>

            <div className="mt-6 pt-4 border-t border-slate-100 flex items-center justify-between">
              <div className="text-[11px] text-slate-400 flex items-center space-x-1">
                <Clock className="h-3.5 w-3.5" />
                <span>Valid till {c.endDate}</span>
              </div>
              <button
                onClick={onOpenCreateTxn}
                className="text-xs font-bold text-emerald-600 hover:text-emerald-700 flex items-center space-x-1"
              >
                <span>Shop Now</span>
                <ArrowRight className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
