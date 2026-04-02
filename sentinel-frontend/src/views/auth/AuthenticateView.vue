<template>
  <div class="relative min-h-screen w-full flex items-center justify-center overflow-x-hidden bg-[#050b14] font-sans selection:bg-[#40c09d] selection:text-[#050b14]">

    <div class="fixed inset-0 z-0 pointer-events-none">
      <BackgroundCanvas />
    </div>

    <transition name="toast-fade">
      <div v-if="toastVisible"
           class="fixed top-10 left-1/2 -translate-x-1/2 z-100 flex items-center gap-6 px-8 py-5 rounded-2xl backdrop-blur-3xl border border-[#40c09d] bg-[#050b14]/95 shadow-[0_0_40px_rgba(64,192,157,0.25)] min-w-[320px] max-w-[90vw] pointer-events-none transition-opacity duration-500">

        <div class="shrink-0 w-12 h-12 rounded-xl flex items-center justify-center bg-[#40c09d]/10 text-[#40c09d] border border-[#40c09d]/20 shadow-[0_0_15px_rgba(64,192,157,0.1)]">
          <svg v-if="toastType === 'success'" xmlns="http://www.w3.org/2000/svg" class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>

        <div class="flex flex-col">
      <span class="text-[10px] text-[#40c09d] uppercase tracking-[0.3em] font-black opacity-60 mb-1">
        {{ toastType === 'success' ? 'System_Verified' : 'Security_Alert' }}
      </span>
          <p class="text-white text-sm md:text-base font-bold tracking-wide leading-tight">
            {{ toastMessage }}
          </p>
        </div>
      </div>
    </transition>

    <div :class="['relative z-10 w-[92%] bg-[#050b14]/85 backdrop-blur-2xl rounded-[2.5rem] shadow-[0_0_60px_rgba(64,192,157,0.15)] border border-white/10 overflow-hidden transition-all duration-700 ease-in-out group hover:border-[#40c09d]/30', isLoginMode ? 'max-w-md' : 'max-w-4xl']">

      <div class="relative bg-linear-to-b from-[#0d1335] to-transparent pt-12 pb-8 flex items-center justify-center border-b border-white/5">
        <div class="absolute top-0 left-0 w-full h-1 bg-linear-to-r from-transparent via-[#40c09d] to-transparent opacity-50"></div>
        <h1 class="text-white text-2xl md:text-3xl font-black tracking-[0.3em] uppercase drop-shadow-[0_0_15px_rgba(64,192,157,0.5)]">
          {{ isLoginMode ? 'LOGIN' : 'REGISTER' }}
        </h1>
      </div>

      <div class="px-8 py-10 md:px-12 md:py-12">
        <form @submit.prevent="handleAuth" autocomplete="off">

          <div style="position: absolute; left: -9999px; top: -9999px;">
            <input type="text" name="fake-username" />
            <input type="password" name="fake-password" />
          </div>

          <div :class="isLoginMode ? 'flex flex-col' : 'grid grid-cols-1 md:grid-cols-2 md:gap-x-16'">

            <div v-if="!isLoginMode" class="flex flex-col">

              <div class="flex items-center border-b border-white/20 pb-4 mb-8 transition-colors focus-within:border-[#40c09d] group/input">
                <i class="fa fa-userEntity text-white/40 text-lg md:text-xl mr-4 md:mr-6 group-focus-within/input:text-[#40c09d] transition-colors"></i>
                <input v-model="fullName" type="text" placeholder="Full Name" class="w-full bg-transparent text-lg text-white placeholder:text-white/30 focus:outline-none" />
              </div>

              <div class="flex items-center border-b border-white/20 pb-4 mb-8 transition-colors focus-within:border-[#40c09d] group/input">
                <i class="fa fa-id-badge text-white/40 text-lg md:text-xl mr-4 md:mr-6 group-focus-within/input:text-[#40c09d] transition-colors"></i>
                <input v-model="username" type="text" placeholder="Username" class="w-full bg-transparent text-lg text-white placeholder:text-white/30 focus:outline-none" />
              </div>

              <div class="flex items-center border-b border-white/20 pb-4 mb-8 transition-colors focus-within:border-[#40c09d] group/input relative"
                   :class="(activeDropdown === 'month' || activeDropdown === 'day' || activeDropdown === 'year') ? 'z-70' : 'z-10'">
                <i class="fa fa-calendar text-white/40 text-lg md:text-xl mr-4 md:mr-6 group-focus-within/input:text-[#40c09d] transition-colors"></i>
                <div v-if="activeDropdown" @click="closeDropdowns" class="fixed inset-0 z-40"></div>
                <div class="flex gap-2 w-full text-lg relative z-50">

                  <div class="relative flex-1">
                    <div @click="toggleDropdown('month')" class="bg-transparent text-white/80 cursor-pointer text-center hover:text-[#40c09d] transition-all duration-300 select-none" :class="activeDropdown === 'month' ? 'opacity-0' : 'opacity-100'">
                      {{ birthMonth ? birthMonth.padStart(2, '0') : 'MM' }}
                    </div>
                    <div v-if="activeDropdown === 'month'" class="absolute -top-2 left-1/2 -translate-x-1/2 w-20 bg-[#0d1335]/95 backdrop-blur-xl border border-white/10 rounded-xl max-h-40 overflow-y-auto custom-scrollbar shadow-[0_10px_40px_rgba(0,0,0,0.9)] py-2">
                      <div v-for="m in months" :key="m" @click="selectDate('month', m)" class="px-2 py-1.5 text-center text-white/70 hover:text-[#40c09d] hover:bg-[#40c09d]/10 cursor-pointer transition-colors text-base">{{ m.padStart(2, '0') }}</div>
                    </div>
                  </div>
                  <span class="text-white/30" :class="activeDropdown ? 'opacity-20' : 'opacity-100'">/</span>

                  <div class="relative flex-1">
                    <div @click="toggleDropdown('day')" class="bg-transparent text-white/80 cursor-pointer text-center hover:text-[#40c09d] transition-all duration-300 select-none" :class="activeDropdown === 'day' ? 'opacity-0' : 'opacity-100'">
                      {{ birthDay ? birthDay.padStart(2, '0') : 'DD' }}
                    </div>
                    <div v-if="activeDropdown === 'day'" class="absolute -top-2 left-1/2 -translate-x-1/2 w-20 bg-[#0d1335]/95 backdrop-blur-xl border border-white/10 rounded-xl max-h-40 overflow-y-auto custom-scrollbar shadow-[0_10px_40px_rgba(0,0,0,0.9)] py-2">
                      <div v-for="d in days" :key="d" @click="selectDate('day', d)" class="px-2 py-1.5 text-center text-white/70 hover:text-[#40c09d] hover:bg-[#40c09d]/10 cursor-pointer transition-colors text-base">{{ d.padStart(2, '0') }}</div>
                    </div>
                  </div>
                  <span class="text-white/30" :class="activeDropdown ? 'opacity-20' : 'opacity-100'">/</span>

                  <div class="relative flex-1">
                    <div @click="toggleDropdown('year')" class="bg-transparent text-white/80 cursor-pointer text-center hover:text-[#40c09d] transition-all duration-300 select-none" :class="activeDropdown === 'year' ? 'opacity-0' : 'opacity-100'">
                      {{ birthYear || 'YYYY' }}
                    </div>
                    <div v-if="activeDropdown === 'year'" class="absolute -top-2 left-1/2 -translate-x-1/2 w-24 bg-[#0d1335]/95 backdrop-blur-xl border border-white/10 rounded-xl max-h-40 overflow-y-auto custom-scrollbar shadow-[0_10px_40px_rgba(0,0,0,0.9)] py-2">
                      <div v-for="y in years" :key="y" @click="selectDate('year', y)" class="px-2 py-1.5 text-center text-white/70 hover:text-[#40c09d] hover:bg-[#40c09d]/10 cursor-pointer transition-colors text-base">{{ y }}</div>
                    </div>
                  </div>

                </div>
              </div>

              <div class="flex items-center border-b border-white/20 pb-4 mb-8 transition-colors focus-within:border-[#40c09d] group/input relative"
                   :class="activeDropdown === 'gender' ? 'z-70' : 'z-10'">
                <i class="fa fa-venus-mars text-white/40 text-lg md:text-xl mr-4 md:mr-6 group-focus-within/input:text-[#40c09d] transition-colors"></i>
                <div v-if="activeDropdown" @click="closeDropdowns" class="fixed inset-0 z-40"></div>
                <div class="w-full text-lg relative z-50">

                  <div @click="toggleDropdown('gender')" class="w-full bg-transparent text-left cursor-pointer transition-all duration-300 select-none focus:outline-none" :class="[gender ? 'text-white' : 'text-white/40', activeDropdown === 'gender' ? 'opacity-0' : 'opacity-100']">
                    {{ gender || 'Gender' }}
                  </div>

                  <div v-if="activeDropdown === 'gender'" class="absolute -top-3 left-0 w-full bg-[#0d1335]/95 backdrop-blur-2xl border border-white/10 rounded-xl overflow-hidden shadow-[0_15px_40px_rgba(0,0,0,0.9)] py-2">
                    <div @click="gender = 'Male'; activeDropdown = null" class="px-4 py-3 text-white/70 hover:text-[#40c09d] hover:bg-[#40c09d]/10 cursor-pointer transition-colors text-lg">Male</div>
                    <div @click="gender = 'Female'; activeDropdown = null" class="px-4 py-3 text-white/70 hover:text-[#40c09d] hover:bg-[#40c09d]/10 cursor-pointer transition-colors text-lg">Female</div>
                  </div>

                </div>
              </div>

            </div>

            <div class="flex flex-col">

              <div class="flex items-center border-b border-white/20 pb-4 mb-8 transition-colors focus-within:border-[#40c09d] group/input">
                <i class="fa fa-envelope text-white/40 text-lg md:text-xl mr-4 md:mr-6 group-focus-within/input:text-[#40c09d] transition-colors"></i>
                <input v-model="email" type="email" placeholder="Email ID" :readonly="!isReady" autocomplete="new-password" class="w-full bg-transparent text-lg text-white placeholder:text-white/30 focus:outline-none" />
              </div>

              <div class="flex items-center border-b border-white/20 pb-4 mb-8 transition-colors focus-within:border-[#40c09d] group/input">
                <i class="fa fa-lock text-white/40 text-lg md:text-xl mr-4 md:mr-6 group-focus-within/input:text-[#40c09d] transition-colors"></i>
                <input v-model="password" type="password" placeholder="Password" :readonly="!isReady" autocomplete="new-password" class="w-full bg-transparent text-lg text-white placeholder:text-white/30 focus:outline-none" />
              </div>

              <div v-if="!isLoginMode" class="flex items-center border-b border-white/20 pb-4 mb-8 transition-colors focus-within:border-[#40c09d] group/input">
                <i class="fa fa-shield text-white/40 text-lg md:text-xl mr-4 md:mr-6 group-focus-within/input:text-[#40c09d] transition-colors"></i>
                <input v-model="confirmPassword" type="password" placeholder="Confirm Password" autocomplete="new-password" class="w-full bg-transparent text-lg text-white placeholder:text-white/30 focus:outline-none" />
              </div>

            </div>

          </div>

          <button type="submit" class="mt-4 w-full bg-[#40c09d] text-[#050b14] py-4 rounded-xl text-xl font-black tracking-widest transition-all duration-300 shadow-[0_0_30px_rgba(64,192,157,0.3)] hover:shadow-[0_0_50px_rgba(64,192,157,0.6)] hover:-translate-y-1 active:scale-95 disabled:opacity-50 uppercase" :disabled="loading">
            {{ loading ? 'LOADING...' : (isLoginMode ? 'LOGIN' : 'SIGN UP') }}
          </button>

          <div class="mt-6 text-center">
            <p class="text-white/40 text-sm tracking-wide">
              {{ isLoginMode ? "Don't have an account?" : "Already have an account?" }}
              <span @click="toggleMode" class="text-[#40c09d] cursor-pointer hover:text-white transition-colors font-bold ml-2">
                {{ isLoginMode ? 'Register' : 'Login' }}
              </span>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
input:-webkit-autofill {
  -webkit-box-shadow: 0 0 0 100px #050b14 inset !important;
  -webkit-text-fill-color: white !important;
}

.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: rgba(255, 255, 255, 0.05); }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(64, 192, 157, 0.4); border-radius: 10px; }
</style>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { BackgroundCanvas } from '@layouts';
import { authService } from '@features/auth';
import { useToast, useDateSelector } from '@shared/composables';

const router = useRouter();

const { toastVisible, toastMessage, toastType, showToast } = useToast();
const {
  birthMonth, birthDay, birthYear, activeDropdown,
  months, days, years, toggleDropdown, closeDropdowns, selectDate, clearDates
} = useDateSelector();

const isLoginMode = ref(true);
const isReady = ref(false);
const loading = ref(false);

const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const fullName = ref('');
const username = ref('');
const gender = ref('');

onMounted(() => setTimeout(() => isReady.value = true, 100));

const handleAuth = async () => {
  loading.value = true;

  if (isLoginMode.value) {
    try {
      await authService.login({ email: email.value, password: password.value });
      showToast('Login successful. Redirecting...', 'success');
      setTimeout(() => router.push('/home'), 1000);
    } catch (error: any) {
      showToast(authService.parseError(error, 'Invalid email or password.'), 'error');
    } finally {
      loading.value = false;
    }
  } else {
    const validationError = authService.validateRegistration({
      password: password.value, confirmPassword: confirmPassword.value,
      birthYear: birthYear.value, birthMonth: birthMonth.value, birthDay: birthDay.value,
      gender: gender.value
    });

    if (validationError) {
      showToast(validationError, "error");
      loading.value = false;
      return;
    }

    try {
      await authService.register({
        email: email.value, username: username.value, password: password.value,
        fullName: fullName.value, gender: gender.value,
        birthYear: birthYear.value, birthMonth: birthMonth.value, birthDay: birthDay.value
      });

      showToast("ACCESS GRANTED: Profile initialized.", "success");
      setTimeout(() => toggleMode(), 500);
    } catch (error: any) {
      showToast(authService.parseError(error, 'Registration failed.'), "error");
    } finally {
      loading.value = false;
    }
  }
};

const toggleMode = () => {
  isLoginMode.value = !isLoginMode.value;
  email.value = password.value = confirmPassword.value = fullName.value = username.value = gender.value = '';
  clearDates();
  toastVisible.value = false;
};
</script>