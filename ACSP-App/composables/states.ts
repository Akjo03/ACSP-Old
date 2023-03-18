import {useState} from "nuxt/app";

export const useDarkMode = () => useState('darkMode', () => true);