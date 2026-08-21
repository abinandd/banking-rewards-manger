export type UserTier = 'SILVER' | 'GOLD' | 'PLATINUM';

export interface User {
  id: number;
  name: string;
  email: string;
  tier: UserTier;
  referralCode: string;
  createdAt: string;
}

export interface RewardWallet {
  id: number;
  userId: number;
  cashbackBalance: number;
  pointsBalance: number;
  updatedAt: string;
}

export type TransactionStatus = 'COMPLETED' | 'REFUNDED' | 'FAILED';

export interface Transaction {
  id: number;
  userId: number;
  merchantName: string;
  category: string;
  amount: number;
  status: TransactionStatus;
  description?: string;
  createdAt: string;
}

export type RewardType = 'CASHBACK' | 'POINTS';
export type RewardStatus = 'CREDITED' | 'REVERSED' | 'EXPIRED' | 'PENDING';

export interface RewardRuleBreakdown {
  ruleName: string;
  description: string;
  percentage: number;
  calculatedAmount: number;
  calculatedPoints: number;
}

export interface Reward {
  id: number;
  userId: number;
  transactionId?: number;
  type: RewardType;
  amount: number;
  points: number;
  status: RewardStatus;
  description: string;
  breakdownJson?: string;
  expiresAt?: string;
  createdAt: string;
}

export interface RewardRule {
  id: number;
  merchant: string;
  category: string;
  rewardType: RewardType;
  rewardValue: number;
  maxReward?: number;
  minSpend?: number;
  active: boolean;
}

export interface Campaign {
  id: number;
  name: string;
  description: string;
  category: string;
  merchant: string;
  bonusPercentage: number;
  minTransactionAmount?: number;
  maxReward?: number;
  bonusPoints?: number;
  startDate: string;
  endDate: string;
  active: boolean;
}

export interface Referral {
  id: number;
  referrerUserId: number;
  referredUserId?: number;
  referredUserName: string;
  referredUserEmail: string;
  status: 'PENDING' | 'COMPLETED';
  rewardPoints: number;
  createdAt: string;
}
