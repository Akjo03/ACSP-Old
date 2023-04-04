<template>
  <div class="relative isolate overflow-hidden">
    <header class="border-b-2 border-themeBorder">
      <nav class="mx-auto flex flex-row items-center justify-between py-4 px-6 lg:px-12">
        <div class="flex lg:flex-1">

        </div>
        <a href="/" class="flex flex-row items-center -m-1.5 p-1.5">
          <span class="sr-only">ACSP Logo</span>
          <img class="h-11" src="~/assets/img/logo_512_transparent.png" alt="" />
        </a>
        <div class="flex flex-1 justify-end">
          <a :href="loginLink" class="-mx-3 block rounded-lg py-2.5 px-3 text-base font-semibold leading-7 text-themeButtonText hover:bg-themeHover hover:text-themeTextHover transition-all">
            Log in
            <span class="pl-2" aria-hidden="true">&rarr;</span>
          </a>
        </div>
      </nav>
    </header>
    <div class="mx-auto max-w-7xl px-6 pt-10 pb-24 sm:pb-32 lg:flex lg:py-40 lg:px-8">
      <div class="mx-auto max-w-2xl flex-shrink-0 lg:mx-0 lg:max-w-xl lg:pt-8">
        <img class="h-11" src="~/assets/img/logo_512_transparent.png" alt="ACSP Logo" />
        <h1 class="mt-10 text-4xl leading-[1.1] tracking-tight font-bold text-themeText sm:text-6xl">
          Akjo's Computer Science Program
        </h1>
        <p class="mt-6 text-lg leading-[1.8] text-themeText">
            Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
        </p>
        <div class="mt-10 flex items-center gap-x-6">
          <a :href="discordServerLink" class="inline-flex items-center gap-x-2 rounded-md bg-themeDiscord px-3.5 py-2.5 text-sm font-semibold text-themeButtonText shadow-sm hover:bg-themeDiscordHover focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:bg-themeDiscordHover transition-all">
            <svg class="-ml-0.5 h-5 w-5" viewBox="0 0 256.00 200.00" fill="#000000">
              <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
              <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
              <g id="SVGRepo_iconCarrier">
                <g>
                  <path d="M216.856339,16.5966031 C200.285002,8.84328665 182.566144,3.2084988 164.041564,0 C161.766523,4.11318106 159.108624,9.64549908 157.276099,14.0464379 C137.583995,11.0849896 118.072967,11.0849896 98.7430163,14.0464379 C96.9108417,9.64549908 94.1925838,4.11318106 91.8971895,0 C73.3526068,3.2084988 55.6133949,8.86399117 39.0420583,16.6376612 C5.61752293,67.146514 -3.4433191,116.400813 1.08711069,164.955721 C23.2560196,181.510915 44.7403634,191.567697 65.8621325,198.148576 C71.0772151,190.971126 75.7283628,183.341335 79.7352139,175.300261 C72.104019,172.400575 64.7949724,168.822202 57.8887866,164.667963 C59.7209612,163.310589 61.5131304,161.891452 63.2445898,160.431257 C105.36741,180.133187 151.134928,180.133187 192.754523,160.431257 C194.506336,161.891452 196.298154,163.310589 198.110326,164.667963 C191.183787,168.842556 183.854737,172.420929 176.223542,175.320965 C180.230393,183.341335 184.861538,190.991831 190.096624,198.16893 C211.238746,191.588051 232.743023,181.531619 254.911949,164.955721 C260.227747,108.668201 245.831087,59.8662432 216.856339,16.5966031 Z M85.4738752,135.09489 C72.8290281,135.09489 62.4592217,123.290155 62.4592217,108.914901 C62.4592217,94.5396472 72.607595,82.7145587 85.4738752,82.7145587 C98.3405064,82.7145587 108.709962,94.5189427 108.488529,108.914901 C108.508531,123.290155 98.3405064,135.09489 85.4738752,135.09489 Z M170.525237,135.09489 C157.88039,135.09489 147.510584,123.290155 147.510584,108.914901 C147.510584,94.5396472 157.658606,82.7145587 170.525237,82.7145587 C183.391518,82.7145587 193.761324,94.5189427 193.539891,108.914901 C193.539891,123.290155 183.391518,135.09489 170.525237,135.09489 Z" fill="#ffffff" fill-rule="nonzero"></path>
                </g>
              </g>
            </svg>
            Get started
          </a>
          <a href="#" class="text-sm font-semibold leading-6 rounded-md px-3.5 py-2.5 text-themeButtonText hover:text-themeTextHover hover:bg-themeHover transition-all">
            Learn more <span class="pl-2" aria-hidden="true">→</span>
          </a>
        </div>
      </div>
      <div class="mx-auto mt-16 flex max-w-2xl sm:mt-24 lg:ml-10 lg:mt-0 lg:mr-0 lg:max-w-none lg:flex-none xl:ml-32">
        <div class="max-w-3xl flex-none sm:max-w-5xl lg:max-w-none">
          <img src="" alt="Screenshot of app" width="2432" height="1442" class="w-[76rem] rounded-md bg-white/5 shadow-2xl ring-1 ring-white/10" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useRuntimeConfig} from "nuxt/app";

const config = useRuntimeConfig();

const discordServerLink = config.public.discordServerLink;
const loginLink = config.public.backendUrl + "/api/auth/login";
</script>