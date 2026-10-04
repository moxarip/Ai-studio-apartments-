import React, { useState } from 'react';
import { 
  Zap, Video, Film, CheckCircle2, MessageSquare, Settings, 
  Share2, Play, Pause, RefreshCw, Scissors, Sparkles, AlertTriangle 
} from 'lucide-react';

export default function App() {
  const [currentTab, setCurrentTab] = useState<'dashboard' | 'new' | 'review' | 'jobs' | 'chat' | 'exports' | 'settings'>('dashboard');
  const [cropMode, setCropMode] = useState<'subject_aware' | 'center_crop'>('subject_aware');
  const [clipTitle, setClipTitle] = useState('🔥 The Secret AI Hack Nobody Tells You');
  const [isPlaying, setIsPlaying] = useState(true);
  const [chatMessages, setChatMessages] = useState<Array<{role: string; text: string}>>([
    { role: 'assistant', text: 'Hello! I am your ShortsForge Creative Director. How can I assist you with hooks, caption animation, or B-roll today?' }
  ]);
  const [chatInput, setChatInput] = useState('');

  const sendChat = () => {
    if (!chatInput.trim()) return;
    const newMsgs = [...chatMessages, { role: 'user', text: chatInput }];
    setChatMessages(newMsgs);
    setChatInput('');
    setTimeout(() => {
      setChatMessages(prev => [
        ...prev,
        { role: 'assistant', text: '💡 Director Tip: On vertical shorts, placing captions in the bottom 25% zone avoids UI icons while keeping 80%+ attention on the speaker face!' }
      ]);
    }, 600);
  };

  return (
    <div className="min-h-screen bg-[#090D16] text-slate-100 flex flex-col">
      {/* Header */}
      <header className="border-b border-slate-800 bg-[#090D16]/90 backdrop-blur sticky top-0 z-50 px-4 py-3 flex items-center justify-between">
        <div className="flex items-center space-x-3 cursor-pointer" onClick={() => setCurrentTab('dashboard')}>
          <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-indigo-500 to-amber-400 flex items-center justify-center">
            <Zap className="w-4 h-4 text-white fill-white" />
          </div>
          <div>
            <h1 className="font-extrabold text-sm tracking-tight text-white">ShortsForge AI</h1>
            <p className="text-[10px] text-amber-400 font-semibold">Web Studio & Cloud Worker</p>
          </div>
        </div>

        <nav className="flex items-center space-x-1 bg-slate-900 border border-slate-800 p-1 rounded-xl text-xs">
          <button 
            onClick={() => setCurrentTab('dashboard')}
            className={`px-3 py-1.5 rounded-lg font-medium transition ${currentTab === 'dashboard' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'}`}
          >
            Dashboard
          </button>
          <button 
            onClick={() => setCurrentTab('new')}
            className={`px-3 py-1.5 rounded-lg font-medium transition ${currentTab === 'new' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'}`}
          >
            New Project
          </button>
          <button 
            onClick={() => setCurrentTab('review')}
            className={`px-3 py-1.5 rounded-lg font-medium transition ${currentTab === 'review' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'}`}
          >
            Clip Studio
          </button>
          <button 
            onClick={() => setCurrentTab('jobs')}
            className={`px-3 py-1.5 rounded-lg font-medium transition ${currentTab === 'jobs' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'}`}
          >
            Pipeline
          </button>
          <button 
            onClick={() => setCurrentTab('chat')}
            className={`px-3 py-1.5 rounded-lg font-medium transition ${currentTab === 'chat' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'}`}
          >
            AI Copilot
          </button>
          <button 
            onClick={() => setCurrentTab('exports')}
            className={`px-3 py-1.5 rounded-lg font-medium transition ${currentTab === 'exports' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'}`}
          >
            Exports
          </button>
          <button 
            onClick={() => setCurrentTab('settings')}
            className={`px-3 py-1.5 rounded-lg font-medium transition ${currentTab === 'settings' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'}`}
          >
            Settings
          </button>
        </nav>
      </header>

      {/* Main Container */}
      <main className="flex-1 max-w-5xl w-full mx-auto p-4 sm:p-6 space-y-6">
        {/* DASHBOARD */}
        {currentTab === 'dashboard' && (
          <div className="space-y-6">
            <div className="p-6 rounded-2xl bg-gradient-to-r from-slate-900 to-indigo-950 border border-slate-800 space-y-3">
              <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-amber-400/20 text-amber-300 border border-amber-400/30">
                GEMINI 3.1 PRO POWERED
              </span>
              <h2 className="text-2xl font-bold text-white">Full-Stack AI Video-to-Shorts Engine</h2>
              <p className="text-sm text-slate-400 max-w-xl">
                Transform authorized long-form videos into portrait 9:16 shorts with AI highlight extraction, automated subject-aware framing, and animated karaoke captions.
              </p>
              <div className="pt-2">
                <button 
                  onClick={() => setCurrentTab('new')}
                  className="px-5 py-2.5 bg-amber-400 hover:bg-amber-300 text-slate-900 rounded-xl font-bold text-sm shadow transition"
                >
                  Start New Project
                </button>
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4">
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-4">
                <p className="text-xs text-slate-400">Total Projects</p>
                <p className="text-2xl font-black text-white mt-1">4</p>
              </div>
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-4">
                <p className="text-xs text-slate-400">Clips Ready</p>
                <p className="text-2xl font-black text-amber-400 mt-1">12</p>
              </div>
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-4">
                <p className="text-xs text-slate-400">Rendered Shorts</p>
                <p className="text-2xl font-black text-emerald-400 mt-1">7</p>
              </div>
            </div>
          </div>
        )}

        {/* CLIP REVIEW */}
        {currentTab === 'review' && (
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="md:col-span-5 flex justify-center">
              <div className="relative w-64 h-[420px] rounded-3xl bg-black border-2 border-indigo-500/50 shadow-2xl flex flex-col justify-between p-4 overflow-hidden">
                <div className="flex justify-between items-center z-10">
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-black/60 text-amber-300">9:16 HD</span>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-300">
                    {cropMode === 'subject_aware' ? '● Smart Tracking' : 'Center Crop'}
                  </span>
                </div>

                <div 
                  onClick={() => setIsPlaying(!isPlaying)}
                  className="w-12 h-12 rounded-full bg-white/20 self-center backdrop-blur flex items-center justify-center cursor-pointer hover:scale-105 transition"
                >
                  {isPlaying ? <Pause className="w-5 h-5 text-white" /> : <Play className="w-5 h-5 text-white ml-0.5" />}
                </div>

                <div className="z-10 text-center mb-8 px-2">
                  <div className="bg-black/75 rounded-lg p-2 text-xs font-black text-amber-300 uppercase shadow">
                    IF YOU ARE STILL EDITING MANUALLY, YOU ARE WASTING HOURS!
                  </div>
                </div>
              </div>
            </div>

            <div className="md:col-span-7 bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold px-2.5 py-1 rounded bg-amber-400/20 text-amber-300">Viral Score: 94/100</span>
                <span className="text-xs text-slate-400">Duration: 30.0s (00:15 - 00:45)</span>
              </div>

              <div>
                <label className="text-xs text-slate-400 font-semibold block mb-1">Short Title</label>
                <input 
                  type="text" 
                  value={clipTitle}
                  onChange={(e) => setClipTitle(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-3 pt-2">
                <button 
                  onClick={() => setCropMode(prev => prev === 'subject_aware' ? 'center_crop' : 'subject_aware')}
                  className="px-3 py-2 border border-slate-700 bg-slate-800 hover:bg-slate-700 rounded-xl text-xs font-semibold"
                >
                  Framing: {cropMode === 'subject_aware' ? 'Smart Subject' : 'Center Fallback'}
                </button>
                <button 
                  onClick={() => setCurrentTab('jobs')}
                  className="px-3 py-2 bg-amber-400 hover:bg-amber-300 text-slate-900 font-bold rounded-xl text-xs shadow"
                >
                  Render 9:16 Video
                </button>
              </div>
            </div>
          </div>
        )}

        {/* AI CHAT */}
        {currentTab === 'chat' && (
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col h-[480px]">
            <div className="pb-3 border-b border-slate-800">
              <h3 className="font-bold text-sm text-white">ShortsForge Creative Director</h3>
              <p className="text-[11px] text-amber-400">Gemini 3.5 Flash Copilot</p>
            </div>

            <div className="flex-1 overflow-y-auto p-2 space-y-3">
              {chatMessages.map((m, idx) => (
                <div key={idx} className={`flex ${m.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                  <div className={`max-w-md p-3 rounded-2xl text-xs leading-relaxed ${m.role === 'user' ? 'bg-indigo-600 text-white' : 'bg-slate-800 text-slate-200'}`}>
                    {m.text}
                  </div>
                </div>
              ))}
            </div>

            <div className="pt-2 border-t border-slate-800 flex space-x-2">
              <input 
                type="text" 
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && sendChat()}
                placeholder="Ask about viral hooks, pacing, subtitles..."
                className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
              />
              <button onClick={sendChat} className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-xl text-xs font-bold text-white">
                Send
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
