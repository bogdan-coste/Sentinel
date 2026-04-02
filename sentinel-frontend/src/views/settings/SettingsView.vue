<template>
  <AppCanvas>
    <div class="h-screen w-full text-white font-sans overflow-hidden flex flex-col selection:bg-blue-500/30">

      <AppNavbar />

      <div class="flex-1 w-full max-w-[1200px] mx-auto overflow-hidden flex flex-row gap-8 px-4 lg:px-8 py-6">

        <aside class="w-[280px] xl:w-[300px] h-full flex flex-col shrink-0 bg-white/[0.02] backdrop-blur-2xl rounded-[32px] shadow-2xl border border-white/[0.05] overflow-hidden hidden md:flex">

          <div class="p-6 pb-4 border-b border-white/[0.05] shrink-0">
            <h2 class="text-2xl font-black text-white tracking-tight">Settings</h2>
          </div>

          <div class="p-5 border-b border-white/[0.05] flex items-center gap-3">
            <img src="https://i.pravatar.cc/150?img=11" class="w-12 h-12 rounded-full object-cover border border-white/10" />
            <div class="flex flex-col">
              <span class="text-[14px] font-bold text-white/95">Bogdan Coste</span>
              <span class="text-[11px] text-blue-400 font-medium">@bogdan_coste</span>
            </div>
          </div>

          <nav class="flex-1 overflow-y-auto custom-scrollbar p-4 flex flex-col gap-1.5">
            <button
                v-for="tab in tabs"
                :key="tab.id"
                @click="activeTab = tab.id"
                class="flex items-center gap-3 px-4 py-3.5 rounded-[20px] transition-all duration-200 border border-transparent text-left w-full group"
                :class="activeTab === tab.id ? 'bg-white/[0.06] border-white/10 shadow-lg text-white' : 'text-white/50 hover:bg-white/[0.03] hover:text-white'"
            >
              <svg class="w-5 h-5 transition-transform group-hover:scale-110" :class="activeTab === tab.id ? 'text-blue-400' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-html="tab.icon"></svg>
              <span class="text-[14px] font-semibold">{{ tab.label }}</span>
            </button>
          </nav>
        </aside>

        <main class="flex-1 h-full flex flex-col bg-white/[0.02] backdrop-blur-2xl rounded-[32px] shadow-2xl border border-white/[0.05] overflow-hidden relative">

          <div class="h-20 shrink-0 w-full px-8 flex items-center border-b border-white/[0.05] bg-white/[0.01]">
            <button class="md:hidden mr-4 p-2 rounded-full bg-white/5 hover:bg-white/10 active:scale-95 text-white/70">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path></svg>
            </button>
            <h3 class="text-xl font-bold text-white tracking-wide">{{ activeTabTitle }}</h3>
          </div>

          <div class="flex-1 overflow-y-auto custom-scrollbar p-8 flex flex-col gap-8 relative">

            <div v-if="activeTab === 'account'" class="space-y-8 animate-fade-in max-w-2xl">

              <div class="flex items-center gap-6">
                <div class="relative group w-20 h-20 shrink-0">
                  <div class="w-full h-full rounded-full bg-[#0A192F] overflow-hidden border border-white/20">
                    <img src="https://i.pravatar.cc/150?img=11" class="w-full h-full object-cover" />
                  </div>
                  <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 rounded-full flex items-center justify-center cursor-pointer transition-opacity backdrop-blur-sm">
                    <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"></path></svg>
                  </div>
                </div>
                <div>
                  <button class="px-5 py-2.5 rounded-full bg-white/10 hover:bg-white/20 transition-all text-[13px] font-bold text-white mb-2 shadow-sm">Change Avatar</button>
                  <p class="text-[11px] text-white/40">JPG, GIF or PNG. Max size of 5MB.</p>
                </div>
              </div>

              <div class="space-y-5">
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Display Name</label>
                  <input v-model="form.name" type="text" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                  <div>
                    <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Username</label>
                    <div class="relative">
                      <span class="absolute left-4 top-1/2 -translate-y-1/2 text-white/30 font-bold">@</span>
                      <input v-model="form.username" type="text" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 pl-9 pr-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
                    </div>
                  </div>
                  <div>
                    <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Email Address</label>
                    <input v-model="form.email" type="email" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
                  </div>
                </div>

                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Bio</label>
                  <textarea v-model="form.bio" rows="4" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all resize-none custom-scrollbar"></textarea>
                </div>
              </div>
            </div>

            <div v-else-if="activeTab === 'privacy'" class="space-y-8 animate-fade-in max-w-2xl">

              <div class="space-y-6">
                <div>
                  <label class="block text-white/90 text-[15px] font-semibold mb-1">Default Post Visibility</label>
                  <p class="text-[12px] text-white/50 mb-3">Control who can see your future posts by default.</p>
                  <div class="relative w-full md:w-2/3">
                    <select v-model="form.postVisibility" class="w-full bg-white/[0.02] hover:bg-white/[0.04] border border-white/[0.05] rounded-2xl py-3.5 pl-5 pr-10 text-[14px] text-white focus:outline-none appearance-none cursor-pointer">
                      <option value="Public" class="bg-[#0F203B]">Public - Everyone can see</option>
                      <option value="Friends" class="bg-[#0F203B]">Friends - Only friends</option>
                      <option value="Private" class="bg-[#0F203B]">Private - Only me</option>
                    </select>
                    <svg class="w-4 h-4 absolute right-4 top-1/2 -translate-y-1/2 text-white/40 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                  </div>
                </div>

                <div class="w-full h-px bg-white/[0.05]"></div>

                <div class="flex items-center justify-between">
                  <div>
                    <h4 class="text-white/90 text-[15px] font-semibold mb-1">Show Activity Status</h4>
                    <p class="text-[12px] text-white/50">Let friends know when you're online.</p>
                  </div>
                  <div
                      @click="form.activityStatus = !form.activityStatus"
                      class="w-12 h-6 rounded-full cursor-pointer transition-colors duration-300 ease-in-out relative flex items-center px-1 shrink-0"
                      :class="form.activityStatus ? 'bg-blue-500' : 'bg-white/10'"
                  >
                    <div
                        class="w-4 h-4 bg-white rounded-full shadow-md transition-transform duration-300 ease-in-out"
                        :class="form.activityStatus ? 'translate-x-6' : 'translate-x-0'"
                    ></div>
                  </div>
                </div>

                <div class="flex items-center justify-between">
                  <div>
                    <h4 class="text-white/90 text-[15px] font-semibold mb-1">Search Engine Indexing</h4>
                    <p class="text-[12px] text-white/50">Allow search engines to link to your profile.</p>
                  </div>
                  <div
                      @click="form.searchIndex = !form.searchIndex"
                      class="w-12 h-6 rounded-full cursor-pointer transition-colors duration-300 ease-in-out relative flex items-center px-1 shrink-0"
                      :class="form.searchIndex ? 'bg-blue-500' : 'bg-white/10'"
                  >
                    <div
                        class="w-4 h-4 bg-white rounded-full shadow-md transition-transform duration-300 ease-in-out"
                        :class="form.searchIndex ? 'translate-x-6' : 'translate-x-0'"
                    ></div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="activeTab === 'security'" class="space-y-8 animate-fade-in max-w-2xl">
              <div class="space-y-5">
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Current Password</label>
                  <input type="password" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
                </div>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                  <div>
                    <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">New Password</label>
                    <input type="password" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
                  </div>
                  <div>
                    <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Confirm Password</label>
                    <input type="password" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
                  </div>
                </div>
                <div class="pt-2">
                  <button class="px-6 py-2.5 rounded-full bg-white/10 hover:bg-white/20 transition-all text-[13px] font-bold text-white shadow-sm">Update Password</button>
                </div>
              </div>

              <div class="w-full h-px bg-white/[0.05]"></div>

              <div>
                <h4 class="text-red-400 text-[15px] font-bold mb-2">Danger Zone</h4>
                <div class="bg-red-500/10 border border-red-500/20 rounded-2xl p-5 flex items-center justify-between">
                  <div>
                    <h5 class="text-white/90 text-[14px] font-bold mb-1">Delete Account</h5>
                    <p class="text-white/50 text-[12px]">Once you delete your account, there is no going back.</p>
                  </div>
                  <button class="px-5 py-2.5 rounded-full bg-red-500/20 text-red-400 border border-red-500/30 hover:bg-red-500 hover:text-white transition-all text-[12px] font-bold shrink-0">
                    Delete Account
                  </button>
                </div>
              </div>
            </div>

            <div v-else class="flex flex-col items-center justify-center py-20 text-center">
              <svg class="w-12 h-12 text-white/10 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path></svg>
              <span class="text-[13px] font-semibold text-white/40 tracking-wider uppercase">{{ activeTabTitle }} configuration coming soon.</span>
            </div>

          </div>

          <div class="p-6 border-t border-white/[0.05] bg-black/20 shrink-0 flex items-center justify-between">
            <span class="text-white/40 text-[11px]">Unsaved changes</span>
            <div class="flex gap-3">
              <button class="px-6 py-2.5 text-[13px] font-bold text-white/50 hover:text-white transition-colors">Reset</button>
              <button class="px-8 py-2.5 rounded-full bg-blue-600 hover:bg-blue-500 text-white transition-all text-[13px] font-bold shadow-lg active:scale-95 flex items-center gap-2">
                Save Changes
              </button>
            </div>
          </div>

        </main>
      </div>

    </div>
  </AppCanvas>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import AppNavbar from "../../layouts/AppNavbar.vue"
import AppCanvas from "../../layouts/AppCanvas.vue"

// Tab configuration
const tabs = [
  { id: 'account', label: 'Account', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>' },
  { id: 'privacy', label: 'Privacy & Safety', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>' },
  { id: 'security', label: 'Security', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>' },
  { id: 'notifications', label: 'Notifications', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>' },
  { id: 'appearance', label: 'Appearance', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01"></path>' }
]

const activeTab = ref('account')

// Dynamic title for the header
const activeTabTitle = computed(() => {
  return tabs.find(t => t.id === activeTab.value)?.label || 'Settings'
})

// Form Data State
const form = reactive({
  name: 'Bogdan Coste',
  username: 'bogdan_coste',
  email: 'hello@bogdan.com',
  bio: 'Software Engineer @ Sentinel. Crafting beautiful web experiences.',
  postVisibility: 'Friends',
  activityStatus: true,
  searchIndex: false
})
</script>

<style scoped>
/* Minimalist scrollbar for settings form area */
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}

.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

/* Simple fade animation for tab switching */
.animate-fade-in {
  animation: fadeIn 0.15s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>