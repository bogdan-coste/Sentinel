<template>
  <aside class="md:col-span-1 h-full overflow-y-auto no-scrollbar pb-10 pr-2 hidden lg:block">
    <div class="bg-white/[0.03] backdrop-blur-xl border border-white/[0.08] rounded-3xl p-6 shadow-xl">
      <h3 class="text-sm font-semibold mb-6 text-white/80 uppercase tracking-widest flex items-center gap-2">
        <svg class="w-4 h-4 text-blue-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path></svg>
        Pending Requests
      </h3>

      <div v-if="isLoading" class="flex justify-center py-8">
        <div class="w-6 h-6 border-2 border-white/20 border-t-blue-400 rounded-full animate-spin"></div>
      </div>

      <div v-else-if="pendingRequests.length > 0" class="space-y-4">
        <div
            v-for="request in pendingRequests"
            :key="request.publicId"
            class="group flex flex-col gap-3 p-3.5 rounded-2xl bg-white/[0.02] hover:bg-white/[0.06] transition-colors border border-transparent hover:border-white/5"
        >
          <div class="flex items-start gap-3">

            <router-link :to="`/profile/${request.publicId}`" class="relative shrink-0 cursor-pointer">
              <div class="w-9 h-9 rounded-full bg-[#0A192F] flex items-center justify-center overflow-hidden border border-white/10">
                <img v-if="request.profilePicUrl" :src="request.profilePicUrl" class="w-full h-full object-cover" alt="Avatar"/>
                <svg v-else class="w-5 h-5 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
              </div>
            </router-link>

            <div>
              <router-link :to="`/profile/${request.publicId}`" class="font-semibold text-sm block text-white/95 hover:text-blue-400 transition-colors">
                {{ request.name }}
              </router-link>
              <span class="text-white/50 text-[11px]">Sent you a friend request</span>
            </div>
          </div>

          <div class="flex items-center justify-end gap-2 mt-1">
            <button
                @click="declineRequest(request.publicId)"
                :disabled="isProcessing === request.publicId"
                class="text-white/50 text-xs font-semibold hover:text-red-400 px-2 transition-colors disabled:opacity-50"
            >
              Decline
            </button>
            <button
                @click="acceptRequest(request.publicId)"
                :disabled="isProcessing === request.publicId"
                class="bg-white/10 text-white border border-white/10 text-[10px] font-bold px-4 py-1.5 rounded-full uppercase tracking-wider hover:bg-blue-600 hover:border-blue-500 transition-all shadow-md flex items-center justify-center min-w-[70px] disabled:opacity-50"
            >
              <span v-if="isProcessing === request.publicId" class="w-3 h-3 border-2 border-white/40 border-t-white rounded-full animate-spin"></span>
              <span v-else>Accept</span>
            </button>
          </div>
        </div>
      </div>

      <div v-else class="flex flex-col items-center justify-center py-8 text-center">
        <svg class="w-10 h-10 text-white/20 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path></svg>
        <p class="text-white/40 text-xs italic">All caught up. No pending requests.</p>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '../../../shared/api/api.ts';

// Interface matching the map returned from your GET endpoint
interface FriendRequest {
  publicId: string;
  name: string;
  username: string;
  profilePicUrl: string | null;
  friendshipId: number;
}

const pendingRequests = ref<FriendRequest[]>([]);
const isLoading = ref(true);
const isProcessing = ref<string | null>(null); // Tracks which request is currently loading

// 1. Fetch pending requests when component loads
const fetchRequests = async () => {
  try {
    const response = await api.get('/friends/pending-requests');
    pendingRequests.value = response.data;
  } catch (error) {
    console.error('Failed to load pending requests', error);
  } finally {
    isLoading.value = false;
  }
};

// 2. Accept Friend Request
const acceptRequest = async (publicId: string) => {
  isProcessing.value = publicId;
  try {
    await api.post('/friends/accept-request', { publicId });
    // Remove from UI immediately upon success
    pendingRequests.value = pendingRequests.value.filter(req => req.publicId !== publicId);
  } catch (error) {
    console.error('Failed to accept request', error);
  } finally {
    isProcessing.value = null;
  }
};

// 3. Decline Friend Request
const declineRequest = async (publicId: string) => {
  isProcessing.value = publicId;
  try {
    // Note: Axios requires the 'data' property when sending a body in a DELETE request
    await api.delete('/friends/remove-friend', {
      data: { publicId }
    });
    pendingRequests.value = pendingRequests.value.filter(req => req.publicId !== publicId);
  } catch (error) {
    console.error('Failed to decline request', error);
  } finally {
    isProcessing.value = null;
  }
};

onMounted(() => {
  fetchRequests();
});
</script>