<template>
  <div class="com-container">
    <div class="com-chart" ref="hot_ref"></div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getThemeValue } from '@/utils/screen/theme_utils'

export default {
  name: 'StatusPie',
  data() {
    return {
      myChart: null,
      allData: null,
      curIndex: 0, // 当前所展示出的一级分类数据
      titleFontSize: 0,
    };
  },
  mounted() {
    // 在组件创建完成之后，进行回调函数的注册
    this.$socket.registerCallBack("screenStatusPieHandler", this.getData);
    this.initChart();
    // this.getData();

    this.$socket.send({
      operate: "screenStatusPieHandler"
    });

    window.addEventListener("resize", this.screenAdapter);
    this.screenAdapter();
  },
  destroyed() {
    window.removeEventListener("resize", this.screenAdapter);
    this.$socket.unRegisterCallBack("screenStatusPieHandler");
  },
  computed: {
    ...mapState("screen",["theme"]),
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
      this.myChart = this.$echarts.init(this.$refs.hot_ref, this.theme);
      let initOption = {
        title: {
          text: "▎ 订单状态统计",
          left: 20,
          top: 20,
          color: "#FFF",
          textStyle:{
            fontSize: '15px',
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: item => item.marker + item.name + " " + item.percent + "%"
        },
        legend: {
          top: "18%",
          icon: "circle",
        },
        backgroundColor: getThemeValue(this.theme).bgColor,
        series: [
          {
            name: '订单',
            type: 'pie',
            radius: ['15%', '45%'],
            center: ['50%', '60%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 8,
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
                fontSize: '10',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
/*            data: [
              { value: 40, name: '未支付' },
              { value: 38, name: '已支付' },
              { value: 320, name: '进行中' },
              { value: 3000, name: '已完成' },
              { value: 280, name: '已关闭' }
            ]*/
          }
        ]
      }
      this.myChart.setOption(initOption);
    },
    getData(ret) {
      // const { data: ret } = await this.$http.get("hotproduct");
      // console.log(ret);
      this.allData = ret;
      this.updateChart();
    },
    updateChart() {
      const dataOption = {
        series: [
          {
            data: this.allData
          },
        ],
      };
      this.myChart.setOption(dataOption);
    },
    screenAdapter() {
      this.titleFontSize = (this.$refs.hot_ref.offsetWidth / 100) * 3.6;
      let adapterOption = {
        title: {
          fontSize: this.titleFontSize,
        },
        legend: {
          top: '18%',
          itemWidth: this.titleFontSize,
          itemHeight: this.titleFontSize,
          itemGap: this.titleFontSize / 2,
          fontSize: this.titleFontSize / 2,
        },
        series: [
          {
            radius: ['15%', '45%'],
            center: ['50%', '60%'],
            itemStyle: {
              borderRadius: 8,
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '10',
                fontWeight: 'bold'
              }
            },
          },
        ],
      };
      if(document.body.offsetWidth <= this.$refs.hot_ref.offsetWidth * 1.1){
        adapterOption.series = [{
          radius: ['30%', '55%'],
          center: ["50%", "50%"],
          itemStyle: {
            borderRadius: 20,
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '20',
              fontWeight: 'bold'
            }
          },
        }]
        adapterOption.legend.top = '8%'
      }
      this.myChart.setOption(adapterOption);
      this.myChart.resize();
    },
  },
}
</script>

<style lang="less" scoped>
.arr-left {
  position: absolute;
  left: 10%;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
}
.arr-right {
  position: absolute;
  right: 10%;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
}
.cat-name {
  position: absolute;
  left: 80%;
  bottom: 5%;
}
</style>
