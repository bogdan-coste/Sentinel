<template>
  <Teleport to="body">
    <div v-if="show" class="fixed inset-0 z-[9999] flex justify-center items-center px-4 overflow-y-auto py-10 animate-fade-in">
      <div class="absolute inset-0 bg-[#0A192F]/80 backdrop-blur-xl cursor-pointer" @click="close"></div>
      <div class="bg-[#051021] border border-white/[0.08] rounded-[32px] w-full max-w-lg p-8 relative z-10 my-auto">

        <button @click="close" class="absolute top-6 right-6 p-2 text-white/50 hover:text-white bg-white/5 hover:bg-white/10 rounded-full transition-colors border border-transparent hover:border-white/10 z-10">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>

        <h3 class="text-2xl font-bold mb-6 text-white/95">Settings</h3>

        <div class="flex gap-6 border-b border-white/10 mb-8">
          <button @click="activeTab = 'basic'" :class="activeTab === 'basic' ? 'border-blue-400 text-white' : 'border-transparent text-white/40 hover:text-white'" class="pb-3 border-b-2 text-[14px] font-semibold transition-colors">Profile</button>
          <button @click="activeTab = 'privacy'" :class="activeTab === 'privacy' ? 'border-blue-400 text-white' : 'border-transparent text-white/40 hover:text-white'" class="pb-3 border-b-2 text-[14px] font-semibold transition-colors">Privacy</button>
        </div>

        <form @submit.prevent="save" class="space-y-5">
          <div v-if="activeTab === 'basic'" class="space-y-5 animate-fade-in">
            <div>
              <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Display Name</label>
              <input v-model="form.name" type="text" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all" />
            </div>
            <div>
              <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Username</label>
              <input :value="profile?.username" type="text" class="w-full bg-black/20 border border-white/[0.02] rounded-2xl py-3.5 px-5 text-[15px] text-white/30 cursor-not-allowed" disabled />
            </div>
            <div>
              <label class="block text-white/50 text-[11px] font-semibold tracking-wider mb-2 uppercase">Bio</label>
              <textarea v-model="form.description" rows="3" class="w-full bg-white/[0.02] hover:bg-white/[0.04] focus:bg-white/[0.06] border border-white/[0.05] rounded-2xl py-3.5 px-5 text-[15px] text-white focus:outline-none focus:border-white/20 transition-all resize-none"></textarea>
            </div>
          </div>

          <div v-if="activeTab === 'privacy'" class="space-y-5 animate-fade-in">
            <p class="text-white/50 text-sm">Privacy settings coming soon...</p>
          </div>

          <div class="flex items-center justify-end gap-3 mt-10">
            <button type="button" @click="close" class="px-6 py-3 text-[14px] font-bold text-white/50 hover:text-white transition-colors">Cancel</button>
            <button type="submit" class="px-8 py-3 rounded-full bg-blue-600 hover:bg-blue-500 text-white transition-all text-[14px] font-bold active:scale-95 flex items-center gap-2" :disabled="isSaving">
              {{ isSaving ? 'Saving...' : 'Save Changes' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import api from "../../../shared/api/api.ts"

const props = defineProps<{ show: boolean, profile: any }>()
const emit = defineEmits(['close', 'saved'])

const activeTab = ref('basic')
const isSaving = ref(false)

const form = reactive({
  name: '', description: '', postVisibility: 'Friends', friendsListVisibility: 'Public', introVisibility: 'Public'
})

watch(() => props.show, (isOpen) => {
  if (isOpen && props.profile) {
    form.name = props.profile.name || ''
    form.description = props.profile.description || ''
  }
})

const close = () => emit('close')

const save = async () => {
  isSaving.value = true
  try {
    await api.put('/users/me', { name: form.name, description: form.description })
    emit('saved')
    close()
  } catch (err) {
    console.error(err)
  } finally {
    isSaving.value = false
  }
}
</script>