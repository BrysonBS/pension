<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="120px">
      <el-card class="box-card" style="padding-top:40px">
        <el-form-item label="自动评价:">
          <el-input style="max-width: 1000px"
                    type="number"
                    :disabled="!checkPermi(['nursing:order:setting:edit'])"
                    placeholder="请输入内容" v-model="form.commentOvertime">
            <template slot="prepend">支付完成订单超过</template>
            <template slot="append">天，自动完成五星好评</template>
          </el-input>
        </el-form-item>
        <el-form-item label="自动关闭:">
          <el-input style="max-width: 1000px"
                    type="number"
                    :disabled="!checkPermi(['nursing:order:setting:edit'])"
                    placeholder="请输入内容" v-model="form.closeOvertime">
            <template slot="prepend"><span v-html="'&emsp;'"/>未支付订单超过</template>
            <template slot="append">分，自动关闭<span v-html="'&emsp;&emsp;&emsp;&emsp;'"/></template>
          </el-input>
        </el-form-item>
        <el-form-item label="备注:">
          <el-input type="textarea" :disabled="!checkPermi(['nursing:order:setting:edit'])" v-model="form.remark"/>
        </el-form-item>
        <el-button type="primary" v-hasPermi="['nursing:order:setting:edit']" @click="submitForm">确 定</el-button>
      </el-card>
    </el-form>
  </div>
</template>

<script>

import { checkPermi } from "@/utils/permission";
import { getOrderSetting, saveOrUpdateOrderSetting } from '@/api/nursing/order/setting'

export default {
  name: 'setting',
  data(){
    return{
      form:{},
    }
  },
  created() {
    this.initData()
  },
  methods:{
    checkPermi,
    initData: function(){
      getOrderSetting().then(response => {
        this.form = response.data || {}
      })
    },
    /** 确定按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          saveOrUpdateOrderSetting(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.initData()
          })
        }
      });
    }
  }
}
</script>

<style scoped>
.el-card{
  margin:auto;
  width:fit-content;
  text-align: center;
}
</style>
