package com.ljw.train.business.service;

import com.ljw.train.business.domain.ConfirmOrder;
import com.ljw.train.business.domain.DailyTrainSeat;
import com.ljw.train.business.domain.DailyTrainTicket;
import com.ljw.train.business.enums.ConfirmOrderStatusEnum;
import com.ljw.train.business.feign.MemberFeign;
import com.ljw.train.business.mapper.ConfirmOrderMapper;
import com.ljw.train.business.mapper.DailyTrainSeatMapper;
import com.ljw.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.ljw.train.business.req.ConfirmOrderTicketReq;
import com.ljw.train.common.context.LoginMemberContext;
import com.ljw.train.common.req.MemberTicketReq;
import com.ljw.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author :ljw
 * @date : 2026/3/20
 * description :
 */
@Service
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

    @Resource
    private MemberFeign memberFeign;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    /**
     * 选中座位后事务处理：
     * 座位表修改售卖情况sell；
     * 余票详情表修改余票；
     * 为会员增加购票记录
     * 更新确认订单为成功
     */
    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList, List<ConfirmOrderTicketReq> tickets, ConfirmOrder confirmOrder) {
        for (int j = 0; j < finalSeatList.size(); j++) {
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

            // 计算这个站卖出去后，影响了哪些站的余票库存
            // 参照2-3节 如何保证不超卖、不少卖，还要能承受极高的并发 10:30左右
            // 影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            // 假设10个站，本次买4~7站
            // 原售：001000001
            // 购买：000011100
            // 新售：001011101
            // 影响：XXX11111X
            /**
             * 【重要概念区分】
             * - 站点索引：表示车站位置，从 0 开始，如：A1(0), A2(1), ..., A6(9)
             * - 区间段索引：表示两个站点之间的区间，从 0 开始，如：[A1→A2](0), [A2→A3](1), ..., [A5→A6](8)
             * - 关系：n 个站点有 n-1 个区间段，sell 字符串长度 = 站点数 - 1
             *
             * 【示例场景】
             * 共 10 个站点，索引从 0 到 9：
             * 站点：A1(0) - A2(1) - A3(2) - 江油 (3) - 广元 (4) - 汉中 (5) - 邯郸 (6) - A4(7) - A5(8) - A6(9)
             *
             * 对应的 9 个区间段（sell 字符串，索引从 0 到 8）：
             * 区间段索引 0: [站点 0→站点 1] 即 [A1→A2]
             * 区间段索引 1: [站点 1→站点 2] 即 [A2→A3]
             * 区间段索引 2: [站点 2→站点 3] 即 [A3→江油] ← 已售卖 ('1')
             * 区间段索引 3: [站点 3→站点 4] 即 [江油→广元]
             * 区间段索引 4: [站点 4→站点 5] 即 [广元→汉中]
             * 区间段索引 5: [站点 5→站点 6] 即 [汉中→邯郸]
             * 区间段索引 6: [站点 6→站点 7] 即 [邯郸→A4]
             * 区间段索引 7: [站点 7→站点 8] 即 [A4→A5]
             * 区间段索引 8: [站点 8→站点 9] 即 [A5→A6] ← 已售卖 ('1')
             *
             * 【本次购票】
             * - 从站点索引 4（广元）到站点索引 7（A4）
             * - 售卖的区间段：区间段索引 [4,5), [5,6), [6,7)
             *   即：[广元→汉中], [汉中→邯郸], [邯郸→A4] 这 3 个区间段
             * - 对应代码：startIndex=4, endIndex=7
             *
             * 【余票更新影响范围计算】
             * 核心原则：只更新那些"本次售卖之前是 0，且与本次售卖区间有交集或相邻连续"的区间段
             *
             * 1. 影响的出发站站点索引范围 [minStartIndex, maxStartIndex]：
             *    - maxStartIndex = endIndex - 1 = 7 - 1 = 6
             *      含义：最晚从站点索引 6（邯郸）出发仍会受影响
             *    - minStartIndex：从站点索引 startIndex-1=3 往前扫描区间段 sell 数组，
             *      找到第一个已售卖的区间段索引 2（即 sell[2]='1'），
             *      所以 minStartIndex = 2 + 1 = 3
             *      含义：从站点索引 3（江油）开始才需要更新余票
             *    - 结果：出发站站点索引范围 [3, 6]
             *      对应站点：江油、广元、汉中、邯郸
             *
             * 2. 影响的到达站站点索引范围 [minEndIndex, maxEndIndex]：
             *    - minEndIndex = startIndex + 1 = 4 + 1 = 5
             *      含义：最早到达站点索引 5（汉中）的票会受影响
             *    - maxEndIndex：从站点索引 endIndex=7 往后扫描区间段 sell 数组，
             *      找到第一个已售卖的区间段索引 8（即 sell[8]='1'），
             *      所以 maxEndIndex = 8
             *      含义：到达站可以更新到站点索引 8（A5），因为 [A4→A5] 虽未售但与本次购票相邻
             *    - 结果：到达站站点索引范围 [5, 8]
             *      对应站点：汉中、邯郸、A4、A5
             *
             * 【最终受影响的余票更新范围】
             * - 出发站站点索引：[3, 6] → 江油、广元、汉中、邯郸
             * - 到达站站点索引：[5, 8] → 汉中、邯郸、A4、A5
             * - 组合规则（必须同时满足）：
             *   ① 出发站站点索引 ∈ [3, 6]
             *   ② 到达站站点索引 ∈ [5, 8]
             *   ③ 出发站站点索引 < 到达站站点索引
             *   ④ 区间段与本次购票区间 [4,7) 有交集或相邻连续
             *   ⑤ 该区间段本身未被售卖（sell 对应位置为'0'）
             *
             * 【实际更新的区间段示例】
             * - 区间段 [3,5)：江油→汉中 ✓
             * - 区间段 [4,6)：广元→邯郸 ✓
             * - 区间段 [5,7)：汉中→A4 ✓
             * - 区间段 [4,8)：广元→A5 ✓（虽然 [7,8) 未售，但与本次购票 [4,7) 相邻连续）
             * - 区间段 [7,8)：A4→A5 ✓（与本次购票终点相邻）
             * - ... 等所有满足上述条件的区间段
             */
            // Integer startIndex = 4;
            // Integer endIndex = 7;
            // Integer minStartIndex = startIndex - 往前碰到的最后一个0;
            // Integer maxStartIndex = endIndex - 1;
            // Integer minEndIndex = startIndex + 1;
            // Integer maxEndIndex = endIndex + 往后碰到的最后一个0;
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] chars = seatForUpdate.getSell().toCharArray();
            Integer maxStartIndex = endIndex - 1;
            Integer minEndIndex = startIndex + 1;
            Integer minStartIndex = 0;
            for (int i = startIndex - 1; i >= 0; i--) {
                char aChar = chars[i];
                if (aChar == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            LOG.info("影响出发站区间：" + minStartIndex + "-" + maxStartIndex);

            Integer maxEndIndex = seatForUpdate.getSell().length();
            for (int i = endIndex; i < seatForUpdate.getSell().length(); i++) {
                char aChar = chars[i];
                if (aChar == '1') {
                    maxEndIndex = i;
                    break;
                }
            }
            LOG.info("影响到达站区间：" + minEndIndex + "-" + maxEndIndex);

            dailyTrainTicketMapperCust.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);

            // 调用会员服务接口，为会员增加一张车票
            MemberTicketReq memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(confirmOrder.getMemberId());
            memberTicketReq.setPassengerId(tickets.get(j).getPassengerId());
            memberTicketReq.setPassengerName(tickets.get(j).getPassengerName());
            memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
            memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
            memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
            memberTicketReq.setStartStation(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
            CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
            LOG.info("调用member接口，返回：{}", commonResp);

            // 更新订单状态为成功
            ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setUpdateTime(new Date());
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);
        }
    }

}
