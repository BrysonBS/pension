<template>
  <el-dialog slot="—" :title="title" :width="width" style="max-width: 900px;margin:auto;" :visible.sync="showMapComponent" top="5vh" custom-class="baidu-map" @close="cancel">
    <baidu-map
      :style="mapStyle"
      ak="LDuaRe9uv77YUvQ5LaNCzid7px1iFLYy"
      :map-click="false"
      :center="center"
      :zoom="zoom"
      :scroll-wheel-zoom="true"
      @click="getClickInfo"
      @moving="syncCenterAndZoom"
      @moveend="syncCenterAndZoom"
      @ready="onBaiduMapReady"
      @zoomend="syncCenterAndZoom"
    >
      <bm-view style="width: 100%; height: 100%;" />
      <bm-marker :position="{lng: center.lng, lat: center.lat}" :dragging="true" animation="BMAP_ANIMATION_BOUNCE" />
      <bm-control :offset="{width: '10px', height: '10px'}">
        <bm-auto-complete v-model="keyword" :sug-style="{zIndex: 999999}">
          <el-input v-model="keyword" type="text" placeholder="请输入地名关键字" clearable>
            <i slot="prefix" class="el-input__icon el-icon-search" />
          </el-input>
        </bm-auto-complete>
      </bm-control>
      <bm-geolocation anchor="BMAP_ANCHOR_BOTTOM_RIGHT" show-address-bar auto-location />
      <bm-local-search :keyword="keyword" :auto-viewport="true" :panel="false" />
    </baidu-map>
    <span slot="footer">
      <el-button icon="el-icon-close" @click="cancel">取 消</el-button>
      <el-button icon="el-icon-place" type="primary" @click="confirm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { BaiduMap, BmControl, BmView, BmAutoComplete, BmLocalSearch, BmMarker, BmGeolocation } from 'vue-baidu-map'
export default {
  components: {
    BaiduMap,
    BmControl,
    BmView,
    BmAutoComplete,
    BmLocalSearch,
    BmMarker,
    BmGeolocation
  },
  props: {
    // 在子组件中使用 props 选项去接收来自父组件传递过来的数据
    dialogVisible: Boolean,
    mapHeight: {
      type: Number,
      default: 450
    },
    title: {
      type: String,
      default: '选择地址'
    },
    width: {
      type: [Number, String],
      default: '85%'
    },
    height: {
      type: [Number, String],
      default: '70%'
    }
  },
  data: function() {
    return {
      BMap: null, // 地图组件是否就绪
      showMapComponent: this.dialogVisible,
      keyword: '', // 地区搜索的信息
      mapStyle: {
        width: '100%',
        height: this.mapHeight + 'px'
      },
      center: { lng: 116.404, lat: 39.915 },
      choosedLocation: { province: '', city: '', district: '', addr: '' },
      zoom: 15
    }
  },
  watch: {
    dialogVisible: function(currentValue) {
      this.showMapComponent = currentValue
      if (currentValue) {
        this.keyword = ''
      }
    }
  },
  methods: {
    // ready事件：在你需要二次开发或手动控制其子组件，可以使用在此事件中使用返回的 BMap 类和 map 实例进行手动控制
    onBaiduMapReady(e) {
      const that = this
      this.BMap = e.BMap
      if (this.BMap) {
        const geolocation = new this.BMap.Geolocation()
        geolocation.getCurrentPosition(res => {
          if(geolocation.getStatus() === BMAP_STATUS_SUCCESS){
            const { lng, lat } = res.point
            const { province, city, district, street, street_number } = res.address
            that.center = { lng, lat }
            that.choosedLocation = { province, city, district, addr: street + street_number, lng, lat }
          }
          else {//失败则使用ip定位
            const myCity = new this.BMap.LocalCity()
            myCity.get(res => {
              const { lng, lat } = res.center
              that.center = { lng, lat }
              that.choosedLocation.city = res.name

            })
          }
        })
      }
    },

    /** *
     * 地图点击事件。
     */
    getClickInfo(e) {
      // 调整地图中心位置
      this.center.lng = e.point.lng
      this.center.lat = e.point.lat

      // 此时已经可以获取到BMap类
      if (this.BMap) {
        const that = this
        // Geocoder() 类进行地址解析
        // 创建地址解析器的实例
        const geoCoder = new this.BMap.Geocoder()
        // getLocation() 类--利用坐标获取地址的详细信息
        // getPoint() 类--获取位置对应的坐标
        geoCoder.getLocation(e.point, function(res) {
          //console.log('获取经纬度', e.point, '获取详细地址', res)
          const addrComponent = res.addressComponents
          const surroundingPois = res.surroundingPois
          const province = addrComponent.province
          const city = addrComponent.city
          const district = addrComponent.district
          let addr = addrComponent.street
          if (surroundingPois.length > 0 && surroundingPois[0].title) {
            if (addr) {
              addr += `-${surroundingPois[0].title}`
            } else {
              addr += `${surroundingPois[0].title}`
            }
          } else {
            addr += addrComponent.streetNumber
          }
          that.choosedLocation = { province, city, district, addr, ...that.center }
        })
      }
    },

    syncCenterAndZoom(e) {
      // 返回地图当前的缩放级别
      this.zoom = e.target.getZoom()
    },
    /** *
     * 确认
     */
    confirm: function() {
      this.showMapComponent = false
      this.$emit('map-confirm', this.choosedLocation)
    },
    /** *
     * 取消
     */
    cancel: function() {
      this.showMapComponent = false
      this.$emit('map-cancel', this.showMapComponent)
    }
  }
}
</script>

<style lang="scss">
.baidu-map {
  .el-dialog__body {
    padding: 5px !important;
  }
}
</style>
