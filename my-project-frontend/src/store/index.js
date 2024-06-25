import {defineStore} from 'pinia';
import axios from "axios";

export const useStore = defineStore('general', {
    state: () => {
        return {
            user: {
                id: -1,
                username: '',
                email: '',
                role: '',
                addresses: [],
                registerTime: null
            },
            type: []
        }
    }, getters: {
        avatarUrl() {
            if (this.user.avatar)
                return `${axios.defaults.baseURL}/images${this.user.avatar}`
            else
                return "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
        }
    },
    actions: {
        resetUserData() {
            this.user.id = -1;
            this.user.username = '';
            this.user.email = '';
            this.user.role = '';
            this.user.address = '';
            this.user.registerTime = null;
        }
    }
})