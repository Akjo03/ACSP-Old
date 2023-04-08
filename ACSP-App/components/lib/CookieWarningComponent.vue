<template>
    <div class="pointer-events-none fixed inset-x-0 bottom-0 px-6 pb-6">
        <div class="pointer-events-auto ml-auto max-w-lg rounded-lg bg-themeBackgroundGradientStart p-6 shadow-lg ring-1 ring-gray-900/10">
            <p class="text-xs md:text-sm xl:text-md leading-6 text-themeText">This website uses cookies to provide you with a great user experience and they make this site work. Accepting our cookies is recommended, as this site won't function without any. We don't track anything with cookies and they are delicious! <br /><br/> See our <a href="#" class="font-semibold text-themeTextLink hover:text-themeTextLinkHover">cookie policy</a> for more information.</p>
            <div class="mt-4 flex items-center">
                <ButtonComponent text="Accept All" type="primary" class="text-xs md:text-sm" />
                <ButtonComponent text="Decline All" class="text-xs md:text-sm" />
            </div>
        </div>
    </div>
</template>
<script setup>
import ButtonComponent from "./ButtonComponent.vue";
</script>