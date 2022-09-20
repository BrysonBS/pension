<template>
  <div class="com-container">
    <div class="com-chart" ref="stock_ref"></div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getThemeValue } from '@/utils/screen/theme_utils'

export default {
  name: 'PersonsBar',
  data(){
    return{
      myChart: null,
      allData: null,
      curIndex: 0, // 当前显示的页数
      timer: null,
      pathSymbols: {
        elder: 'path://M938.268444 938.666667h-113.777777v-199.111111-14.222223a42.666667 42.666667 0 0 0-85.333334 0l-0.256 10.126223-13.084444 15.217777a22.812444 22.812444 0 0 1-29.781333 2.104889l-119.893334-95.544889a22.784 22.784 0 0 0-36.408889 18.176v190.264889c0 44.259556-28.956444 73.045333-73.386666 73.045334H194.275556A80.327111 80.327111 0 0 1 113.777778 858.538667v-34.759111s-3.982222-305.265778 272.839111-479.118223a23.580444 23.580444 0 0 1 28.558222 3.100445 16863.061333 16863.061333 0 0 1 263.111111 260.920889A155.022222 155.022222 0 0 1 781.824 568.888889a156.444444 156.444444 0 0 1 156.444444 156.444444V938.666667z m-284.444444-512a170.666667 170.666667 0 1 1 0-341.333334 170.666667 170.666667 0 0 1 0 341.333334z m128 455.111111h0.028444z',
        worker: 'path://M607.9744 568.576c184.32 0 334.2848 147.712 334.2848 329.2416v19.3792c-0.0256 93.7472-145.1264 96.768-317.0048 96.8704h-232.1152C214.9632 1013.9648 76.8 1010.944 76.8 917.1968v-19.3792c0-181.5296 149.9648-329.216 334.2848-329.216h196.8896zM550.4 679.04h-76.8v89.6H384v76.8h89.6v89.6h76.8v-89.6H640v-76.8h-89.6v-89.6z m-50.7392-652.8c146.3552 0 265.344 117.248 265.344 261.4528 0 144.2048-119.04 261.504-265.344 261.504-146.3296 0-265.3184-117.2992-265.3184-261.504 0-144.1792 118.9888-261.4528 265.3184-261.4528z',
        bed: 'path://M471.04 528.725333V361.130667c0-33.536 11.136-40.96 40.917333-40.96h286.72c42.88 0 122.922667 57.728 122.922667 165.674666 0 22.357333-18.602667 40.96-40.96 40.96h-409.6v1.92z m-141.525333 0c-55.893333 0-102.4-46.506667-102.4-104.234666 0-57.685333 44.714667-104.192 102.4-104.192 55.893333 0 102.4 46.506667 102.4 104.192-1.834667 57.685333-46.506667 104.234667-102.4 104.234666zM921.6 612.522667v156.416c0 22.357333-18.602667 40.96-40.96 40.96a41.258667 41.258667 0 0 1-40.917333-40.96v-74.453334a41.258667 41.258667 0 0 0-40.96-40.96H225.237333a41.258667 41.258667 0 0 0-40.96 40.96v74.453334c0 22.357333-18.56 40.96-40.874666 40.96a41.258667 41.258667 0 0 1-40.96-40.96V255.061333c0-16.768 7.424-40.96 40.96-40.96 0 0 40.917333-1.792 40.917333 40.96v273.664c0 22.357333 18.602667 40.96 40.96 40.96h655.36c16.725333 0 41.002667 3.712 41.002667 42.837334z',
        device: 'path://M832 917.333333h-128V106.666667h128c46.933333 0 85.333333 38.4 85.333333 85.333333v640c0 46.933333-38.4 85.333333-85.333333 85.333333zM192 106.666667C145.066667 106.666667 106.666667 145.066667 106.666667 192v640c0 46.933333 38.4 85.333333 85.333333 85.333333h448V106.666667H192z m74.666667 746.666666c-17.066667 0-32-14.933333-32-32s14.933333-32 32-32 32 14.933333 32 32-14.933333 32-32 32z m106.666666 0c-17.066667 0-32-14.933333-32-32s14.933333-32 32-32 32 14.933333 32 32-14.933333 32-32 32z m106.666667 0c-17.066667 0-32-14.933333-32-32s14.933333-32 32-32 32 14.933333 32 32-14.933333 32-32 32zM533.333333 569.6c0 4.266667-2.133333 6.4-4.266666 8.533333l-119.466667 85.333334c-14.933333 10.666667-34.133333 10.666667-49.066667 0l-119.466666-85.333334c-2.133333-2.133333-4.266667-6.4-4.266667-8.533333l-19.2-296.533333c0-8.533333 6.4-14.933333 14.933333-12.8l130.133334 32c14.933333 4.266667 32 4.266667 46.933333 0l130.133333-32c8.533333-2.133333 17.066667 4.266667 14.933334 12.8l-21.333334 296.533333z',
        user: 'path://M501.041414 62.931285c125.654838 0 227.51269 100.19498 227.51269 223.775576 0 123.605156-101.856829 223.805252-227.51269 223.805252-125.650745 0-227.506551-100.199073-227.506551-223.805252C273.53384 163.097612 375.424438 62.931285 501.041414 62.931285L501.041414 62.931285 501.041414 62.931285zM501.041414 62.931285c125.654838 0 227.51269 100.19498 227.51269 223.775576 0 123.605156-101.856829 223.805252-227.51269 223.805252-125.650745 0-227.506551-100.199073-227.506551-223.805252C273.53384 163.097612 375.424438 62.931285 501.041414 62.931285L501.041414 62.931285 501.041414 62.931285zM415.730423 585.125461l189.629895 0c162.314783 0 293.880237 129.387859 293.880237 289.057395l0 18.622126c0 62.922075-131.593084 65.313541-293.880237 65.313541L415.730423 958.118524c-162.319899 0-293.880237-0.092098-293.880237-65.313541l0-18.622126C121.852232 714.51332 253.411547 585.125461 415.730423 585.125461L415.730423 585.125461 415.730423 585.125461z',
      },
    }
  },
  watch: {
    theme() {
      this.myChart.dispose(); // 销毁当前的图表
      this.initChart(); // 重新以最新的主题名称初始化图表对象
      this.screenAdapter(); // 完成屏幕适配
      this.updateChart(); // 更新图表展示
    },
  },
  mounted() {
    // 在组件创建完成之后，进行回调函数的注册
    this.$socket.registerCallBack("screenNursingHandler", this.getData);
    this.initChart()
    this.$socket.send({
      operate: "screenNursingHandler"
    })

    window.addEventListener("resize", this.screenAdapter);
    this.screenAdapter();
  },
  destroyed() {
    clearInterval(this.timer);
    window.removeEventListener("resize", this.screenAdapter);
    this.$socket.unRegisterCallBack("screenNursingHandler");
  },
  computed: {
    ...mapState("screen",["theme"]),
  },
  methods: {
    initChart() {
      this.myChart = this.$echarts.init(this.$refs.stock_ref, this.theme);
      let initOption = {
        title: {
          text: '▎ 护理统计',
          left: 20,
          top: 20,
          textStyle:{
            fontSize: '15px',
          }
        },
        backgroundColor: getThemeValue(this.theme).bgColor,
        grid: {
          left: "3%",
          top: "30%",
          right: "4%",
          bottom: "10%",
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            //type: 'none'
          },
          formatter: function (params) {
            return params[0].marker + params[0].name + ': ' + params[0].value;
          }
        },
        xAxis: {
          data: [],
          axisTick: { show: false },
          axisLine: { show: false },
          axisLabel: {
            //color: '#002238'
          }
        },
        yAxis: {
          splitLine: { show: false },
          axisTick: { show: false },
          axisLine: { show: false },
          axisLabel: { show: false }
        },
        //color: ['#e54035'],
        series: [
          {
            name: 'hill',
            type: 'pictorialBar',
            barCategoryGap: '-130%',
            symbol: 'path://M0,10 L10,10 C5.5,10 5.5,5 5,0 C4.5,5 4.5,10 0,10 z',
            itemStyle: {
              opacity: 0.5,
              //barBorderRadius: [0, 33, 33, 0],
              borderRadius: [0,33,33,0],
              // 指明颜色渐变的方向
              // 指明不同百分比之下颜色的值
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: "#5052EE", // 0% 处的颜色
                  },
                  {
                    offset: 1,
                    color: "#AB6EE5", // 100% 处的颜色
                  },
                ],
                global: false, // 缺省为 false
              },
            },
            emphasis: {
              itemStyle: {
                opacity: 1
              }
            },
            data: [],
            z: 10
          },
          {
            name: 'glyph',
            type: 'pictorialBar',
            barGap: '-100%',
            symbolPosition: 'end',
            symbolSize: 20,
            symbolOffset: [0, '-120%'],
            data: []
          }
        ]
      };
      this.myChart.setOption(initOption);
    },
    getData(ret) {
      this.allData = ret;
      this.updateChart();
      //this.startInterval();
    },
    updateChart() {
      let xAxisData = this.allData.map(item => item.name)
      let series1Data = this.allData.map(item => item.value)
      let series2Data = this.allData.map(item => {
        return {
          value: item.value,
          symbol: this.pathSymbols[item.label],
        }
      })
      const dataOption = {
        xAxis: {
          data: xAxisData
        },
        series: [{
          data: series1Data
        },{
          data: series2Data
        }]
      };
      this.myChart.setOption(dataOption);
    },
    screenAdapter() {
      const titleFontSize = this.$refs.stock_ref.offsetWidth / 100 * 3.6;
      const innerRadius = titleFontSize * 2.4;
      const outerRadius = innerRadius * 1.125;
      let adapterOption = {
        title: {
          fontSize: titleFontSize,
          color: "#FFF",
        },
        grid:{
          bottom: "5%",
        },
        series: [{},{
          symbolSize: 50
        }]
      };
      if(document.body.offsetWidth > this.$refs.stock_ref.offsetWidth * 1.1){
        adapterOption.series[1].symbolSize = 20
        adapterOption.grid.bottom = '10%'
      }
      this.myChart.setOption(adapterOption);
      this.myChart.resize();
    },
    startInterval() {
      if (this.timer) {
        clearInterval(this.timer);
      }
      this.timer = setInterval(() => {
        this.curIndex++;
        if (this.curIndex > 1) {
          this.curIndex = 0;
        }
        this.updateChart();
      }, 5000)
    }
  }
}
</script>

<style scoped>

</style>
