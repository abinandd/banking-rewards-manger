import axios from 'axios';
import { User, RewardWallet, Transaction, Reward, RewardRule, Campaign, Referral } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || (import.meta.env.PROD ? '/api' : 'http://localhost:8080/api');

export const api = axios.create({
 baseURL: API_BASE_URL,
 headers: {
 'Content-Type': 'application/json',
 },
 timeout: 15000,
});

// Request Interceptor: Attach dynamic Request/Correlation IDs for tracing
api.interceptors.request.use(
 (config) => {
 const requestId = crypto.randomUUID ? crypto.randomUUID() : `req-${Date.now()}`;
 config.headers['X-Correlation-Id'] = requestId;
 return config;
 },
 (error) => Promise.reject(error)
);

// Response Interceptor: Provide clean error unwrapping
api.interceptors.response.use(
 (response) => response,
 (error) => {
 const customMessage =
 error.response?.data?.message ||
 error.response?.data?.error ||
 error.message ||
 'An unexpected network or server error occurred';
 return Promise.reject(new Error(customMessage));
 }
);

export const UserService = {
 getUsers: () => api.get<User[]>('/users').then(res => res.data),
 getUserById: (id: number) => api.get<User>(`/users/${id}`).then(res => res.data),
 createUser: (data: { name: string; email: string; tier?: string }) =>
 api.post<User>('/users', data).then(res => res.data),
 updateTier: (id: number, tier: string) =>
 api.patch<User>(`/users/${id}/tier`, { tier }).then(res => res.data),
};

export const WalletService = {
 getBalance: (userId: number) =>
 api.get<RewardWallet>(`/users/${userId}/rewards/balance`).then(res => res.data),
};

export const TransactionService = {
 getTransactions: (userId?: number) =>
 api.get<Transaction[]>('/transactions', { params: { userId } }).then(res => res.data),
 getTransactionById: (id: number) =>
 api.get<Transaction>(`/transactions/${id}`).then(res => res.data),
 createTransaction: (data: {
 userId: number;
 merchantName: string;
 category: string;
 amount: number;
 description?: string;
 }) => api.post<Transaction>('/transactions', data).then(res => res.data),
 refundTransaction: (id: number) =>
 api.post<Transaction>(`/transactions/${id}/refund`).then(res => res.data),
};

export const RewardService = {
 getUserRewards: (userId: number) =>
 api.get<Reward[]>(`/users/${userId}/rewards`).then(res => res.data),
 getAllRewards: () => api.get<Reward[]>('/rewards').then(res => res.data),
 getRules: () => api.get<RewardRule[]>('/rewards/rules').then(res => res.data),
 createRule: (rule: Partial<RewardRule>) =>
 api.post<RewardRule>('/rewards/rules', rule).then(res => res.data),
 toggleRule: (id: number) =>
 api.patch<RewardRule>(`/rewards/rules/${id}/toggle`).then(res => res.data),
 deleteRule: (id: number) =>
 api.delete(`/rewards/rules/${id}`).then(res => res.data),
};

export const CampaignService = {
 getCampaigns: (activeOnly = false) =>
 api.get<Campaign[]>('/campaigns', { params: { activeOnly } }).then(res => res.data),
 createCampaign: (data: Partial<Campaign>) =>
 api.post<Campaign>('/campaigns', data).then(res => res.data),
 toggleCampaign: (id: number) =>
 api.patch<Campaign>(`/campaigns/${id}/toggle`).then(res => res.data),
 deleteCampaign: (id: number) =>
 api.delete(`/campaigns/${id}`).then(res => res.data),
};

export const ReferralService = {
 getUserReferrals: (userId: number) =>
 api.get<Referral[]>(`/referrals/user/${userId}`).then(res => res.data),
 getAllReferrals: () =>
 api.get<Referral[]>('/referrals').then(res => res.data),
 inviteFriend: (data: { referrerUserId: number; friendName: string; friendEmail: string }) =>
 api.post<Referral>('/referrals/invite', data).then(res => res.data),
 completeReferral: (id: number) =>
 api.post<Referral>(`/referrals/${id}/complete`).then(res => res.data),
};
