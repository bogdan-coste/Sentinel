<template>
  <AppCanvas>
    <div class="h-screen w-full text-white font-sans overflow-hidden flex flex-col selection:bg-blue-500/30 relative">

      <AppNavbar />

      <div class="flex-1 w-full max-w-360 mx-auto overflow-hidden flex flex-col lg:flex-row gap-8 px-4 lg:px-8 py-6">

        <div v-if="loading" class="flex justify-center items-center w-full h-full">
          <div class="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-400"></div>
        </div>

        <aside v-if="guestProfile && !loading" class="w-full lg:w-[320px] xl:w-85 h-full flex flex-col gap-5 shrink-0 pt-2 overflow-y-auto no-scrollbar pb-6">

          <div class="bg-[#0A192F]/40 backdrop-blur-2xl rounded-[40px] flex flex-col relative border border-white/5 shrink-0 transition-all z-10">

            <div class="w-full relative h-36 shrink-0 rounded-t-[40px] overflow-visible">
              <img v-if="guestProfile.bannerPicUrl" :src="guestProfile.bannerPicUrl" class="w-full h-full object-cover rounded-t-[40px]"  alt=""/>
              <div v-else class="w-full h-full bg-gradient-to-br from-blue-900/40 to-indigo-900/40 rounded-t-[40px]"></div>

              <div class="absolute inset-x-0 bottom-0 h-16 bg-gradient-to-t from-[#09152a] via-[#09152a]/50 to-transparent pointer-events-none z-0"></div>

              <div class="absolute -bottom-10 left-1/2 -translate-x-1/2 z-10 cursor-pointer" @click="viewImage('avatar')">
                <img v-if="guestProfile.profilePicUrl" :src="guestProfile.profilePicUrl" class="w-24 h-24 rounded-full border-[6px] border-[#09152a] object-cover hover:opacity-90 transition-opacity"  alt=""/>
                <div v-else class="w-24 h-24 rounded-full border-[6px] border-[#09152a] bg-[#0A192F] flex items-center justify-center hover:opacity-90 transition-opacity">
                  <svg class="w-10 h-10 text-white/20" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                </div>
              </div>
            </div>

            <div class="px-6 pb-8 flex flex-col items-center text-center relative z-20 mt-12">

              <div class="flex flex-col items-center gap-1 mb-6">
                <span class="text-[10px] font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-indigo-400 tracking-[0.25em] uppercase">
                  @{{ guestProfile.username }}
                </span>
                <h2 class="text-[28px] font-black text-white tracking-tight leading-tight">
                  {{ guestProfile.name }}
                </h2>
              </div>

              <div class="flex items-center justify-around w-full bg-black/30 border border-white/3 rounded-[28px] py-5 mb-6">
                <div class="flex-1 flex flex-col items-center">
                  <span class="text-2xl font-black text-white tracking-tighter">{{ guestProfile.friendCount || 0 }}</span>
                  <span class="text-[9px] text-white/30 uppercase tracking-[0.2em] font-bold mt-0.5">Friends</span>
                </div>
                <div class="w-px h-8 bg-white/5"></div>
                <div class="flex-1 flex flex-col items-center">
                  <span class="text-2xl font-black text-white tracking-tighter">{{ guestProfile.mutualCount || 0 }}</span>
                  <span class="text-[9px] text-white/30 uppercase tracking-[0.2em] font-bold mt-0.5">Mutual</span>
                </div>
              </div>

              <div class="flex gap-2 w-full mb-6">

                <button
                    @click="toggleFriendStatus"
                    :disabled="isTogglingFriend"
                    :class="[
                    'flex-1 py-3.5 rounded-full border text-[11px] font-bold tracking-[0.2em] uppercase transition-all flex items-center justify-center gap-2',
                    isTogglingFriend ? 'opacity-50 cursor-not-allowed' : 'active:scale-95',
                    friendshipState === 'none' ? 'bg-blue-600 hover:bg-blue-500 border-transparent text-white' :
                    friendshipState === 'pending' ? 'bg-white/10 hover:bg-white/15 border-white/10 text-white/80' :
                    'bg-white/5 hover:bg-white/10 border-white/10 text-white/90'
                  ]"
                >
                  <div v-if="isTogglingFriend" class="w-3.5 h-3.5 border-2 border-white/40 border-t-white rounded-full animate-spin"></div>
                  <template v-else>
                    <svg v-if="friendshipState === 'friends'" class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"></path></svg>
                    <svg v-else-if="friendshipState === 'none'" class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4"></path></svg>
                  </template>
                  {{ isTogglingFriend ? 'Updating...' : friendButtonText }}
                </button>

                <button class="w-12 flex items-center justify-center rounded-full border border-white/8 bg-white/2 hover:bg-white/6 text-white/90 transition-all active:scale-95 shrink-0" title="Message">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path></svg>
                </button>
              </div>

              <p v-if="guestProfile.description" class="text-white/60 text-[13px] leading-relaxed w-full px-4 mb-6 font-light italic">
                "{{ guestProfile.description }}"
              </p>

              <div v-if="canViewDetails" class="flex flex-wrap justify-center gap-2 mb-8">
                <div v-if="guestProfile.role" class="px-4 py-2 rounded-full bg-white/3 border border-white/5 flex items-center gap-2 text-[11px] font-semibold text-white/60 hover:text-white transition-colors cursor-default backdrop-blur-sm">
                  <div class="w-1.5 h-1.5 rounded-full bg-blue-500"></div>
                  {{ guestProfile.role }}
                </div>
                <div v-if="guestProfile.location" class="px-4 py-2 rounded-full bg-white/3 border border-white/5 flex items-center gap-2 text-[11px] font-semibold text-white/60 hover:text-white transition-colors cursor-default backdrop-blur-sm">
                  <svg class="w-3.5 h-3.5 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path></svg>
                  {{ guestProfile.location }}
                </div>
              </div>

              <div class="w-full" v-if="canViewFriends">
                <ConnectionsPanel
                    :friends="friends"
                    :total-count="guestProfile.friendCount || friends.length"
                    @refresh-friends="fetchGuestData(guestProfile.id)"
                />
              </div>

            </div>
          </div>
        </aside>

        <main class="flex-1 h-full flex flex-col overflow-hidden max-w-3xl pt-2">

          <div class="w-full flex-1 overflow-y-auto custom-scrollbar flex flex-col gap-6 pb-20 pr-2 pt-1">

            <div class="w-full flex flex-col gap-5 shrink-0">
              <div class="w-full flex items-center gap-8 border-b border-white/5 pb-0 px-2">
                <button class="text-[13px] font-extrabold text-white relative pb-3 tracking-wide">
                  Timeline
                  <div class="absolute -bottom-px left-0 w-full h-0.75 bg-gradient-to-r from-blue-500 to-indigo-500 rounded-t-full"></div>
                </button>
                <button class="text-[13px] font-bold text-white/40 hover:text-white/80 transition-colors pb-3 tracking-wide">Media</button>
                <button class="text-[13px] font-bold text-white/40 hover:text-white/80 transition-colors pb-3 tracking-wide">Photos</button>
              </div>
            </div>

            <div v-if="!canViewContent" class="flex flex-col items-center justify-center py-24 px-4 text-center rounded-4xl bg-[#0A192F]/40 border border-white/5 shrink-0 backdrop-blur-2xl mt-4">
              <div class="w-20 h-20 rounded-full bg-black/40 border border-white/5 flex items-center justify-center mb-6 shadow-inner">
                <svg class="w-8 h-8 text-white/40" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
              </div>
              <h3 class="text-[18px] font-bold text-white tracking-tight mb-2">This profile is private</h3>
              <p class="text-[13px] text-white/50 max-w-xs">
                Add {{ guestProfile?.name }} as a friend to see their posts, photos, and updates.
              </p>
            </div>

            <template v-else>
              <div v-if="profilePosts.length > 0" class="flex flex-col gap-6 mt-4">
                <FeedPost
                    v-for="post in profilePosts"
                    :key="post.id"
                    :post="post"
                    :user-profile="guestProfile || undefined"
                    @toggle-menu="toggleMenu"
                    @view-image="viewImage"
                    @open-preview="previewImage = $event"
                />
              </div>

              <div v-else class="flex flex-col items-center justify-center py-24 text-center rounded-4xl bg-white/1 border border-white/3 shrink-0 border-dashed backdrop-blur-sm mt-4">
                <div class="w-16 h-16 rounded-3xl bg-white/2 border border-white/5 flex items-center justify-center mb-4 transform rotate-3">
                  <svg class="w-8 h-8 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10l4 4v10a2 2 0 01-2 2z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 11v5m0 0l-3-3m3 3l3-3"></path></svg>
                </div>
                <span class="text-[13px] font-bold tracking-wide text-white/40">No posts yet</span>
              </div>
            </template>

          </div>
        </main>

        <aside class="w-full lg:w-75 xl:w-[320px] h-full flex flex-col gap-6 shrink-0 pt-2 overflow-y-auto no-scrollbar pb-6 relative z-10">

          <div v-if="canViewFriends" class="bg-[#0A192F]/40 backdrop-blur-2xl border border-white/5 rounded-4xl p-5 shrink-0">
            <div class="flex items-center justify-between mb-5">
              <h3 class="text-[11px] font-extrabold text-white/80 tracking-widest uppercase">Mutual Friends</h3>
            </div>

            <div v-if="mutualFriends.length > 0" class="flex flex-col gap-1">
              <div v-for="person in mutualFriends" :key="person.name" class="flex items-center justify-between group p-2 -mx-2 rounded-2xl hover:bg-white/3 transition-all cursor-pointer">
                <div class="flex items-center gap-3">
                  <div class="relative">
                    <img :src="person.image" class="w-10 h-10 rounded-full object-cover border border-white/10"  alt=""/>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-[13px] font-bold text-white/90 group-hover:text-blue-400 transition-colors">{{ person.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-4 text-[12px] text-white/40">
              No mutual friends
            </div>
          </div>

          <div v-if="canViewContent" class="bg-[#0A192F]/40 backdrop-blur-2xl border border-white/5 rounded-4xl p-5 shrink-0">
            <div class="flex items-center justify-between mb-5">
              <h3 class="text-[11px] font-extrabold text-white/80 tracking-widest uppercase">Photos</h3>
              <button class="text-[10px] text-blue-400 font-bold hover:text-blue-300 transition-colors uppercase tracking-wider">See All</button>
            </div>

            <div v-if="recentPhotos.length > 0" class="grid grid-cols-2 gap-2">
              <div v-for="(photo, index) in recentPhotos" :key="index" class="aspect-square rounded-2xl overflow-hidden border border-white/5 cursor-pointer hover:opacity-80 transition-opacity">
                <img :src="photo" class="w-full h-full object-cover"  alt=""/>
              </div>
            </div>
            <div v-else class="w-full aspect-video rounded-2xl bg-black/20 border border-white/5 flex flex-col items-center justify-center gap-2">
              <svg class="w-6 h-6 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
              <span class="text-[9px] text-white/30 uppercase tracking-widest font-bold">No photos yet</span>
            </div>
          </div>

        </aside>

      </div>

      <PostPreview
          v-if="previewImage"
          :preview-image="previewImage"
          :user-profile="guestProfile || undefined"
          @close="closePreview"
      />

    </div>
  </AppCanvas>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from "../service/api.ts"
import type { Post } from '../types'
import AppCanvas from "../components/common/AppCanvas.vue";
import AppNavbar from "../components/common/AppNavbar.vue";
import FeedPost from "../components/common/BasicPreview/FeedPost.vue";
import PostPreview from "../components/common/PostPreview/PostPreview.vue";
import ConnectionsPanel from "../components/common/ConnectionsPanel.vue";

interface GuestProfile {
  id: string;
  username: string;
  name: string;
  profilePicUrl: string | null;
  bannerPicUrl: string | null;
  description: string | null;
  role?: string;
  location?: string;
  friendCount: number;
  mutualCount: number;
  privacy: {
    timeline: 'Public' | 'Friends' | 'Only Me';
    friendsList: 'Public' | 'Friends' | 'Only Me';
    details: 'Public' | 'Friends' | 'Only Me';
  }
}

const route = useRoute()
const router = useRouter()

const guestProfile = ref<GuestProfile | null>(null)
const loading = ref(true)
const profilePosts = ref<Post[]>([])
const friends = ref<any[]>([])
const mutualFriends = ref<any[]>([])

const isTogglingFriend = ref(false)

const friendshipState = ref<'none' | 'pending' | 'friends'>('none')
const previewImage = ref<any>(null)
const activeMenu = ref<number | null>(null)

const canViewContent = computed(() => {
  if (!guestProfile.value) return false
  if (guestProfile.value.privacy?.timeline === 'Public') return true
  return friendshipState.value === 'friends'
})

const canViewFriends = computed(() => {
  if (!guestProfile.value) return false
  if (guestProfile.value.privacy?.friendsList === 'Public') return true
  return friendshipState.value === 'friends'
})

const canViewDetails = computed(() => {
  if (!guestProfile.value) return false
  if (guestProfile.value.privacy?.details === 'Public') return true
  return friendshipState.value === 'friends'
})

const friendButtonText = computed(() => {
  if (friendshipState.value === 'friends') return 'Friends'
  if (friendshipState.value === 'pending') return 'Requested'
  return 'Add Friend'
})

const recentPhotos = computed(() => {
  return profilePosts.value
      .map(post => post.image)
      .filter(image => image !== null)
      .slice(0, 4)
})

const toggleFriendStatus = async () => {
  if (!guestProfile.value || isTogglingFriend.value) return;

  const publicId = guestProfile.value.id; // Using the mapped publicId
  isTogglingFriend.value = true;

  try {
    if (friendshipState.value === 'none') {
      await api.post('/friends/add-friend', { publicId });
      friendshipState.value = 'pending';
    }
    else if (friendshipState.value === 'pending') {
      await api.delete('/friends/remove-friend', { data: { publicId } });
      friendshipState.value = 'none';
    }
    else if (friendshipState.value === 'friends') {
      if (confirm(`Remove ${guestProfile.value.name} from your friends?`)) {
        await api.delete('/friends/remove-friend', { data: { publicId } });
        friendshipState.value = 'none';

        guestProfile.value.friendCount = Math.max(0, guestProfile.value.friendCount - 1);
      }
    }
  } catch (error) {
    console.error('Failed to update friendship status', error);
  } finally {
    isTogglingFriend.value = false;
  }
}

const toggleMenu = (postId: number) => {
  activeMenu.value = activeMenu.value === postId ? null : postId
}

const viewImage = (type: 'avatar' | 'banner') => {
  if (type === 'avatar' && guestProfile.value?.profilePicUrl) {
    previewImage.value = { url: guestProfile.value.profilePicUrl, type: 'avatar' }
  } else if (type === 'banner' && guestProfile.value?.bannerPicUrl) {
    previewImage.value = { url: guestProfile.value.bannerPicUrl, type: 'banner' }
  }
}

const closePreview = () => {
  previewImage.value = null;
}

const fetchGuestData = async (publicId: string) => {
  loading.value = true
  try {
    const response = await api.get(`/users/${publicId}`);
    const data = response.data;

    const userInfo = data.userInformation;
    guestProfile.value = {
      id: userInfo.publicId || publicId,
      username: userInfo.username,
      name: userInfo.name,
      profilePicUrl: userInfo.profilePicUrl || null,
      bannerPicUrl: userInfo.bannerPicUrl || null,
      description: userInfo.description || null,
      role: userInfo.role || '',
      location: userInfo.location || '',
      friendCount: userInfo.friendCount || 0,
      mutualCount: userInfo.mutualCount || 0,
      privacy: userInfo.privacy || {
        timeline: 'Friends',
        friendsList: 'Friends',
        details: 'Public'
      }
    };

    const dbStatus = data.friendshipStatus;

    if (dbStatus === 'ACCEPTED' || data.friends) {
      friendshipState.value = 'friends';

      const mappedFriends = data.friends ? data.friends.map((f: any) => ({
        publicId: f.publicId,
        username: f.username,
        name: f.name,
        image: f.profilePicUrl || undefined
      })) : [];

      friends.value = mappedFriends;
      mutualFriends.value = mappedFriends;
    }
    else if (dbStatus === 'PENDING') {
      friendshipState.value = 'pending';
      friends.value = [];
      mutualFriends.value = [];
    }
    else {
      friendshipState.value = 'none';
      friends.value = [];
      mutualFriends.value = [];
    }

    if (data.media) {
      profilePosts.value = data.media
          .filter((item: any) => item.type === 'POST' || !item.type)
          .map((item: any) => ({
            id: item.id,
            time: item.createdAt ? new Date(item.createdAt).toLocaleString() : 'Recently',
            content: item.content || '',
            image: item.url || item.imageUrl || null,
            likes: item.likesCount || item.likeCount || 0,
            comments: undefined,
            shares: 0,
            isLiked: item.isLiked || item.likedByCurrentUser || false,
            showComments: false,
            showLikes: false,
            commentInput: ''
          }));
    } else {
      profilePosts.value = [];
    }

  } catch (err: any) {
    if (err.response && (err.response.status === 302 || err.response.status === 304 || err.response.status === 301)) {
      router.push('/profile');
    } else {
      console.error('Failed to load guest profile', err);
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const userId = route.params.publicId as string;
  if (userId) {
    fetchGuestData(userId);
  }
})

watch(
    () => route.params.publicId,
    (newPublicId) => {
      if (newPublicId) {
        guestProfile.value = null;
        profilePosts.value = [];
        friends.value = [];
        mutualFriends.value = [];
        fetchGuestData(newPublicId as string);
      }
    }
)
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.2); }
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
</style>