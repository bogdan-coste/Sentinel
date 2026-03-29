<template>
  <AppCanvas>
    <div class="h-screen w-full text-white font-sans overflow-hidden flex flex-col selection:bg-blue-500/30 relative">

      <AppNavbar />

      <div class="flex-1 w-full max-w-[1600px] mx-auto overflow-hidden px-6 md:px-10 py-8">

        <div v-if="loading" class="flex justify-center items-center h-full w-full">
          <div class="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-400"></div>
        </div>

        <div v-else-if="error" class="flex flex-col justify-center items-center h-full gap-4">
          <div class="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-red-400 text-sm">
            {{ error }}
          </div>
          <button @click="fetchInitialData" class="text-xs font-bold uppercase tracking-widest text-blue-400 hover:text-white transition-colors">Try Again</button>
        </div>

        <div v-else-if="userProfile" class="grid grid-cols-1 lg:grid-cols-4 gap-8 h-full">

          <aside class="md:col-span-1 h-full overflow-y-auto no-scrollbar pb-10 space-y-6 pr-2">

            <div class="bg-white/[0.03] backdrop-blur-xl border border-white/[0.08] rounded-3xl pb-6 overflow-hidden">
              <div class="relative h-32 w-full overflow-hidden border-b border-white/[0.05]">
                <img v-if="userProfile.bannerPicUrl" :src="userProfile.bannerPicUrl" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full bg-gradient-to-br from-[#1B365D] to-[#0A192F]"></div>
                <div class="absolute inset-0 bg-gradient-to-t from-[#0A192F] to-transparent opacity-60"></div>
              </div>

              <div class="px-6 text-center relative flex flex-col items-center">
                <div class="w-24 h-24 rounded-full bg-[#0A192F] border-[3px] border-[#1B365D] overflow-hidden z-10 relative -mt-12 mb-3">
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

                <router-link to="/profile" class="block text-center w-full py-2.5 rounded-xl bg-white/10 border border-white/20 text-white hover:bg-white hover:text-[#0A192F] transition-all duration-300 text-xs font-bold uppercase tracking-widest">
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
              <div v-else class="px-3 py-2 text-white/30 text-xs italic">
                You haven't joined any communities yet.
              </div>
            </div>
          </aside>

          <main class="lg:col-span-2 h-full overflow-y-auto custom-scrollbar flex flex-col gap-6 pr-2 pb-20 pt-2">

            <div v-if="activeFriends.length > 0" class="w-full flex items-start gap-5 overflow-x-auto no-scrollbar shrink-0 min-h-[110px] pt-4 px-2">
              <div v-for="friend in activeFriends" :key="friend.name" class="flex flex-col items-center gap-2 cursor-pointer shrink-0 group">
                <div class="w-16 h-16 rounded-full p-[2px] bg-gradient-to-tr from-blue-300 to-white/50 group-hover:-translate-y-1 transition-transform duration-300">
                  <div class="w-full h-full rounded-full border-2 border-[#0A192F] overflow-hidden bg-[#1B365D]">
                    <img v-if="friend.image" :src="friend.image" class="w-full h-full object-cover" />
                  </div>
                </div>
                <p class="text-[11px] font-medium text-white/70 group-hover:text-white transition-colors">{{ friend.name }}</p>
              </div>
            </div>

            <div class="bg-white/3 backdrop-blur-xl border border-white/[0.08] rounded-2xl p-4 flex items-center gap-4 shrink-0">
              <div class="w-10 h-10 rounded-full bg-[#0A192F] border border-white/10 overflow-hidden shrink-0">
                <img v-if="userProfile.profilePicUrl" :src="userProfile.profilePicUrl" class="w-full h-full object-cover"  alt=""/>
                <div v-else class="w-full h-full bg-white/5 flex items-center justify-center">
                  <svg class="w-5 h-5 text-white/20" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                </div>
              </div>
              <input
                  v-model="newPostContent"
                  type="text"
                  placeholder="What's on your mind?"
                  class="flex-grow bg-white/[0.04] hover:bg-white/[0.06] focus:bg-white/[0.08] border border-white/5 rounded-full py-2.5 px-5 text-sm text-white placeholder-white/50 focus:outline-none focus:border-white/20 transition-all"
              />
              <button @click="handleCreatePost" :disabled="!newPostContent.trim()" class="bg-blue-600 disabled:opacity-30 text-white p-2.5 rounded-full transition-all shrink-0 active:scale-95">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
              </button>
            </div>

            <div v-if="posts.length > 0" class="flex flex-col gap-6">
              <FeedPost
                  v-for="post in posts"
                  :key="post.id"
                  :post="post"
                  :user-profile="userProfile"
              />
            </div>

            <div v-else class="flex flex-col items-center justify-center py-20 text-center bg-white/[0.02] border border-white/[0.05] border-dashed rounded-3xl">
              <div class="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
                <svg class="w-8 h-8 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10l4 4v10a2 2 0 01-2 2z"></path></svg>
              </div>
              <h4 class="text-white/60 font-bold">Your feed is quiet</h4>
              <p class="text-white/30 text-xs mt-1">Follow some friends to see what they're sharing.</p>
            </div>
          </main>

          <RecentActivityPanel />

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
import RecentActivityPanel from "../components/common/RecentActivityPanel.vue";
import FeedPost from "../components/common/BasicPreview/FeedPost.vue";
import type { Post } from "../types";

interface UserProfile {
  username: string;
  name: string;
  profilePicUrl: string | null;
  bannerPicUrl: string | null;
  description: string | null;
  email: string;
  friendCount: number;
}

// Data State
const userProfile = ref<UserProfile | null>(null)
const posts = ref<Post[]>([])
const activeFriends = ref<any[]>([]) // Stories
const communities = ref<any[]>([])
const newPostContent = ref('')

// UI State
const loading = ref(true)
const error = ref<string | null>(null)

const fetchInitialData = async () => {
  loading.value = true
  error.value = null
  try {
    // 1. Fetch Profile
    const profileRes = await api.get('/users/me')
    userProfile.value = profileRes.data

    // 2. Fetch Feed (Update the endpoint path to your actual feed endpoint)
    const feedRes = await api.get('/users/me/user-media')
    // Filter for POST types and map to our frontend Post interface
    posts.value = feedRes.data
        .filter((item: any) => item.type === 'POST')
        .map((item: any) => ({
          id: item.id,
          time: new Date(item.createdAt).toLocaleString(),
          content: item.content || '',
          image: item.imageUrl || null,
          likes: item.likeCount || 0,
          isLiked: item.likedByCurrentUser,
          showComments: false,
          showLikes: false,
        }))

  } catch (err: any) {
    console.error('Dashboard data load failed', err)
    error.value = 'Failed to load dashboard. Please refresh.'
  } finally {
    loading.value = false
  }
}

const handleCreatePost = async () => {
  if (!newPostContent.value.trim()) return
  // Logic to call your create-post endpoint
  console.log('Creating post:', newPostContent.value)
  newPostContent.value = ''
}

onMounted(() => {
  fetchInitialData()
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.2); }

.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
</style>