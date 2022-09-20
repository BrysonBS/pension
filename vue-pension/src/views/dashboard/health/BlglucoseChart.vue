<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import resize from '@/views/dashboard/mixins/resize'
require('echarts/theme/macarons') // echarts theme
import * as echarts from 'echarts';

export default {
  name: 'Blglucose',
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        if(this.chartData.init) this.chart.clear(); //初始化
        this.setOptions(val)
      }
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
    initChart() {
      this.chart = echarts.init(this.$el)
      this.setOptions(this.chartData)
    },
    setOptions({ xAxisData,data3 } = {}) {
      this.chart.setOption(
        {
          xAxis: {
            data: xAxisData,
          },
          grid: {
            left: 10,
            right: 10,
            bottom: 20,
            top: 30,
            containLabel: true
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            },
            padding: [5, 10]
          },
          yAxis: {
            axisTick: {
              show: false
            }
          },
          legend: {
            data: ['血糖']
          },
          series: [{
            name: '血糖', itemStyle: {
              color: '#ff8800',
              lineStyle: {
                color: '#ff8800',
                width: 2
              }
            },
            smooth: true,
            type: 'line',
            data: data3,
            animationDuration: 2800,
            animationEasing: 'cubicInOut'
          }],
          dataZoom:[
            {
              type: 'inside',
              show:true
            }
          ]
        })
    }
  }
}
</script>

<style scoped>

</style>
