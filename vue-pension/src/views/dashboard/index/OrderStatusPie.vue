<template>
  <div style="width: 100%;height: 280px"/>
</template>

<script>
import resize from '@/views/dashboard/mixins/resize'
require('echarts/theme/macarons') // echarts theme
import * as echarts from 'echarts';
import { orderStatus } from '@/api/dashboard/dashboard'
export default {
  name: 'OrderStatusPie',
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
            backgroundColor: "rgba(255,255,255,.5)",
            formatter: item => item.marker + item.name + " " + item.percent + "%"
            //formatter: '{b}: {d}%'
          },
          legend: {
            top: 'bottom',
            icon: "circle",
          },
          series: [
            {
              name: '订单',
              type: 'pie',
              radius: ['20%', '50%'],
              center: ['50%', '50%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 5,
                borderColor: '#fff',
                borderWidth: 0
              },
              label: {
                show: false,
                position: 'center',
                formatter: '{b}\n{c}'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '18',
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
            }
          ]
        }
      )
    },
    async initData(){
      let response = await orderStatus()
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
