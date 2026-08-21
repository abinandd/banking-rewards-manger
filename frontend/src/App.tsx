import React, { useEffect, useState } from 'react';
import { 
  UserService, 
  WalletService, 
  TransactionService, 
  RewardService, 
  CampaignService, 
  ReferralService 
} from './services/api';
import { 
  User, 
  RewardWallet, 
  Transaction, 
  Reward, 
  RewardRule, 
  Campaign, 
  Referral 
} from './types';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { CustomerDashboard } from './pages/CustomerDashboard';
import { RewardsWallet } from './pages/RewardsWallet';
import { OffersPage } from './pages/OffersPage';
import { TierPage } from './pages/TierPage';
import { ReferralPage } from './pages/ReferralPage';
import { AdminDashboard } from './pages/AdminDashboard';
import { TransactionDetailModal } from './components/TransactionDetailModal';
import { CreateTransactionModal } from './components/CreateTransactionModal';

export const App: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [wallet, setWallet] = useState<RewardWallet | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [rewards, setRewards] = useState<Reward[]>([]);
  const [rules, setRules] = useState<RewardRule[]>([]);
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [referrals, setReferrals] = useState<Referral[]>([]);

  const [currentTab, setCurrentTab] = useState<string>('dashboard');
  const [isAdmin, setIsAdmin] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);

  // Modals
  const [selectedTxn, setSelectedTxn] = useState<Transaction | null>(null);
  const [showCreateTxnModal, setShowCreateTxnModal] = useState<boolean>(false);

  // Fetch initial data
  const fetchData = async () => {
    try {
      setLoading(true);
      const fetchedUsers = await UserService.getUsers();
      setUsers(fetchedUsers);

      const activeUser = currentUser || fetchedUsers[0];
      if (activeUser) {
        setCurrentUser(activeUser);
        await fetchUserData(activeUser.id);
      }

      const [allRules, allCampaigns] = await Promise.all([
        RewardService.getRules(),
        CampaignService.getCampaigns(false),
      ]);
      setRules(allRules);
      setCampaigns(allCampaigns);
    } catch (err) {
      console.error('Failed to load initial data:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchUserData = async (userId: number) => {
    try {
      const [w, txns, userRewards, userRefs] = await Promise.all([
        WalletService.getBalance(userId),
        TransactionService.getTransactions(userId),
        RewardService.getUserRewards(userId),
        ReferralService.getUserReferrals(userId),
      ]);
      setWallet(w);
      setTransactions(txns);
      setRewards(userRewards);
      setReferrals(userRefs);
    } catch (err) {
      console.error('Failed to load user state:', err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSelectUser = async (user: User) => {
    setCurrentUser(user);
    await fetchUserData(user.id);
  };

  const handleToggleAdmin = async () => {
    const nextState = !isAdmin;
    setIsAdmin(nextState);
    if (nextState) {
      setCurrentTab('admin-dashboard');
      // fetch all global transactions and rewards for admin
      const [allTxns, allRewards, allRefs] = await Promise.all([
        TransactionService.getTransactions(),
        RewardService.getAllRewards(),
        ReferralService.getAllReferrals(),
      ]);
      setTransactions(allTxns);
      setRewards(allRewards);
      setReferrals(allRefs);
    } else {
      setCurrentTab('dashboard');
      if (currentUser) {
        await fetchUserData(currentUser.id);
      }
    }
  };

  const handleCreateTransaction = async (data: {
    userId: number;
    merchantName: string;
    category: string;
    amount: number;
    description?: string;
  }) => {
    await TransactionService.createTransaction(data);
    if (currentUser) {
      await fetchUserData(currentUser.id);
    }
  };

  const handleRefundTransaction = async (txnId: number) => {
    await TransactionService.refundTransaction(txnId);
    if (currentUser) {
      await fetchUserData(currentUser.id);
    }
    // Update selected transaction object if open
    const updated = await TransactionService.getTransactionById(txnId);
    setSelectedTxn(updated);
  };

  const handleUpgradeTier = async (tier: string) => {
    if (!currentUser) return;
    const updated = await UserService.updateTier(currentUser.id, tier);
    setCurrentUser(updated);
    setUsers(users.map(u => u.id === updated.id ? updated : u));
  };

  const handleInviteFriend = async (name: string, email: string) => {
    if (!currentUser) return;
    await ReferralService.inviteFriend({
      referrerUserId: currentUser.id,
      friendName: name,
      friendEmail: email,
    });
    await fetchUserData(currentUser.id);
  };

  const handleCompleteReferral = async (id: number) => {
    await ReferralService.completeReferral(id);
    if (currentUser) {
      await fetchUserData(currentUser.id);
    }
  };

  // Admin handlers
  const handleToggleRule = async (id: number) => {
    await RewardService.toggleRule(id);
    const updatedRules = await RewardService.getRules();
    setRules(updatedRules);
  };

  const handleCreateRule = async (rule: Partial<RewardRule>) => {
    await RewardService.createRule(rule);
    const updatedRules = await RewardService.getRules();
    setRules(updatedRules);
  };

  const handleDeleteRule = async (id: number) => {
    await RewardService.deleteRule(id);
    const updatedRules = await RewardService.getRules();
    setRules(updatedRules);
  };

  const handleToggleCampaign = async (id: number) => {
    await CampaignService.toggleCampaign(id);
    const updated = await CampaignService.getCampaigns(false);
    setCampaigns(updated);
  };

  const handleCreateCampaign = async (c: Partial<Campaign>) => {
    await CampaignService.createCampaign(c);
    const updated = await CampaignService.getCampaigns(false);
    setCampaigns(updated);
  };

  const handleDeleteCampaign = async (id: number) => {
    await CampaignService.deleteCampaign(id);
    const updated = await CampaignService.getCampaigns(false);
    setCampaigns(updated);
  };

  const handleCreateUser = async (name: string, email: string, tier: string) => {
    await UserService.createUser({ name, email, tier });
    const fetchedUsers = await UserService.getUsers();
    setUsers(fetchedUsers);
  };

  if (loading && users.length === 0) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center text-white">
        <div className="text-center space-y-3">
          <div className="h-10 w-10 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="text-sm font-semibold tracking-wider text-slate-300">Connecting to Banking Rewards Monolith...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans text-slate-900">
      <Navbar
        currentUser={currentUser}
        users={users}
        onSelectUser={handleSelectUser}
        isAdmin={isAdmin}
        onToggleAdmin={handleToggleAdmin}
      />

      <div className="flex flex-1">
        <Sidebar
          currentTab={currentTab}
          onSelectTab={setCurrentTab}
          isAdmin={isAdmin}
        />

        <main className="flex-1 max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
          {!isAdmin && currentUser && (
            <>
              {currentTab === 'dashboard' && (
                <CustomerDashboard
                  user={currentUser}
                  wallet={wallet}
                  transactions={transactions}
                  rewards={rewards}
                  onOpenCreateTxn={() => setShowCreateTxnModal(true)}
                  onSelectTab={setCurrentTab}
                  onSelectTxn={(t) => setSelectedTxn(t)}
                />
              )}

              {currentTab === 'wallet' && (
                <RewardsWallet
                  user={currentUser}
                  wallet={wallet}
                  rewards={rewards}
                />
              )}

              {currentTab === 'transactions' && (
                <CustomerDashboard
                  user={currentUser}
                  wallet={wallet}
                  transactions={transactions}
                  rewards={rewards}
                  onOpenCreateTxn={() => setShowCreateTxnModal(true)}
                  onSelectTab={setCurrentTab}
                  onSelectTxn={(t) => setSelectedTxn(t)}
                />
              )}

              {currentTab === 'offers' && (
                <OffersPage
                  campaigns={campaigns}
                  onOpenCreateTxn={() => setShowCreateTxnModal(true)}
                />
              )}

              {currentTab === 'tiers' && (
                <TierPage
                  user={currentUser}
                  wallet={wallet}
                  onUpgradeTier={handleUpgradeTier}
                />
              )}

              {currentTab === 'referrals' && (
                <ReferralPage
                  user={currentUser}
                  referrals={referrals}
                  onInviteFriend={handleInviteFriend}
                  onCompleteReferral={handleCompleteReferral}
                />
              )}
            </>
          )}

          {isAdmin && (
            <AdminDashboard
              currentTab={currentTab}
              users={users}
              transactions={transactions}
              rewards={rewards}
              rules={rules}
              campaigns={campaigns}
              referrals={referrals}
              onToggleRule={handleToggleRule}
              onCreateRule={handleCreateRule}
              onDeleteRule={handleDeleteRule}
              onToggleCampaign={handleToggleCampaign}
              onCreateCampaign={handleCreateCampaign}
              onDeleteCampaign={handleDeleteCampaign}
              onRefundTxn={handleRefundTransaction}
              onCreateUser={handleCreateUser}
            />
          )}
        </main>
      </div>

      {/* Transaction Detail Inspection Modal */}
      {selectedTxn && (
        <TransactionDetailModal
          transaction={selectedTxn}
          rewards={rewards}
          onClose={() => setSelectedTxn(null)}
          onRefund={handleRefundTransaction}
        />
      )}

      {/* Make a Purchase Modal */}
      {currentUser && (
        <CreateTransactionModal
          user={currentUser}
          isOpen={showCreateTxnModal}
          onClose={() => setShowCreateTxnModal(false)}
          onSubmit={handleCreateTransaction}
        />
      )}
    </div>
  );
};

export default App;
