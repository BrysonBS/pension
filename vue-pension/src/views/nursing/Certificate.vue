<template>
  <div class="container">
    <el-upload
      :file-list=fileList
      multiple
      action=""
      accept="image/png,image/jpeg"
      ref="upload"
      list-type="picture-card"
      :http-request="handleUpload"
      :auto-upload="true"
      :limit="10">
      <i slot="default" class="el-icon-plus"></i>
      <div slot="file" slot-scope="{file}"
           style="display: flex;justify-content: center;align-items: center;height: inherit;">
        <img
          style="max-width: 100%;max-height: 100%;width: auto;height: auto;"
          class="el-upload-list__item-thumbnail"
          :src="file.url" alt=""
        >
        <span class="el-upload-list__item-actions">
        <span
          class="el-upload-list__item-preview"
          @click="handlePictureCardPreview(file)"
        >
          <i class="el-icon-zoom-in"></i>
        </span>
        <span
          v-if="!disabled"
          class="el-upload-list__item-delete"
          @click="handleDownload(file)"
        >
          <i class="el-icon-download"></i>
        </span>
        <span
          v-if="!disabled"
          class="el-upload-list__item-delete"
          @click="handleRemove(file)"
          v-hasPermi="['nursing:worker:remove']"
        >
          <i class="el-icon-delete"></i>
        </span>
      </span>
      </div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>

<script>
import { download, downloadBlob } from '@/utils/request'
import { addWorkerCertificate, delWorkerCertificate, getWorkerCertificate } from '@/api/nursing/worker'

export default {
  name: 'Certificate',
  props:{
    workerId:{
      type:Number,
      default:-1
    }
  },
  data() {
    return {
      fileList:[],
      formData:undefined,
      dialogImageUrl: '',
      dialogVisible: false,
      disabled: false
    };
  },
  created() {
    getWorkerCertificate(this.workerId).then(response => {
      for(let item of response.data){
        let params = {
          id:item.id,
          rootPath: item.rootPath,
          uri: item.uri,
          fileName: item.fileName
        }
        downloadBlob("/pension/download",params)
          .then(blob =>{
            this.fileList.push({
              raw:params,
              name:params.fileName,
              url:URL.createObjectURL(blob)
            })
          })
      }
    })
  },
  methods: {
    handleRemove(file) {
      const id = file.raw.id;
      delWorkerCertificate(id).then(response => {
        //删除成功,更新列表
        for(let i=0;i<this.fileList.length;i++){
          if(id === this.fileList[i].raw.id){
            this.fileList.splice(i,1);
          }
        }
        this.$modal.msgSuccess("删除成功");
      })
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    //自定义文件上传事件
    async handleUpload(params){
      this.formData = new FormData();
      this.formData.append('certificate', params.file);
      //文件大小限制
      if(params.file.size > (1 << 23)) {//8M
        this.$modal.msgError("图片大小超过8M限制!");
        return;
      }
      this.fileList.push({
        raw:undefined,
        name:params.file.name,
        url:URL.createObjectURL(params.file)
      })
      let response = await addWorkerCertificate(this.workerId,this.formData);
      const file = response.data;
      this.fileList.filter(item =>item.raw === undefined && Object.is(item.name,file.fileName))
        .map(item => item.raw = file)
      this.$modal.msgSuccess("添加成功");
    },
    handleDownload(file) {
      let data = {
        rootPath: file.raw.rootPath,
        uri:file.raw.uri,
        fileName:file.raw.fileName
      }
      let url ="/pension/download";
      download(url,data,file.raw.fileName);
    }
  }
}
</script>

<style scoped>
</style>
