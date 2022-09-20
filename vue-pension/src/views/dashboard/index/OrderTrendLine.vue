<template>
  <div style="width: 100%;height: 280px"/>
</template>

<script>
import resize from '@/views/dashboard/mixins/resize'
require('echarts/theme/macarons') // echarts theme
import * as echarts from 'echarts';
import { orderDay } from '@/api/dashboard/dashboard'
export default {
  name: 'OrderTrendLine',
  mixins: [resize],
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
      this.chart = echarts.init(this.$el)
      await this.initData()
      this.setOptions()
      this.updateChart()
    },
    setOptions() {
      let option = {
        tooltip: {
          trigger: 'axis',
          formatter: params => params.reduce((a,b) => a + b.axisValue + "</br>" + b.marker + "数量: " + b.data,"")
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['2021-12', '2022-01', '2022-02', '2022-03', '2022-04', '2022-05', '2022-06']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [150, 230, 224, 218, 135, 147, 260],
            type: 'line'
          }
        ]
      };
      this.chart.setOption(option);

    },
    async initData(){
      let response = await orderDay()
      this.data = response.data;
    },
    updateChart(){
      this.chart.setOption({
        xAxis: {
          data: this.data.map(item => item.name)
        },
        series:[{
          data: this.data.map(item => item.value)
        }]
      })
    }
  }
}
</script>

<style scoped>

</style>
