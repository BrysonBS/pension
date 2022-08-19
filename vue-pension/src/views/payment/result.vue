<template>
  <div>
    <el-result icon="error" title="支付失败" />
    <el-result icon="success" title="支付成功" />
  </div>
</template>

<script>
export default {
  name: 'result',
  created() {
    console.log(this.$route.query.out_trade_no);
  }
}
</script>

<style>

</style>
