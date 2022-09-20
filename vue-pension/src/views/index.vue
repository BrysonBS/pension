<template>
  <div v-if="!$isMobile" class="app-container">
    <el-row :span="24" :gutter="10">
      <el-col :xs="14" :sm="15" :md="17" :lg="18" :xl="20">
        <el-card class="box-card" :body-style="{ padding: '0px',height: '400px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-amount" disabled> 订单金额统计</el-link>
          </div>
          <AmountBar/>
        </el-card>
      </el-col>
      <el-col :xs="10" :sm="9" :md="7" :lg="6" :xl="4">
        <el-card class="box-card" :body-style="{ height: '400px' }">
          <div slot="header" class="clearfix">
            <el-link style="width: 70px;float: left;" icon="icon-expand-notice" disabled> 通知公告</el-link>
            <el-link v-if="total > limit" style="width: 40px;font-size: 10px;margin-top: 2px;float: right">
              <app-link to="/notice/show/more">
                更多
              </app-link>
              <i class="el-icon-arrow-right"></i></el-link>
          </div>

          <el-table :data="notices"
                    :cell-style="{ padding: '5px 0 0 0' }"
                    :show-header="false"
                    style="width: 100%;font-size: 12px">
            <el-table-column prop="noticeTitle" label="标题" width="auto" :show-overflow-tooltip="true">
              <template v-slot="scope">
                <app-link :to="'/notice/show/' + scope.row.noticeId" :params="{target:'_blank'}">
                  <el-link :underline="false">
                    {{ scope.row.noticeTitle }}
                  </el-link>
                </app-link>
              </template>
            </el-table-column>
            <el-table-column prop="noticeTime" label="时间" width="110" :show-overflow-tooltip="true"/>
          </el-table>

        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="10">
      <el-col :xs="12" :sm="8" :lg="6">
        <el-card class="box-card" :body-style="{ padding: '0px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-tongji2" disabled> 护理统计</el-link>
          </div>
          <OrderPie/>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="6">
        <el-card class="box-card" :body-style="{ padding: '0px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-expand-pie" disabled> 订单状态统计</el-link>
          </div>
          <OrderStatusPie/>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="6">
        <el-card class="box-card" :body-style="{ height: '280px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-expand-line" disabled> 月订单统计</el-link>
          </div>
          <OrderTrendLine/>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="6">
        <el-card class="box-card" :body-style="{ height: '280px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-expand-alert" disabled> 报警信息</el-link>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :icon="activity.icon"
              :type="activity.type"
              :color="activity.color"
              :size="activity.size"
              :timestamp="activity.timestamp">
              {{activity.content}}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
  <div v-else class="app-container">
    <el-row :span="24">
      <el-col>
        <el-card class="box-card" :body-style="{ padding: '0px',height: '400px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-amount" disabled> 订单金额统计</el-link>
          </div>
          <AmountBar/>
        </el-card>
      </el-col>
    </el-row>
    <el-row :span="24" :gutter="10">
      <el-col>
        <el-card class="box-card" :body-style="{ height: '400px' }">
          <div slot="header" class="clearfix">
            <el-link style="width: 70px;float: left;" icon="icon-expand-notice" disabled> 通知公告</el-link>
            <el-link v-if="total > limit" style="width: 40px;font-size: 10px;margin-top: 2px;float: right">
              <app-link to="/notice/show/more">
                更多
              </app-link>
              <i class="el-icon-arrow-right"></i></el-link>
          </div>

          <el-table :data="notices"
                    :cell-style="{ padding: '5px 0 0 0' }"
                    :show-header="false"
                    style="width: 100%;font-size: 12px">
            <el-table-column prop="noticeTitle" label="标题" width="auto" :show-overflow-tooltip="true">
              <template v-slot="scope">
                <app-link :to="'/notice/show/' + scope.row.noticeId" :params="{target:'_blank'}">
                  <el-link :underline="false">
                    {{ scope.row.noticeTitle }}
                  </el-link>
                </app-link>
              </template>
            </el-table-column>
            <el-table-column prop="noticeTime" label="时间" width="110" :show-overflow-tooltip="true"/>
          </el-table>

        </el-card>
      </el-col>
    </el-row>
    <el-row :span="24" :gutter="10">
      <el-col>
        <el-card class="box-card" :body-style="{ padding: '0px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-tongji2" disabled> 护理统计</el-link>
          </div>
          <OrderPie/>
        </el-card>
      </el-col>
    </el-row>
    <el-row :span="24" :gutter="10">
      <el-col>
        <el-card class="box-card" :body-style="{ padding: '0px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-expand-pie" disabled> 订单状态统计</el-link>
          </div>
          <OrderStatusPie/>
        </el-card>
      </el-col>
    </el-row>
    <el-row :span="24" :gutter="10">
      <el-col>
        <el-card class="box-card" :body-style="{ height: '280px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-expand-line" disabled> 月订单统计</el-link>
          </div>
          <OrderTrendLine/>
        </el-card>
      </el-col>
    </el-row>
    <el-row :span="24" :gutter="10">
      <el-col>
        <el-card class="box-card" :body-style="{ height: '280px' }">
          <div slot="header" class="clearfix">
            <el-link icon="icon-expand-alert" disabled> 报警信息</el-link>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :icon="activity.icon"
              :type="activity.type"
              :color="activity.color"
              :size="activity.size"
              :timestamp="activity.timestamp">
              {{activity.content}}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import OrderPie from '@/views/dashboard/index/OrderPie'
import OrderStatusPie from '@/views/dashboard/index/OrderStatusPie'
import AmountBar from '@/views/dashboard/index/AmountBar'
import OrderTrendLine from '@/views/dashboard/index/OrderTrendLine'
import AppLink from '@/layout/components/Sidebar/Link'
import { listNoticeLatest } from '@/api/system/notice'
import { deviceLatest } from '@/api/dashboard/dashboard'

export default {
  name: 'index',
  components: { AppLink, OrderTrendLine, AmountBar, OrderStatusPie, OrderPie},
  data() {
    return {
      notices: undefined,
      total: undefined,
      limit: 13,//13,
      activities: []
    };
  },
  mounted() {
    listNoticeLatest(this.limit + 1).then(response => {
     this.notices = response.data.slice(0,this.limit)
     this.total = this.notices ? response.data.length : 0;
    })
    deviceLatest({ limit: 5 }).then(response => {
      this.activities = response.data.map(item => {
        return {
          content: item.value,
          type: 'warning',
          timestamp: item.name
        }
      })
    })
  },
}
</script>

<style>

.el-table__body-wrapper::-webkit-scrollbar {
  /*width: 0;宽度为0隐藏*/
  width: 0px;
}

.el-table__body-wrapper::-webkit-scrollbar-thumb {
  border-radius: 2px;
  height: 50px;
  background: #eee;
}
.el-table__body-wrapper::-webkit-scrollbar-track {
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 2px;
  background: rgba(0, 0, 0, 0.4);
}

.el-table--scrollable-y .el-table__body-wrapper {
  overflow: hidden !important;
}

.el-table--scrollable-x .el-table__body-wrapper {
  overflow: hidden !important;
}

.el-card__header{
  border: none;
}
.el-card__body{
  padding-top: 0;
}
.el-card{
  margin-bottom: 10px;
}
.el-link{
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 12px;
  line-height: 12px;
}
</style>
