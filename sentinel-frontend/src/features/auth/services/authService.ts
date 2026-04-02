import api from '../../../shared/api/api.ts'

export const authService = {
    validateRegistration(form: any): string | null {
        if (form.password !== form.confirmPassword) return "Passwords do not match!";
        if (!form.birthYear || !form.birthMonth || !form.birthDay) return "Please complete your full birth date.";
        if (!form.gender) return "Please select a gender.";
        return null;
    },

    async register(rawForm: any) {
        const formattedPayload = {
            email: rawForm.email,
            username: rawForm.username,
            password: rawForm.password,
            name: rawForm.fullName || null,
            birthDate: `${rawForm.birthYear}-${rawForm.birthMonth.padStart(2, '0')}-${rawForm.birthDay.padStart(2, '0')}`,
            gender: rawForm.gender ? rawForm.gender.toUpperCase() : null
        };

        const response = await api.post('/users/register', formattedPayload);
        return response.data;
    },

    async login(credentials: any) {
        const response = await api.post('/users/login', credentials);
        return response.data;
    },

    parseError(error: any, defaultMessage: string): string {
        let errorMsg = defaultMessage;
        if (error.response && error.response.data) {
            if (typeof error.response.data === 'string') {
                errorMsg = error.response.data;
                const match = errorMsg.match(/interpolatedMessage='([^']+)'/);
                if (match && match[1]) {
                    errorMsg = match[1];
                }
            } else if (error.response.data.message) {
                errorMsg = error.response.data.message;
            } else {
                errorMsg = JSON.stringify(error.response.data);
            }
        }
        return errorMsg;
    }
};