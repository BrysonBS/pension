<template>
  <div>
      <div class="ql-editor" v-html="noticeContent"></div>
  </div>
</template>

<script>
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
import { getNotice } from '@/api/system/notice'

export default {
  name: 'show',
  data(){
    return {
      noticeId: undefined,
      noticeTitle: undefined,
      noticeContent: undefined
    }
  },
  mounted() {
    this.noticeId = this.$route.params.noticeId
    getNotice(this.noticeId).then(response => {
      this.noticeContent = response.data.noticeContent
    })
  }
}
</script>

<style scoped>
</style>
