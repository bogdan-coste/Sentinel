<template>
  <div class="relative w-full overflow-visible">
    <div
        ref="bannerRef"
        class="h-36 relative w-full shrink-0 rounded-t-[40px] overflow-hidden cursor-pointer group z-10"
        @click.stop="$emit('open-submenu', 'banner')"
    >
      <img
          v-if="userProfile?.bannerPicUrl"
          :src="userProfile.bannerPicUrl"
          class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
      />
      <div v-else class="w-full h-full bg-gradient-to-br from-[#12284C] to-[#0A192F] flex items-center justify-center border-b border-white/5">
        <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMiIgY3k9IjIiIHI9IjEiIGZpbGw9InJnYmEoMjU1LDI1NSwyNTUsMC4wMykiLz48L3N2Zz4=')]"></div>
      </div>
      <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-all duration-300 flex items-center justify-center backdrop-blur-[2px]">
        <div class="bg-white/10 p-3 rounded-full border border-white/20">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"></path></svg>
        </div>
      </div>
    </div>

    <div
        ref="avatarRef"
        class="absolute left-1/2 -translate-x-1/2 -bottom-12 z-30 group cursor-pointer"
        @click.stop="$emit('open-submenu', 'avatar')"
    >
      <div class="absolute inset-0 bg-blue-500/10 blur-2xl rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
      <div class="relative w-28 h-28 rounded-[32px] bg-[#0A192F] p-1 border-2 border-[#051A30] overflow-hidden transition-transform duration-300 group-hover:scale-105 group-active:scale-95">
        <img v-if="userProfile?.profilePicUrl" :src="userProfile.profilePicUrl" class="w-full h-full object-cover rounded-[26px]" />
        <div v-else class="w-full h-full flex items-center justify-center bg-white/5 rounded-[26px]">
          <svg class="w-12 h-12 text-white/20" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="activeSubmenu"
           class="fixed z-[9999] bg-[#0A192F]/95 backdrop-blur-3xl border border-white/10 rounded-3xl shadow-[0_30px_60px_rgba(0,0,0,0.6)] py-3 min-w-[220px] animate-menu-in"
           :style="menuPosition">

        <div class="px-5 py-2 mb-1 border-b border-white/5">
          <span class="text-[10px] font-black text-white/30 uppercase tracking-[0.2em]">
            {{ activeSubmenu === 'banner' ? 'Banner Settings' : 'Photo Settings' }}
          </span>
        </div>

        <template v-if="activeSubmenu === 'banner'">
          <template v-if="userProfile?.bannerPicUrl">
            <button @click.stop="$emit('view-image', 'banner')" class="w-full text-left px-5 py-3 text-[13px] font-bold text-white/70 hover:bg-blue-600 hover:text-white transition-all flex items-center gap-3 group/item">
              <svg class="w-4 h-4 text-blue-400 group-hover/item:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
              View Banner
            </button>
            <button @click.stop="triggerUpload('banner')" class="w-full text-left px-5 py-3 text-[13px] font-bold text-white/70 hover:bg-blue-600 hover:text-white transition-all flex items-center gap-3 group/item">
              <svg class="w-4 h-4 text-white/40 group-hover/item:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path></svg>
              Update Banner
            </button>
          </template>
          <button v-else @click.stop="triggerUpload('banner')" class="w-full text-left px-5 py-3 text-[13px] font-bold text-white/70 hover:bg-blue-600 hover:text-white transition-all flex items-center gap-3 group/item">
            <svg class="w-4 h-4 text-blue-400 group-hover/item:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
            Add Banner
          </button>
        </template>

        <template v-else-if="activeSubmenu === 'avatar'">
          <template v-if="userProfile?.profilePicUrl">
            <button @click.stop="$emit('view-image', 'avatar')" class="w-full text-left px-5 py-3 text-[13px] font-bold text-white/70 hover:bg-blue-600 hover:text-white transition-all flex items-center gap-3 group/item">
              <svg class="w-4 h-4 text-blue-400 group-hover/item:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
              View Photo
            </button>
            <button @click.stop="triggerUpload('avatar')" class="w-full text-left px-5 py-3 text-[13px] font-bold text-white/70 hover:bg-blue-600 hover:text-white transition-all flex items-center gap-3 group/item">
              <svg class="w-4 h-4 text-white/40 group-hover/item:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path></svg>
              Update Photo
            </button>
          </template>
          <button v-else @click.stop="triggerUpload('avatar')" class="w-full text-left px-5 py-3 text-[13px] font-bold text-white/70 hover:bg-blue-600 hover:text-white transition-all flex items-center gap-3 group/item">
            <svg class="w-4 h-4 text-blue-400 group-hover/item:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
            Add Photo
          </button>
        </template>
      </div>
    </Teleport>

    <input type="file" ref="bannerInput" class="hidden" accept="image/*" @change="handleBannerSelect" />
    <input type="file" ref="avatarInput" class="hidden" accept="image/*" @change="handleAvatarSelect" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useMediaUpload } from '../../composables/userMediaUpload';

const props = defineProps<{
  userProfile?: any;
  activeSubmenu?: 'avatar' | 'banner' | null;
  isUploadingAvatar?: boolean;
  isUploadingBanner?: boolean;
}>();

const emit = defineEmits(['open-submenu', 'view-image', 'refresh-user']);

const bannerRef = ref<HTMLElement | null>(null);
const avatarRef = ref<HTMLElement | null>(null);

// Calculate position based on the trigger's location on screen
const menuPosition = computed(() => {
  const trigger = props.activeSubmenu === 'banner' ? bannerRef.value : avatarRef.value;
  if (!trigger) return {};

  const rect = trigger.getBoundingClientRect();
  return {
    left: `${rect.right + 20}px`, // 20px to the right of the trigger
    top: `${rect.top}px`          // Align with the top of the trigger
  };
});

// Logic - UNTOUCHED
const { uploadAvatar, uploadBanner } = useMediaUpload();
const bannerInput = ref<HTMLInputElement | null>(null);
const avatarInput = ref<HTMLInputElement | null>(null);

const triggerUpload = (type: 'avatar' | 'banner') => {
  if (type === 'avatar') avatarInput.value?.click();
  else bannerInput.value?.click();
};

const handleAvatarSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;
  const result = await uploadAvatar(file);
  if (result.success) emit('refresh-user');
  target.value = '';
};

const handleBannerSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;
  const result = await uploadBanner(file);
  if (result.success) emit('refresh-user');
  target.value = '';
};
</script>

<style scoped>
.animate-menu-in { animation: menuIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes menuIn {
  from { opacity: 0; transform: translateX(-20px) scale(0.95); }
  to { opacity: 1; transform: translateX(0) scale(1); }
}
</style>