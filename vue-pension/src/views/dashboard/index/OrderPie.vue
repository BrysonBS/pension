<template>
  <div style="width: 100%;height: 280px"/>
</template>

<script>
import resize from '@/views/dashboard/mixins/resize'
require('echarts/theme/macarons') // echarts theme
import * as echarts from 'echarts';
import { nursingCount, orderAmount } from '@/api/dashboard/dashboard'
export default {
  name: 'OrderPie',
  mixins: [resize],
  props: {
    theme: {
      type: String,
      default: ''
    },
  },
  data() {
    return {
      chart: null,
      data: undefined
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    async initChart() {
      this.chart = echarts.init(this.$el, this.theme)
      await this.initData()
      this.setOptions()
      this.updateChart()
    },
    setOptions() {
      this.chart.setOption(
        {
          tooltip: {
            trigger: 'item',
            backgroundColor: "rgba(255,255,255,.5)"
          },
          legend: {
            top: 'bottom',
            icon: "circle",
          },
          toolbox: {
            show: true,
            feature: {
              //mark: { show: true },
              //saveAsImage: { show: true }
            }
          },
          series: [
            {
              name: '订单',
              type: 'pie',
              radius: '50%',
              label: {
                show: true,
                position: 'inner',
                formatter: '{d}%'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '20',
                  fontWeight: 'bold'
                }
              },
            }
          ]
        }
      )
    },
    async initData(){
      let response = await nursingCount();
      this.data = response.data;
    },
    updateChart(){
      this.chart.setOption({
        series:[{
          data: this.data
        }]
      })
    }
  }
}
</script>

<style scoped>

</style>
