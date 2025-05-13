package cn.turbo.bot.base.module.reminder;

import cn.turbo.bot.base.module.reminder.domain.ReminderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 提醒事项 dao
 *
 * @author huke
 * @date 2025/2/12 08:55
 */
@Component
@Mapper
public interface ReminderDao extends BaseMapper<ReminderEntity> {

    /**
     * 根据处理状态查询
     *
     * @param startReminderId
     * @param handleFlag
     * @param maxTime
     * @param limit
     * @return
     */
    List<ReminderEntity> queryByHandleFlag(@Param("startReminderId") Integer startReminderId,
                                           @Param("handleFlag") Boolean handleFlag,
                                           @Param("maxTime") String maxTime,
                                           @Param("limit") Integer limit);

    /**
     * 根据 botid 用户id 群聊id 查询预约次数
     *
     * @param botId
     * @param userWxId
     * @param roomId
     * @param handleFlag
     * @return
     */
    Integer countByBotIdAndUserWxIdAndRoomId(@Param("botId") Integer botId,
                                             @Param("userWxId") String userWxId,
                                             @Param("roomId") String roomId,
                                             @Param("handleFlag") Boolean handleFlag);
}
