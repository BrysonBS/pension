<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts';
require('echarts/theme/macarons') // echarts theme
import resize from '@/views/dashboard/mixins/resize'
export default {
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
    setOptions({ xAxisData,data1,data2,data3 } = {}) {
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
            data: ['血高压','血低压', '心率']
          },
          series: [{
            name: '血高压', itemStyle: {
              color: '#ff8800',
              lineStyle: {
                color: '#ff8800',
                width: 2
              }
            },
            smooth: true,
            type: 'line',
            data: data1,
            animationDuration: 2800,
            animationEasing: 'cubicInOut'
          },
            {
              name: '血低压',
              smooth: true,
              type: 'line',
              itemStyle: {
                color: '#38a9fa',
                lineStyle: {
                  color: '#3888fa',
                  width: 2
                },
                areaStyle: {
                  color: '#f3f8ff'
                }
              },
              data: data2,
              animationDuration: 2800,
              animationEasing: 'quadraticOut'
            },
            {
              name: '心率', itemStyle: {
                color: '#FF005A',
                lineStyle: {
                  color: '#FF005A',
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
