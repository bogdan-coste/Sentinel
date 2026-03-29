<template>
  <AppCanvas>
    <div class="h-screen w-full text-white font-sans overflow-hidden flex flex-col selection:bg-blue-500/30 relative">

      <AppNavbar />

      <div v-if="activeSubmenu" @click="closeSubmenu" class="fixed inset-0 z-40"></div>

      <div class="flex-1 w-full max-w-360 mx-auto overflow-hidden flex flex-col lg:flex-row gap-8 px-4 lg:px-8 py-6">

        <div v-if="loading" class="flex justify-center items-center w-full h-full">
          <div class="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-400"></div>
        </div>

        <aside v-if="userProfile && !loading" class="w-full lg:w-[320px] xl:w-85 h-full flex flex-col gap-5 shrink-0 pt-2 overflow-y-auto no-scrollbar pb-6">

          <div class="bg-[#0A192F]/40 backdrop-blur-2xl rounded-[40px] flex flex-col relative border border-white/5 shrink-0 transition-all overflow-visible" :class="activeSubmenu ? 'z-50' : 'z-10'">

            <div class="w-full relative h-36 shrink-0">
              <UserAvatarBanner
                  :user-profile="userProfile"
                  :active-submenu="activeSubmenu"
                  :is-uploading-avatar="isUploadingAvatar"
                  :is-uploading-banner="isUploadingBanner"
                  @open-submenu="openSubmenu"
                  @view-image="viewImage"
                  @refresh-user="refreshUserData"
              />
              <div class="absolute inset-x-0 bottom-0 h-16 bg-gradient-to-t from-[#09152a] via-[#09152a]/50 to-transparent pointer-events-none"></div>
            </div>

            <div class="px-6 pb-8 flex flex-col items-center text-center relative z-20">

              <div class="h-20 w-full shrink-0"></div>

              <div class="flex flex-col items-center gap-1 mb-6">
                <span class="text-[10px] font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-indigo-400 tracking-[0.25em] uppercase">
                  @{{ userProfile.username }}
                </span>
                <h2 class="text-[28px] font-black text-white tracking-tight leading-tight">
                  {{ userProfile.name }}
                </h2>
              </div>

              <div class="flex items-center justify-around w-full bg-black/30 border border-white/3 rounded-[28px] py-5 mb-6">
                <div class="flex-1 flex flex-col items-center">
                  <span class="text-2xl font-black text-white tracking-tighter">{{ userProfile.friendCount || 0 }}</span>
                  <span class="text-[9px] text-white/30 uppercase tracking-[0.2em] font-bold mt-0.5">Friends</span>
                </div>
                <div class="w-px h-8 bg-white/5"></div>
                <div class="flex-1 flex flex-col items-center">
                  <span class="text-2xl font-black text-white tracking-tighter">{{ communities.length || 0 }}</span>
                  <span class="text-[9px] text-white/30 uppercase tracking-[0.2em] font-bold mt-0.5">Groups</span>
                </div>
              </div>

              <button @click="openEditModal('basic')" class="w-full py-3.5 rounded-full border border-white/8 bg-white/2 hover:bg-white/6 text-white/90 text-[11px] font-bold tracking-[0.2em] uppercase transition-all active:scale-95 mb-6">
                {{ userProfile.description ? 'Edit Profile' : '+ Add a bio' }}
              </button>

              <p v-if="userProfile.description" class="text-white/60 text-[13px] leading-relaxed w-full px-4 mb-6 font-light italic">
                "{{ userProfile.description }}"
              </p>

              <div v-if="editForm.introVisibility !== 'Only Me'" class="flex flex-wrap justify-center gap-2 mb-8">
                <div class="px-4 py-2 rounded-full bg-white/3 border border-white/5 flex items-center gap-2 text-[11px] font-semibold text-white/60 hover:text-white transition-colors cursor-default backdrop-blur-sm">
                  <div class="w-1.5 h-1.5 rounded-full bg-blue-500"></div>
                  Sentinel
                </div>
                <div class="px-4 py-2 rounded-full bg-white/3 border border-white/5 flex items-center gap-2 text-[11px] font-semibold text-white/60 hover:text-white transition-colors cursor-default backdrop-blur-sm">
                  <svg class="w-3.5 h-3.5 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path></svg>
                  Cluj-Napoca
                </div>
              </div>

              <div class="w-full" v-if="editForm.friendsListVisibility !== 'Only Me'">
                <ConnectionsPanel
                    :friends="friends"
                    :total-count="userProfile.friendCount || friends.length"
                    :is-owner="true"
                    @refresh-friends="fetchMyFriends"
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
                <button @click="openEditModal('privacy')" class="text-[13px] font-bold text-white/40 hover:text-white/80 transition-colors pb-3 tracking-wide ml-auto">Settings</button>
              </div>

              <PostCreator
                  :user-profile="userProfile"
                  @post-created="refreshPosts"
                  @view-avatar="viewImage('avatar')"
              />
            </div>

            <div v-if="profilePosts.length > 0" class="flex flex-col gap-6">
              <FeedPost
                  v-for="post in profilePosts"
                  :key="post.id"
                  :post="post"
                  :user-profile="userProfile || undefined"
                  @toggle-menu="toggleMenu"
                  @view-image="viewImage"
                  @open-preview="previewImage = $event"
              />
            </div>

            <div v-else class="flex flex-col items-center justify-center py-24 text-center rounded-4xl bg-white/1 border border-white/3 shrink-0 border-dashed backdrop-blur-sm">
              <div class="w-16 h-16 rounded-3xl bg-white/2 border border-white/5 flex items-center justify-center mb-4 transform rotate-3">
                <svg class="w-8 h-8 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10l4 4v10a2 2 0 01-2 2z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 11v5m0 0l-3-3m3 3l3-3"></path></svg>
              </div>
              <span class="text-[13px] font-bold tracking-wide text-white/40">Nothing here yet</span>
            </div>
          </div>
        </main>

        <aside class="w-full lg:w-75 xl:w-[320px] h-full flex flex-col gap-6 shrink-0 pt-2 overflow-y-auto no-scrollbar pb-6 relative z-10">

          <div class="bg-[#0A192F]/40 backdrop-blur-2xl border border-white/5 rounded-4xl p-5 shrink-0">
            <div class="flex items-center justify-between mb-5">
              <h3 class="text-[11px] font-extrabold text-white/80 tracking-widest uppercase">Suggested Friends</h3>
              <button class="text-[10px] text-blue-400 font-bold hover:text-blue-300 transition-colors uppercase tracking-wider">See All</button>
            </div>

            <div class="flex flex-col gap-1">
              <div v-for="person in suggestedFriends" :key="person.name" class="flex items-center justify-between group p-2 -mx-2 rounded-2xl hover:bg-white/[0.03] transition-all">
                <div class="flex items-center gap-3 cursor-pointer">
                  <div class="relative">
                    <img :src="person.image" class="w-10 h-10 rounded-full object-cover border border-white/10" />
                  </div>
                  <div class="flex flex-col">
                    <span class="text-[13px] font-bold text-white/90 group-hover:text-blue-400 transition-colors">{{ person.name }}</span>
                    <span class="text-[10px] text-white/40 font-medium mt-0.5">{{ person.mutualCount }} mutual friends</span>
                  </div>
                </div>
                <button class="w-8 h-8 rounded-full bg-white/[0.05] group-hover:bg-blue-600 flex items-center justify-center text-white/60 group-hover:text-white transition-all active:scale-95 shrink-0 border border-transparent">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path></svg>
                </button>
              </div>
            </div>
          </div>

          <div class="bg-[#0A192F]/40 backdrop-blur-2xl border border-white/[0.05] rounded-[32px] p-5 shrink-0">
            <div class="flex items-center justify-between mb-5">
              <h3 class="text-[11px] font-extrabold text-white/80 tracking-widest uppercase">Your Photos</h3>
              <button class="text-[10px] text-blue-400 font-bold hover:text-blue-300 transition-colors uppercase tracking-wider">See All</button>
            </div>

            <div v-if="recentPhotos.length > 0" class="grid grid-cols-2 gap-2">
              <div v-for="(photo, index) in recentPhotos" :key="index" class="aspect-square rounded-2xl overflow-hidden border border-white/[0.05] cursor-pointer hover:opacity-80 transition-opacity">
                <img :src="photo" class="w-full h-full object-cover" />
              </div>
            </div>
            <div v-else class="w-full aspect-video rounded-2xl bg-black/20 border border-white/[0.05] flex flex-col items-center justify-center gap-2">
              <svg class="w-6 h-6 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
              <span class="text-[9px] text-white/30 uppercase tracking-widest font-bold">No photos yet</span>
            </div>
          </div>

        </aside>

      </div>

      <PostPreview
          v-if="previewImage"
          :preview-image="previewImage"
          :user-profile="userProfile || undefined"
          @close="closePreview"
          @refresh-user="refreshUserData"
      />

      <Teleport to="body">
        <div v-if="isEditing" class="fixed inset-0 z-[9999] flex justify-center items-center px-4 overflow-y-auto py-10 animate-fade-in">
          <div class="absolute inset-0 bg-[#0A192F]/80 backdrop-blur-xl cursor-pointer" @click="closeEditModal"></div>
          <div class="bg-[#051021] border border-white/[0.08] rounded-[32px] w-full max-w-lg p-8 relative z-10 my-auto">
            <button @click="closeEditModal" class="absolute top-6 right-6 p-2 text-white/50 hover:text-white bg-white/5 hover:bg-white/10 rounded-full transition-colors border border-transparent hover:border-white/10 z-10">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
            </button>

            <h3 class="text-2xl font-bold mb-6 text-white/95">Settings</h3>

            <div class="flex gap-6 border-b border-white/10 mb-8">
              <button @click="activeModalTab = 'basic'" :class="activeModalTab === 'basic' ? 'border-blue-400 text-white' : 'border-transparent text-white/40 hover:text-white'" class="pb-3 border-b-2 text-[14px] font-semibold transition-colors">Profile</button>
              <button @click="activeModalTab = 'privacy'" :class="activeModalTab === 'privacy' ? 'border-blue-400 text-white' : 'border-transparent text-white/40 hover:text-white'" class="pb-3 border-b-2 text-[14px] font-semibold transition-colors">Privacy</button>
            </div>

            <form @submit.prevent="saveProfile" class="space-y-5">
              <div v-if="activeModalTab === 'basic'" class="space-y-5 animate-fade-in">
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Display Name</label>
                  <input v-model="editForm.name" type="text" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
                </div>
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Username</label>
                  <input v-model="editForm.username" type="text" class="w-full bg-black/20 border border-white/[0.02] rounded-2xl py-3.5 px-5 text-[15px] text-white/30 cursor-not-allowed" disabled />
                </div>
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Bio</label>
                  <textarea v-model="editForm.description" rows="3" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all resize-none"></textarea>
                </div>
              </div>

              <div v-if="activeModalTab === 'privacy'" class="space-y-5 animate-fade-in">
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Post Visibility</label>
                  <select v-model="editForm.postVisibility" class="w-full bg-white/[0.02] hover:bg-white/[0.04] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none appearance-none cursor-pointer">
                    <option value="Public" class="bg-[#0F203B]">Public</option>
                    <option value="Friends" class="bg-[#0F203B]">Friends Only</option>
                    <option value="Only Me" class="bg-[#0F203B]">Private</option>
                  </select>
                </div>
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Friends List Visibility</label>
                  <select v-model="editForm.friendsListVisibility" class="w-full bg-white/[0.02] hover:bg-white/[0.04] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none appearance-none cursor-pointer">
                    <option value="Public" class="bg-[#0F203B]">Public</option>
                    <option value="Friends" class="bg-[#0F203B]">Friends Only</option>
                    <option value="Only Me" class="bg-[#0F203B]">Private</option>
                  </select>
                </div>
                <div>
                  <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Profile Details Visibility</label>
                  <select v-model="editForm.introVisibility" class="w-full bg-white/[0.02] hover:bg-white/[0.04] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none appearance-none cursor-pointer">
                    <option value="Public" class="bg-[#0F203B]">Public</option>
                    <option value="Friends" class="bg-[#0F203B]">Friends Only</option>
                    <option value="Only Me" class="bg-[#0F203B]">Hidden</option>
                  </select>
                </div>
              </div>

              <div class="flex items-center justify-end gap-3 mt-10">
                <button type="button" @click="closeEditModal" class="px-6 py-3 text-[14px] font-bold text-white/50 hover:text-white transition-colors">Cancel</button>
                <button type="submit" class="px-8 py-3 rounded-full bg-blue-600 hover:bg-blue-500 text-white transition-all text-[14px] font-bold active:scale-95 flex items-center gap-2" :disabled="isSaving">
                  {{ isSaving ? 'Saving...' : 'Save Changes' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </Teleport>
    </div>
  </AppCanvas>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import api from "../service/api.ts"
import AppNavbar from "../components/common/AppNavbar.vue"
import AppCanvas from "../components/common/AppCanvas.vue"
import FeedPost from "../components/common/BasicPreview/FeedPost.vue"
import PostPreview from "../components/common/PostPreview/PostPreview.vue"
import UserAvatarBanner from "../components/profile/UserAvatarBanner.vue"
import PostCreator from "../components/profile/PostCreator.vue"
import ConnectionsPanel from "../components/common/ConnectionsPanel.vue"
import type { Post } from '../types'

interface UserProfile {
  username: string;
  name: string;
  profilePicUrl: string;
  bannerPicUrl: string;
  description: string;
  email: string;
  friendCount: number;
  profilePicMediaId?: number;
  bannerMediaId?: number;
}

const userProfile = ref<UserProfile | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const profilePosts = ref<Post[]>([])

const friends = ref<any[]>([])
const communities = ref<any[]>([])
const suggestedFriends = ref<any[]>([])

const isEditing = ref(false)
const isSaving = ref(false)
const activeModalTab = ref('basic')
const editForm = reactive({
  name: '',
  username: '',
  description: '',
  postVisibility: 'Friends',
  friendsListVisibility: 'Public',
  introVisibility: 'Public'
})

const activeSubmenu = ref<'avatar' | 'banner' | null>(null)
const previewImage = ref<any>(null)

const isUploadingAvatar = ref(false)
const isUploadingBanner = ref(false)

const activeMenu = ref<number | null>(null)

const toggleLike = (post: any) => {
  post.isLiked = !post.isLiked
  post.likes += post.isLiked ? 1 : -1
}

const toggleMenu = (postId: number) => {
  activeMenu.value = activeMenu.value === postId ? null : postId
}

const recentPhotos = computed(() => {
  return profilePosts.value
      .map(post => post.image)
      .filter(image => image !== null)
      .slice(0, 6)
})

const fetchUserProfile = async () => {
  loading.value = true
  try {
    const response = await api.get('/users/me')
    userProfile.value = response.data
    if (userProfile.value) {
      editForm.name = userProfile.value.name
      editForm.username = userProfile.value.username
      editForm.description = userProfile.value.description || ''
    }
  } catch (err: any) {
    error.value = 'Failed to load profile data.'
  } finally {
    loading.value = false
  }
}

const fetchUserPosts = async () => {
  try {
    const response = await api.get('/users/me/user-media')
    const mediaItems = response.data
    profilePosts.value = mediaItems
        .filter((item: any) => item.type === 'POST')
        .map((item: any) => ({
          id: item.id,
          time: new Date(item.createdAt).toLocaleString(),
          content: item.content || '',
          image: item.imageUrl || null,
          likes: item.likeCount || 0,
          comments: undefined,
          shares: 0,
          isLiked: item.likedByCurrentUser,
          showComments: false,
          showLikes: false,
          commentInput: ''
        }))
  } catch (err) {
    console.error('Failed to fetch user media', err)
  }
}

const openSubmenu = (type: 'avatar' | 'banner') => activeSubmenu.value = type
const closeSubmenu = () => activeSubmenu.value = null

const viewImage = (type: 'avatar' | 'banner') => {
  closeSubmenu()
  const url = type === 'avatar' ? userProfile.value?.profilePicUrl : userProfile.value?.bannerPicUrl
  const mediaId = type === 'avatar' ? userProfile.value?.profilePicMediaId : userProfile.value?.bannerMediaId

  if (!url || !mediaId) {
    console.warn(`Cannot open preview: missing ${type} media ID or URL`)
    return
  }

  previewImage.value = {
    id: mediaId,
    url,
    type,
    likes: 0,
    comments: [],
    isLiked: false,
    showComments: false,
    commentInput: ''
  }
}

const closePreview = () => {
  console.log('Closing preview...');
  previewImage.value = null;
}

const refreshUserData = async () => {
  await fetchUserProfile()
}

const refreshPosts = async () => {
  await fetchUserPosts()
}

const openEditModal = (tab = 'basic') => {
  activeModalTab.value = tab
  isEditing.value = true
}

const closeEditModal = () => {
  isEditing.value = false
}

const saveProfile = async () => {
  if (!userProfile.value) return
  isSaving.value = true
  try {
    await api.put('/users/me', { name: editForm.name, description: editForm.description })
    userProfile.value.name = editForm.name
    userProfile.value.description = editForm.description
    closeEditModal()
  } catch (err) {
    console.error(err)
  } finally {
    isSaving.value = false
  }
}

const fetchMyFriends = async () => {
  try {
    const response = await api.get('/friends/my-friends');
    friends.value = response.data;
  } catch (err) {
    console.error('Failed to fetch friends list', err);
  }
}

onMounted(async () => {
  await fetchUserProfile()
  await fetchUserPosts()
  await fetchMyFriends()
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.2); }
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
.animate-fade-in { animation: fadeIn 0.15s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(4px); } to { opacity: 1; transform: translateY(0); } }
</style>