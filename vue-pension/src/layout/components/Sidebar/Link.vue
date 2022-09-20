<template>
  <component :is="type" :params="params" v-bind="linkProps(to)">
    <slot />
  </component>
</template>

<script>
import { isExternal } from '@/utils/validate'

export default {
  name: 'app-link',
  props: {
    to: {
      type: [String, Object],
      required: true
    },
    params:{
      type: Object,
      default: undefined
    }
  },
  computed: {
    isExternal() {
      return isExternal(this.to)
    },
    type() {
      if (this.isExternal) {
        return 'a'
      }
      return 'router-link'
    }
  },
  methods: {
    linkProps(to) {
      if (this.isExternal) {
        return {
          href: to,
          target: '_blank',
          rel: 'noopener'
        }
      }
      else if(this.params){
        return {...this.params,to:to}
      }
      return {
        to: to
      }
    }
  }
}
</script>
