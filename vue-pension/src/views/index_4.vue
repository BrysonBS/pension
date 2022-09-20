<template>
<!--  <div style="width: 500px;height: 500px"/>-->
  <SleepChart :chart-data="sleepData"/>
</template>

<script>
import resize from '@/views/dashboard/mixins/resize'
require('echarts/theme/macarons') // echarts theme
//import echarts from 'echarts'
import * as echarts from 'echarts';
import { listSleep } from '@/api/health/health'
import SleepChart from '@/views/dashboard/health/SleepChart'
import { deviceList } from '@/api/device/deviceCategories'
export default {
  name: 'OrderPie',
  components: {SleepChart},
  mixins: [resize],
  data() {
    return {
      chart: null,
      sleepData : {
        init: true,
        xAxisData : [],
        heartRateData : [],
        respiratoryRateData : []
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ieee: undefined,
        beginTime:undefined,
        endTime:undefined
      },
    }
  },
  mounted() {
    /*    this.$nextTick(() => {
          this.initChart()
        })*/
    //this.initChart();
    this.getClassOptions()
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    /** 获取设备列表 */
    getClassOptions(){
      deviceList().then(response => {
        let data = response.data;
        this.classOptions = data;
        if(data.length > 0)
          this.queryParams.ieee = data[0].ieee;
      }).then(() => this.getList())
    },
    /** 查询通知列表 */
    getList() {
      this.loading = true;
      this.dateRange = this.dateRange || [];
      this.queryParams.beginTime = this.dateRange[0];
      this.queryParams.endTime = this.dateRange[1];
      listSleep(this.queryParams).then(response => {
        this.list = response.data;

        this.showList = this.list.slice(0,500);

        this.sleepData.init = true;
        this.sleepData.xAxisData.splice(0);
        this.sleepData.heartRateData.splice(0) ;
        this.sleepData.respiratoryRateData.splice(0);
        for(let e of response.data){
          this.sleepData.xAxisData.unshift(e.ts);
          this.sleepData.heartRateData.unshift(e.heartRate);
          this.sleepData.respiratoryRateData.unshift(e.respiratoryRate);
        }
        this.loading = false;
      })
    },
    initChart() {
      this.chart = echarts.init(this.$el)
      this.setOptions()
    },
    setOptions() {
      this.chart.setOption(
        {
          title: {
            text: 'Stacked Line'
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
            data: ['Email', 'Union Ads', 'Video Ads', 'Direct', 'Search Engine']
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          toolbox: {
            feature: {
              saveAsImage: {}
            }
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: 'Email',
              type: 'line',
              stack: 'Total',
              data: [120, 132, 101, 134, 90, 230, 210]
            },
            {
              name: 'Union Ads',
              type: 'line',
              stack: 'Total',
              data: [220, 182, 191, 234, 290, 330, 310]
            },
            {
              name: 'Video Ads',
              type: 'line',
              stack: 'Total',
              data: [150, 232, 201, 154, 190, 330, 410]
            },
            {
              name: 'Direct',
              type: 'line',
              stack: 'Total',
              data: [320, 332, 301, 334, 390, 330, 320]
            },
            {
              name: 'Search Engine',
              type: 'line',
              stack: 'Total',
              data: [820, 932, 901, 934, 1290, 1330, 1320]
            }
          ]
        }
      )
    }
  }
}
</script>

<style scoped>

</style>
