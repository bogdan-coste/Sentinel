<template>
  <AppCanvas>
    <div class="h-screen w-full text-white font-sans overflow-hidden flex flex-col">

      <AppNavbar />

      <div class="flex-1 w-full max-w-[1600px] mx-auto overflow-hidden px-6 md:px-10 py-8">

        <div v-if="loading" class="flex justify-center items-center h-full w-full">
          <div class="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-white/80"></div>
        </div>

        <div v-else-if="error" class="text-red-400 text-center p-4 bg-red-900/20 rounded-xl border border-red-900/30">
          {{ error }}
        </div>

        <div v-else-if="userProfile" class="grid grid-cols-1 lg:grid-cols-4 gap-8 h-full">

          <aside class="md:col-span-1 h-full overflow-y-auto no-scrollbar pb-10 space-y-6 pr-2">

            <div class="bg-white/[0.03] backdrop-blur-xl border border-white/[0.08] rounded-3xl pb-6 overflow-hidden shadow-2xl">
              <div class="relative h-32 w-full overflow-hidden border-b border-white/[0.05]">
                <img v-if="userProfile.bannerPicUrl" :src="userProfile.bannerPicUrl" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full bg-gradient-to-br from-[#1B365D] to-[#0A192F]"></div>
                <div class="absolute inset-0 bg-gradient-to-t from-[#0A192F] to-transparent opacity-60"></div>
              </div>

              <div class="px-6 text-center relative flex flex-col items-center">
                <div class="w-24 h-24 rounded-full bg-[#0A192F] border-[3px] border-[#1B365D] overflow-hidden shadow-xl z-10 relative -mt-12 mb-3">
                  <img v-if="userProfile.profilePicUrl" :src="userProfile.profilePicUrl" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full bg-white/5 flex items-center justify-center">
                    <svg class="w-10 h-10 text-white/20" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                  </div>
                </div>

                <h2 class="text-xl font-bold text-white/95">{{ userProfile.name }}</h2>
                <p class="text-white/50 text-xs font-medium tracking-wide mb-3">@{{ userProfile.username }}</p>

                <div class="flex items-center gap-1.5 bg-white/5 border border-white/10 px-3 py-1 rounded-full mb-5">
                  <svg class="w-3.5 h-3.5 text-blue-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                  <span class="text-xs font-semibold text-white/90">{{ userProfile.friendCount || 0 }} <span class="text-white/60 font-normal">Friends</span></span>
                </div>

                <p class="text-white/80 text-sm mb-6 px-2 leading-relaxed" v-if="userProfile.description">
                  {{ userProfile.description }}
                </p>
                <div v-else class="flex flex-col items-center justify-center mb-6 gap-2">
                  <svg class="w-5 h-5 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path></svg>
                  <p class="text-white/40 text-xs italic">No bio provided.</p>
                </div>

                <router-link to="/profile" class="block text-center w-full py-2.5 rounded-xl bg-white/10 border border-white/20 text-white hover:bg-white hover:text-[#0A192F] hover:shadow-[0_0_20px_rgba(255,255,255,0.2)] transition-all duration-300 text-xs font-bold uppercase tracking-widest">
                  My Profile
                </router-link>
              </div>
            </div>

            <div class="space-y-4 pt-2">
              <h3 class="text-sm font-semibold px-2 mb-3 text-white/80 uppercase tracking-widest flex items-center gap-2">
                <svg class="w-4 h-4 text-blue-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
                Communities
              </h3>
              <div v-if="communities.length > 0" class="space-y-3">
                <div v-for="community in communities" :key="community.name" class="flex items-center gap-3 px-3 py-2 rounded-xl hover:bg-white/5 transition-colors cursor-pointer group">
                  <div class="w-10 h-10 rounded-lg bg-black/20 border border-white/5 flex-shrink-0 overflow-hidden group-hover:border-white/30 transition-colors">
                    <img v-if="community.icon" :src="community.icon" class="w-full h-full object-cover" />
                  </div>
                  <div>
                    <p class="font-medium text-sm text-white/90">{{ community.name }}</p>
                    <p class="text-white/40 text-[10px] mt-0.5 uppercase tracking-wider">{{ community.memberCount }} members</p>
                  </div>
                </div>
              </div>
              <div v-else class="flex flex-row items-center gap-3 px-3 py-2 text-white/40 text-xs italic">
                <div class="w-10 h-10 rounded-lg bg-white/5 border border-white/5 border-dashed flex items-center justify-center">
                  <svg class="w-5 h-5 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                </div>
                <p>Not part of any communities.</p>
              </div>
            </div>
          </aside>

          <main class="lg:col-span-2 h-full overflow-y-auto custom-scrollbar flex flex-col gap-6 pr-2 pb-20 pt-2">

            <div v-if="headerUsers.length > 0" class="w-full flex items-start gap-5 overflow-x-auto no-scrollbar shrink-0 min-h-[110px] pt-4 px-2">
              <div v-for="user in headerUsers" :key="user.name" class="flex flex-col items-center gap-2 cursor-pointer shrink-0 group">
                <div class="w-16 h-16 rounded-full p-[2px] bg-gradient-to-tr from-blue-300 to-white/50 group-hover:-translate-y-1 transition-transform duration-300 shadow-md">
                  <div class="w-full h-full rounded-full border-2 border-[#0A192F] overflow-hidden bg-[#1B365D]">
                    <img v-if="user.image" :src="user.image" class="w-full h-full object-cover" />
                  </div>
                </div>
                <p class="text-[11px] font-medium text-white/70 group-hover:text-white transition-colors">{{ user.name }}</p>
              </div>
            </div>

            <div class="bg-white/[0.03] backdrop-blur-xl border border-white/[0.08] rounded-2xl p-4 flex items-center gap-4 shadow-lg shrink-0">
              <div class="w-10 h-10 rounded-full bg-[#0A192F] border border-white/10 overflow-hidden shrink-0">
                <img v-if="userProfile.profilePicUrl" :src="userProfile.profilePicUrl" class="w-full h-full object-cover"  alt=""/>
              </div>
              <input
                  type="text"
                  placeholder="What's on your mind?"
                  class="flex-grow bg-white/[0.04] hover:bg-white/[0.06] focus:bg-white/[0.08] border border-white/5 rounded-full py-2.5 px-5 text-sm text-white placeholder-white/50 focus:outline-none focus:border-white/20 transition-all"
              />
              <button class="bg-blue-500/20 text-blue-400 border border-blue-500/30 hover:bg-blue-500 hover:text-white p-2.5 rounded-full shadow-lg transition-all shrink-0">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
              </button>
            </div>

            <div v-for="post in mockPosts" :key="post.id" class="bg-white/[0.03] backdrop-blur-xl border border-white/[0.08] rounded-2xl p-5 shadow-lg flex flex-col gap-4 shrink-0 hover:bg-white/[0.05] hover:border-white/[0.15] transition-all duration-300">

              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-white/5 border border-white/10 overflow-hidden shrink-0">
                    <img :src="post.avatar" class="w-full h-full object-cover" />
                  </div>
                  <div>
                    <h4 class="text-sm font-bold text-white/95">{{ post.author }}</h4>
                    <p class="text-[11px] text-white/50">{{ post.time }}</p>
                  </div>
                </div>
                <button class="text-white/40 hover:text-white transition-colors p-1.5 rounded-full hover:bg-white/10">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z"></path></svg>
                </button>
              </div>

              <p class="text-[15px] text-white/80 leading-relaxed tracking-wide">
                {{ post.content }}
              </p>

              <div class="flex items-center gap-2 pt-3 border-t border-white/[0.05]">
                <button class="flex items-center gap-2 text-white/50 hover:text-blue-300 hover:bg-blue-500/10 transition-colors text-xs font-semibold px-4 py-2 rounded-full">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path></svg>
                  {{ post.likes }}
                </button>
                <button class="flex items-center gap-2 text-white/50 hover:text-white hover:bg-white/10 transition-colors text-xs font-semibold px-4 py-2 rounded-full">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path></svg>
                  {{ post.comments }}
                </button>
              </div>
            </div>
          </main>

          <aside class="md:col-span-1 h-full overflow-y-auto no-scrollbar pb-10 pr-2 hidden lg:block">
            <div class="bg-white/[0.03] backdrop-blur-xl border border-white/[0.08] rounded-3xl p-6 shadow-xl">
              <h3 class="text-sm font-semibold mb-6 text-white/80 uppercase tracking-widest flex items-center gap-2">
                <svg class="w-4 h-4 text-blue-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path></svg>
                Recent Activity
              </h3>

              <div v-if="activities.length > 0" class="space-y-4">
                <div v-for="activity in activities" :key="activity.id" class="group flex flex-col gap-3 p-3.5 rounded-2xl bg-white/[0.02] hover:bg-white/[0.06] transition-colors border border-transparent hover:border-white/5">
                  <div class="flex items-start gap-3">
                    <div class="relative shrink-0">
                      <div class="w-9 h-9 rounded-full bg-white/5 overflow-hidden border border-white/10">
                        <img v-if="activity.avatar" :src="activity.avatar" class="w-full h-full object-cover" />
                      </div>
                      <div v-if="activity.isVerified" class="absolute -bottom-1 -right-1 w-4 h-4 bg-white rounded-full flex items-center justify-center border-2 border-[#0A192F]">
                        <svg class="w-2.5 h-2.5 text-[#0A192F]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path></svg>
                      </div>
                    </div>
                    <div>
                      <span class="font-semibold text-sm block text-white/95">{{ activity.user }}</span>
                      <span class="text-white/50 text-[11px]">{{ activity.action || 'Sent a friend request' }} • {{ activity.time }}</span>
                    </div>
                  </div>

                  <div class="flex items-center justify-end gap-2 mt-1">
                    <button class="text-white/50 text-xs font-semibold hover:text-white px-2 transition-colors">
                      Decline
                    </button>
                    <button class="bg-white/10 text-white border border-white/10 text-[10px] font-bold px-4 py-1.5 rounded-full uppercase tracking-wider hover:bg-white hover:text-[#0A192F] transition-all shadow-md">
                      Accept
                    </button>
                  </div>
                </div>
              </div>

              <div v-else class="flex flex-col items-center justify-center py-8 text-center">
                <svg class="w-10 h-10 text-white/20 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path></svg>
                <p class="text-white/40 text-xs italic">All caught up. No new activity.</p>
              </div>
            </div>
          </aside>

        </div>
      </div>
    </div>
  </AppCanvas>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from "../service/api.ts";
import AppCanvas from "../components/common/AppCanvas.vue";
import AppNavbar from "../components/common/AppNavbar.vue";

interface UserProfile {
  username: string;
  name: string;
  profilePicUrl: string;
  bannerPicUrl: string;
  description: string;
  email: string;
  friendCount: number;
}

const userProfile = ref<UserProfile | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

// Header Stories Dummy Data
const headerUsers = ref<Array<{name: string, image: string}>>([
  { name: 'Alice', image: 'https://i.pravatar.cc/150?img=5' },
  { name: 'Bob', image: 'https://i.pravatar.cc/150?img=11' },
])

// Mock Posts Data
const mockPosts = ref([
  {
    id: 1,
    author: 'Elena Rossi',
    avatar: 'https://i.pravatar.cc/150?img=47',
    time: '2 hours ago',
    content: 'Just finished setting up the new Vue 3 dashboard! The glassmorphism effects are looking incredibly crisp over this new blue gradient. ✨',
    likes: 42,
    comments: 5
  },
  {
    id: 2,
    author: 'David Chen',
    avatar: 'https://i.pravatar.cc/150?img=14',
    time: '5 hours ago',
    content: 'Has anyone tried integrating Tailwind with the latest Vite build? Seeing some amazing performance improvements on local development.',
    likes: 18,
    comments: 2
  },
  {
    id: 3,
    author: 'Sarah Jenkins',
    avatar: 'https://i.pravatar.cc/150?img=32',
    time: '1 day ago',
    content: 'Here is a reminder to take a break and stretch! Coding for 8 hours straight is not a badge of honor. Stay healthy friends. 🧘‍♂️☕',
    likes: 112,
    comments: 18
  },
  {
    id: 4,
    author: 'Marcus Johnson',
    avatar: 'https://i.pravatar.cc/150?img=59',
    time: '2 days ago',
    content: 'Just deployed my first full-stack application to production! The feeling of seeing real users interact with your code is absolutely unmatched.',
    likes: 89,
    comments: 12
  }
])

const communities = ref<Array<{name: string, memberCount: number, icon?: string}>>([])
const activities = ref<Array<{id: number, user: string, action?: string, time: string, avatar?: string, isVerified?: boolean}>>([])

const fetchUserProfile = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await api.get('/users/me')
    userProfile.value = response.data
  } catch (err: any) {
    console.error('Failed to fetch user profile', err)
    error.value = err.message || 'Failed to load profile'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
/* Custom scrollbar applied to the middle feed */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
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

/* Utility to hide scrollbar but keep scroll functionality for sidebars */
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}
</style>