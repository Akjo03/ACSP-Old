import {computed} from 'vue';

export const copyrightYear = computed(() => {
    return new Date().getFullYear();
});