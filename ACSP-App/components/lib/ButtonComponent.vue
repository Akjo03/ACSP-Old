<template>
    <button @click="onClicked()" :disabled="disabled" :aria-disabled="disabled" :class="`inline-flex items-center justify-center gap-x-4 xl:gap-x-5 px-4 py-2 xl:px-5 xl:py-3 m-2 min-h-[2em] rounded-md shadow-sm transition-all select-none ${getButtonStyles} active:ring-2 active:ring-white/5 disabled:active:ring-0 disabled:cursor-not-allowed disabled:opacity-90`">
        <span v-if="icon && iconPlacement === 'left'"><font-awesome-icon :icon="icon" :class="`${getButtonTextStyles} text-center -ml-0.5 text-base xl:text-lg`" aria-hidden="true" :spin="isLoading" style="--fa-animation-duration: 1s;" /></span>
        <span v-if="text" :class="`${getButtonTextStyles} font-semibold text-sm xl:text-base`">{{text}}</span>
        <span v-if="icon && iconPlacement === 'right'"><font-awesome-icon :icon="icon" :class="`${getButtonTextStyles} text-center -ml-0.5 text-base xl:text-lg`" aria-hidden="true" :spin="isLoading" style="--fa-animation-duration: 1s;" /></span>
    </button>
</template>

<script lang="ts">
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";

export default {
    props: {
        text: {
            type: String,
            default: null
        },
        icon: {
            type: Object as () => IconDefinition,
            default: null
        },
        iconPlacement: {
            type: "left" | "right",
            default: "left"
        },
        type: {
            type: "primary" | "secondary" | "success" | "danger" | "discord",
            default: "secondary"
        },
        disabled: {
            type: Boolean,
            default: false
        },
        onClicked: Function<Promise<void>>(),
        loading: {
            type: Boolean,
            default: false
        }
    }
}
</script>

<script setup lang="ts">
import {computed, ref} from "vue";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faSpinnerThird} from "@fortawesome/pro-regular-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

interface ButtonType {
    name: string;

    colorThemeKey: string;
    colorThemeHoverKey: string;
    colorThemeActiveKey: string;
    colorThemeDisabledKey: string;
    colorThemeTextKey: string;
    colorThemeTextHoverKey: string;
    colorThemeTextActiveKey: string;
    colorThemeTextDisabledKey: string;
}

const buttonTypes: ButtonType[] = [
    {
        name: "primary",

        colorThemeKey: "bg-themeButtonPrimary",
        colorThemeHoverKey: "hover:bg-themeButtonPrimaryHover",
        colorThemeActiveKey: "active:bg-themeButtonPrimaryActive",
        colorThemeDisabledKey: "disabled:bg-themeButtonPrimaryDisabled",
        colorThemeTextKey: "text-themeButtonText",
        colorThemeTextHoverKey: "hover:text-themeButtonText",
        colorThemeTextActiveKey: "active:text-themeButtonText",
        colorThemeTextDisabledKey: "disabled:text-themeButtonTextDisabled"
    },
    {
        name: "secondary",

        colorThemeKey: "bg-themeButtonSecondary",
        colorThemeHoverKey: "hover:bg-themeButtonSecondaryHover",
        colorThemeActiveKey: "active:bg-themeButtonSecondaryActive",
        colorThemeDisabledKey: "disabled:bg-themeButtonSecondaryDisabled",
        colorThemeTextKey: "text-themeText",
        colorThemeTextHoverKey: "hover:text-themeText",
        colorThemeTextActiveKey: "active:text-themeText",
        colorThemeTextDisabledKey: "disabled:text-themeTextDisabled",
    },
    {
        name: "success",

        colorThemeKey: "bg-themeButtonSuccess",
        colorThemeHoverKey: "hover:bg-themeButtonSuccessHover",
        colorThemeActiveKey: "active:bg-themeButtonSuccessActive",
        colorThemeDisabledKey: "disabled:bg-themeButtonSuccessDisabled",
        colorThemeTextKey: "text-themeButtonText",
        colorThemeTextHoverKey: "hover:text-themeButtonText",
        colorThemeTextActiveKey: "active:text-themeButtonText",
        colorThemeTextDisabledKey: "disabled:text-themeButtonTextDisabled"
    },
    {
        name: "danger",

        colorThemeKey: "bg-themeButtonDanger",
        colorThemeHoverKey: "hover:bg-themeButtonDangerHover",
        colorThemeActiveKey: "active:bg-themeButtonDangerActive",
        colorThemeDisabledKey: "disabled:bg-themeButtonDangerDisabled",
        colorThemeTextKey: "text-themeButtonText",
        colorThemeTextHoverKey: "hover:text-themeButtonText",
        colorThemeTextActiveKey: "active:text-themeButtonText",
        colorThemeTextDisabledKey: "disabled:text-themeButtonTextDisabled"
    },
    {
        name: "discord",

        colorThemeKey: "bg-themeButtonDiscord",
        colorThemeHoverKey: "hover:bg-themeButtonDiscordHover",
        colorThemeActiveKey: "active:bg-themeButtonDiscordActive",
        colorThemeDisabledKey: "disabled:bg-themeButtonDiscordDisabled",
        colorThemeTextKey: "text-themeButtonText",
        colorThemeTextHoverKey: "hover:text-themeButtonText",
        colorThemeTextActiveKey: "active:text-themeButtonText",
        colorThemeTextDisabledKey: "disabled:text-themeButtonTextDisabled"
    }
]

const props = defineProps<{
    text?: string;
    icon?: IconDefinition;
    iconPlacement?: "left" | "right";
    type?: "primary" | "secondary" | "success" | "danger" | "discord";
    disabled?: boolean;
    onClicked?: () => Promise<void>;
    loading?: boolean;
}>();

const buttonType = computed(() => buttonTypes.find((buttonType) => buttonType.name === props.type));

const getButtonStyles = computed(() => {
    const key = buttonType.value ? buttonType.value.colorThemeKey : "bg-themeButtonSecondary";
    const hoverKey = buttonType.value ? buttonType.value.colorThemeHoverKey : "hover:bg-themeButtonSecondaryHover";
    const activeKey = buttonType.value ? buttonType.value.colorThemeActiveKey : "active:bg-themeButtonSecondaryActive";
    const disabledKey = buttonType.value ? buttonType.value.colorThemeDisabledKey : "disabled:bg-themeButtonSecondaryDisabled";

    return `${key} ${hoverKey} ${activeKey} ${disabledKey} ${key === "bg-themeButtonSecondary" ? "border border-1 border-themeButtonBorder" : ""}`;
})

const getButtonTextStyles = computed(() => {
    const key = buttonType.value ? buttonType.value.colorThemeTextKey : "text-themeText";
    const hoverKey = buttonType.value ? buttonType.value.colorThemeTextHoverKey : "hover:text-themeText";
    const activeKey = buttonType.value ? buttonType.value.colorThemeTextActiveKey : "active:text-themeText";
    const disabledKey = buttonType.value ? buttonType.value.colorThemeTextDisabledKey : "disabled:text-themeTextDisabled";

    return `${key} ${hoverKey} ${activeKey} ${disabledKey}`;
})

const isLoading = ref(false);

const iconPlacement = computed(() => props.iconPlacement ? props.iconPlacement : "left");
const text = computed(() => props.text ? props.text : null);
const icon = computed(() => {
    if (isLoading.value) {
        return faSpinnerThird;
    } else if (props.icon) {
        return props.icon;
    } else {
        return null;
    }
});
const disabled = computed(() => {
    if (isLoading.value) {
        return true;
    } else if (props.disabled) {
        return props.disabled;
    } else {
        return false;
    }
});
const onClicked = computed(() => {
    if (props.onClicked) {
        return () => {
            if (props.loading) {
                loading(true);
            }
            props.onClicked().then(() => {
                if (props.loading) {
                    loading(false);
                }
            });
        };
    } else {
        return () => {};
    }
});

function loading(value: boolean) {
    isLoading.value = value;
}
</script>