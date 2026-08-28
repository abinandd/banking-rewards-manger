import React, { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

interface Props {
 children: ReactNode;
}

interface State {
 hasError: boolean;
 error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
 public state: State = {
 hasError: false,
 error: null,
 };

 public static getDerivedStateFromError(error: Error): State {
 return { hasError: true, error };
 }

 public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
 console.error('Uncaught error caught by ErrorBoundary:', error, errorInfo);
 }

 public handleReload = () => {
 window.location.reload();
 };

 public render() {
 if (this.state.hasError) {
 return (
 <div className="min-h-screen bg-slate-900 text-white flex items-center justify-center p-6">
 <div className="max-w-md w-full bg-slate-800 rounded-2xl p-8 text-center">
 <div className="w-16 h-16 bg-rose-500/10 rounded-full flex items-center justify-center mx-auto mb-4 text-rose-400">
 <AlertTriangle className="w-8 h-8"/>
 </div>
 <h2 className="text-xl font-medium mb-2">Something went wrong</h2>
 <p className="text-slate-400 text-sm mb-6">
 {this.state.error?.message || 'An unexpected client error occurred in the application.'}
 </p>
 <button
 onClick={this.handleReload}
 className="inline-flex items-center gap-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-normal rounded-xl transition"
 >
 <RefreshCw className="w-4 h-4"/>
 Reload Application
 </button>
 </div>
 </div>
 );
 }

 return this.props.children;
 }
}
