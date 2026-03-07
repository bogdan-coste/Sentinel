import { ref, computed, watch } from 'vue';

export function useDateSelector() {
    const birthMonth = ref('');
    const birthDay = ref('');
    const birthYear = ref('');
    const activeDropdown = ref<string | null>(null);

    const toggleDropdown = (type: string) => activeDropdown.value = activeDropdown.value === type ? null : type;
    const closeDropdowns = () => activeDropdown.value = null;

    const selectDate = (type: string, value: string) => {
        if (type === 'month') birthMonth.value = value;
        if (type === 'day') birthDay.value = value;
        if (type === 'year') birthYear.value = value;
        activeDropdown.value = null;
    };

    const months = Array.from({ length: 12 }, (_, i) => (i + 1).toString());
    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: 121 }, (_, i) => (currentYear - i).toString());

    const daysInMonth = computed(() => {
        const month = parseInt(birthMonth.value) || 1;
        const year = parseInt(birthYear.value) || 2023;
        return new Date(year, month, 0).getDate();
    });

    const days = computed(() => Array.from({ length: daysInMonth.value }, (_, i) => (i + 1).toString()));

    watch([birthMonth, birthYear], () => {
        if (birthDay.value && parseInt(birthDay.value) > daysInMonth.value) {
            birthDay.value = daysInMonth.value.toString();
        }
    });

    const clearDates = () => {
        birthMonth.value = '';
        birthDay.value = '';
        birthYear.value = '';
    };

    return {
        birthMonth, birthDay, birthYear, activeDropdown,
        months, years, days,
        toggleDropdown, closeDropdowns, selectDate, clearDates
    };
}