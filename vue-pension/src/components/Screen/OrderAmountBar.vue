<template>
  <div class="com-container">
    <div class="com-chart" ref="trend_ref"></div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getThemeValue } from '@/utils/screen/theme_utils'

export default {
  name: 'OrderAmountBar',
  data() {
    return {
      myChart: null,
      allData: null,
      showChoice: false, // 是否显示可选项
      choiceType: "map", // 显示数据类型
      titleFontSize: 0, // 指明标题字体大小
    };
  },
  created() {
  },
  mounted() {
    // 在组件创建完成之后，进行回调函数的注册
    this.$socket.registerCallBack("screenOrderAmountHandler", this.getData);
    this.initChart();
    // this.getData();
    // 发送数据给服务端，告诉服务端，前端现在需要数据
    this.$socket.send({
      operate: "screenOrderAmountHandler",
      test: this.test,
    })
    window.addEventListener("resize", this.screenAdapter);
    this.screenAdapter();
  },
  destroyed() {
    window.removeEventListener("resize", this.screenAdapter);
    // 在组件销毁时，进行回调函数取消
    this.$socket.unRegisterCallBack("screenOrderAmountHandler");
  },
  computed: {
    ...mapState("screen",["theme","gradient","test"]),
    selectTypes() {
      if (!this.allData) {
        return [];
      } else {
        return this.allData.type.filter((item) => {
          return item.key !== this.choiceType;
        });
      }
    },
    showTitle() {
      if (!this.allData) {
        return "";
      } else {
        return this.allData[this.choiceType].title;
      }
    },
    // 设置给标题的样式
    comStyle() {
      return {
        fontSize: this.titleFontSize + "px",
        color: getThemeValue(this.theme).labelColor
      };
    },
    marginStyle() {
      return {
        marginLeft: this.titleFontSize + "px",
        backgroundColor: getThemeValue(this.theme).bgColor
      };
    },
  },
  watch: {
    theme() {
      this.myChart.dispose(); // 销毁当前的图表
      this.initChart(); // 重新以最新的主题名称初始化图表对象
      this.screenAdapter(); // 完成屏幕适配
      this.updateChart(); // 更新图表展示
    },
  },
  methods: {
    initChart() {
      this.myChart = this.$echarts.init(this.$refs.trend_ref, this.theme);
      let initOption = {
        //color: ['#80FFA5', '#00DDFF', '#37A2FF', '#FF0087', '#FFBF00'],
        title: {
          text: '▎ 月订单统计',
          left: 20,
          top: 20,
          textStyle:{
            fontSize: '15px',
          }
        },
        backgroundColor: getThemeValue(this.theme).bgColor,
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
      this.myChart.setOption(initOption);
    },
    // 参数ret就是服务端发送给客户端的图表数据
    getData(ret) {
      // const { data: ret } = await this.$http.get("trend");
      this.allData = ret;

      this.updateChart();
    },
    updateChart() {
      let dataSeries = this.allData.series.map((item,index) => {
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
            color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
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
          data: this.allData.xAxis,
        },
        series: dataSeries
      };
      this.myChart.setOption(dataOption);
    },
    screenAdapter() {
      this.titleFontSize = (this.$refs.trend_ref.offsetWidth / 100) * 3.6;
      const adapterOption = {
        legend: {
          itemWidth: this.titleFontSize,
          itemHeight: this.titleFontSize,
          itemGap: this.titleFontSize,
          fontSize: this.titleFontSize / 2,
        },
      };
      this.myChart.setOption(adapterOption);
      this.myChart.resize();
    },
  },
}
</script>

<style scoped>

</style>
