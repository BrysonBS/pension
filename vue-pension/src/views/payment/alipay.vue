<template>
  <div class="app-container">
    <el-empty v-if="form == null" description="当前所属机构没有配置商户信息!"></el-empty>
    <el-form v-if="form != null" ref="form" :model="form" label-width="120px">
      <el-card class="box-card" style="padding-top:40px">
         <el-form-item label="APPID">
           <el-input readonly placeholder="请输入内容" v-model="form.appId"/>
         </el-form-item>
        <el-form-item label="应用私钥">
          <el-input readonly placeholder="请输入内容" autosize type="textarea" v-model="form.appPrivateKey"/>
        </el-form-item>
        <el-form-item v-if="!form.certModel" label="支付宝公钥">
          <el-input readonly placeholder="请输入内容" autosize type="textarea" v-model="form.alipayPublicKey"/>
        </el-form-item>
        <el-form-item v-if="form.certModel" label="证书">
          <el-link type="primary" icon="el-icon-document" disabled>{{getFileName(form.alipayCertPath)}}</el-link>
          <el-link type="primary" icon="el-icon-document" disabled>{{getFileName(form.alipayRootCertPath)}}</el-link>
          <el-link type="primary" icon="el-icon-document" disabled>{{getFileName(form.appCertPath)}}</el-link>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="创建人">
              <time class="time">{{ form.createBy }}</time>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="修改人">
              <time class="time">{{ form.updateBy }}</time>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="创建时间">
              <time class="time">{{ form.createTime }}</time>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="修改时间">
              <time class="time">{{ form.updateTime }}</time>
            </el-form-item>
          </el-col>
        </el-row>

      </el-card>
    </el-form>
  </div>
</template>

<script>
import { getMerchantByPayType } from '@/api/payment/merchant'

export default {
  name: 'alipay',
  data(){
    return{
      form:undefined,
    }
  },
  beforeCreate() {
    getMerchantByPayType({payType: 1}).then(response => {
      this.form = response.data;
    })
  },
  methods:{
    getFileName(path){
      return path.substring(path.lastIndexOf('/') + 1,path.lastIndexOf('_')) + path.substring(path.lastIndexOf('.'));
    }
  }
}
</script>

<style scoped>
.time {
  color: #999;
}
.el-link{
  margin-right: 10px;
}
</style>
