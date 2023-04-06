import {ref} from 'vue';

export type FooterLink = {
    name: string;
    url: string;
    danger: boolean;
}

const footerLinks = ref([] as FooterLink[])

const setFooterLinks = (links: FooterLink[]) => {
    footerLinks.value = links
}

export const useFooter = () => {
    return { footerLinks, setFooterLinks }
}