import React, { useState } from 'react';
import { X, ShoppingBag, ArrowRight } from 'lucide-react';
import { User } from '../types';

interface CreateTransactionModalProps {
 user: User;
 isOpen: boolean;
 onClose: () => void;
 onSubmit: (data: {
 userId: number;
 merchantName: string;
 category: string;
 amount: number;
 description?: string;
 }) => Promise<void>;
}

export const CreateTransactionModal: React.FC<CreateTransactionModalProps> = ({
 user,
 isOpen,
 onClose,
 onSubmit,
}) => {
 const [merchantName, setMerchantName] = useState('Amazon');
 const [category, setCategory] = useState('ELECTRONICS');
 const [amount, setAmount] = useState('2000');
 const [description, setDescription] = useState('Electronics Purchase');
 const [loading, setLoading] = useState(false);

 if (!isOpen) return null;

 const handleSubmit = async (e: React.FormEvent) => {
 e.preventDefault();
 if (!amount || Number(amount) <= 0) {
 alert('Please enter a valid positive amount');
 return;
 }

 try {
 setLoading(true);
 await onSubmit({
 userId: user.id,
 merchantName,
 category,
 amount: Number(amount),
 description,
 });
 onClose();
 } catch (err) {
 alert((err as Error).message || 'Failed to create transaction. Please try again.');
 } finally {
 setLoading(false);
 }
 };

 const presetMerchants = [
 { name: 'Amazon', category: 'ELECTRONICS' },
 { name: 'Swiggy', category: 'DINING' },
 { name: 'XYZ Supermarket', category: 'GROCERIES' },
 { name: 'MakeMyTrip', category: 'TRAVEL' },
 { name: 'Shell Petrol', category: 'FUEL' },
 ];

 return (
 <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
 <div className="bg-white rounded-3xl max-w-md w-full overflow-hidden animate-in fade-in zoom-in-95 duration-200">
 <div className="p-6 bg-slate-900 text-white flex items-center justify-between">
 <div className="flex items-center space-x-3">
 <div className="p-2.5 bg-emerald-500/20 text-emerald-400 rounded-xl">
 <ShoppingBag className="h-6 w-6"/>
 </div>
 <div>
 <h3 className="text-lg font-semibold">Simulate Transaction</h3>
 <p className="text-xs text-slate-400">Trigger Reward Rules for {user.name} ({user.tier})</p>
 </div>
 </div>
 <button onClick={onClose} className="p-2 rounded-full hover:bg-slate-800 text-slate-400 hover:text-white transition">
 <X className="h-5 w-5"/>
 </button>
 </div>

 <form onSubmit={handleSubmit} className="p-6 space-y-4">
 <div>
 <label className="block text-xs font-semibold uppercase tracking-wider text-slate-500 mb-1.5">
 Quick Merchant Presets
 </label>
 <div className="flex flex-wrap gap-2">
 {presetMerchants.map((p) => (
 <button
 type="button"
 key={p.name}
 onClick={() => {
 setMerchantName(p.name);
 setCategory(p.category);
 setDescription(`${p.name} ${p.category} purchase`);
 }}
 className={`px-2.5 py-1 text-xs rounded-lg font-medium transition ${
 merchantName === p.name
 ? 'bg-emerald-50 text-emerald-800 font-semibold'
 : 'bg-slate-50 text-slate-600 hover:bg-slate-100'
 }`}
 >
 {p.name}
 </button>
 ))}
 </div>
 </div>

 <div>
 <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1">
 Merchant Name
 </label>
 <input
 type="text"
 required
 className="w-full px-4 py-2.5 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 text-sm"
 value={merchantName}
 onChange={(e) => setMerchantName(e.target.value)}
 placeholder="e.g. Amazon, Swiggy"
 />
 </div>

 <div className="grid grid-cols-2 gap-3">
 <div>
 <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1">
 Category
 </label>
 <select
 className="w-full px-3 py-2.5 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 text-sm font-medium bg-white"
 value={category}
 onChange={(e) => setCategory(e.target.value)}
 >
 <option value="GROCERIES">GROCERIES</option>
 <option value="ELECTRONICS">ELECTRONICS</option>
 <option value="DINING">DINING</option>
 <option value="TRAVEL">TRAVEL</option>
 <option value="FUEL">FUEL</option>
 <option value="SHOPPING">SHOPPING</option>
 <option value="OTHER">OTHER</option>
 </select>
 </div>

 <div>
 <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1">
 Amount (₹)
 </label>
 <input
 type="number"
 step="0.01"
 min="1"
 required
 className="w-full px-4 py-2.5 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 text-sm font-semibold"
 value={amount}
 onChange={(e) => setAmount(e.target.value)}
 placeholder="2000"
 />
 </div>
 </div>

 <div>
 <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1">
 Description / Notes
 </label>
 <input
 type="text"
 className="w-full px-4 py-2.5 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 text-sm"
 value={description}
 onChange={(e) => setDescription(e.target.value)}
 placeholder="e.g. Sony Headphones"
 />
 </div>

 <div className="pt-2">
 <button
 type="submit"
 disabled={loading}
 className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-xl transition disabled:opacity-50 flex items-center justify-center space-x-2"
 >
 <span>{loading ? 'Executing...' : 'Process Purchase & Calculate Cashback'}</span>
 <ArrowRight className="h-4 w-4"/>
 </button>
 </div>
 </form>
 </div>
 </div>
 );
};
