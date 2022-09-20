<template>
  <div style="width: 100%;height: 100%"/>
</template>

<script>
import resize from '@/views/dashboard/mixins/resize'
require('echarts/theme/macarons') // echarts theme
import * as echarts from 'echarts';
import { orderAmount } from '@/api/dashboard/dashboard'
import { mapState } from 'vuex'
export default {
  name: 'AmountBar',
  props: {
    height: {
      type: [Number, String],
      default: '400px'
    }
  },
  mixins: [resize],
  data() {
    return {
      chart: null,
      data: undefined,
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
  computed: {
    ...mapState("screen",["gradient"]),
  },
  methods: {
    async initChart() {
      this.chart = echarts.init(this.$el)
      await this.initData()
      this.setOptions()
      this.updateOption()
    },
    async initData(){
      let response = await orderAmount();
      this.data = response.data;
    },
    setOptions() {
      let initOption = {
        backgroundColor: '#FFF',
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        legend: {
          top: "15%",
          icon: "circle",
        },
        grid: {
          left: "3%",
          top: "30%",
          right: "4%",
          bottom: "1%",
          containLabel: true,
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
          }
        ],
        yAxis: [
          {
            type: 'value'
          }
        ]
      };
      this.chart.setOption(initOption);
    },
    updateOption(){
      let dataSeries = this.data.series.map((item,index) => {
        return {
          name: item.name,
          type: 'line',
          stack: 'Total',
          smooth: true,
          lineStyle: {
            width: 0
          },
          showSymbol: false,
          areaStyle: {
            opacity: 0.8,
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: this.gradient[index <<= 1 % this.gradient.length]
              },
              {
                offset: 1,
                color: this.gradient[++index % this.gradient.length]
              }
            ])
          },
          emphasis: {
            focus: 'series'
          },
          data: item.data
        }
      });
      const dataOption = {
        xAxis: {
          data: this.data.xAxis,
        },
        series: dataSeries
      };
      this.chart.setOption(dataOption);
    }
  }
}
</script>

<style scoped>

</style>
