<template>
  <div style="width: 154px;height: 35px" ref="logo_ref"/>
</template>

<script>
import resize from '@/views/dashboard/mixins/resize'
import { mapState } from 'vuex'
import { getThemeValue } from '@/utils/screen/theme_utils'
require('echarts/theme/macarons') // echarts theme
export default {
  name: 'Logo',
  mixins: [resize],
  data() {
    return {
      chart: null
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
  watch: {
    theme() {
      this.chart.dispose(); // 销毁当前的图表
      this.initChart(); // 重新以最新的主题名称初始化图表对象
    },
  },
  computed: {
    ...mapState("screen", ["theme"]),
  },
  methods: {
    initChart() {
      this.chart = this.$echarts.init(this.$el,this.theme)
      this.setOptions()
    },
    setOptions() {
      let option = {
        backgroundColor: 'transparent',
        graphic: {
          elements: [
            {
              type: 'text',
              left: 'center',
              top: 'center',
              style: {
                text: '斐爱科技',
                fontFamily: 'Alimama',
                fontSize: 23,
                //fontWeight: 'bold',
                lineDash: [0, 200],
                lineDashOffset: 0,
                fill: 'transparent',
                stroke: getThemeValue(this.theme).titleColor,
                lineWidth: 1
              },
              keyframeAnimation: {
                duration: 3000,
                loop: false,
                keyframes: [
                  {
                    percent: 0.7,
                    style: {
                      fill: 'transparent',
                      lineDashOffset: 200,
                      lineDash: [200, 0]
                    }
                  },
                  {
                    // Stop for a while.
                    percent: 0.8,
                    style: {
                      fill: 'transparent'
                    }
                  },
                  {
                    percent: 1,
                    style: {
                      //opacity: .5,
                      //fill: getThemeValue(this.theme).titleColor,
                    }
                  }
                ]
              }
            }
          ]
        }
      };
      this.chart.setOption(option)
    }
  }
}
</script>

<style scoped>

</style>
