package com.jcy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcy.entity.Notice;
import org.springframework.stereotype.Repository;

/**
 * @author JCY
 * @implNote 2022/02/09 8:59
 */
@Repository
public interface NoticeMapper extends BaseMapper<Notice> {

    // 将所有公告设置为历史公告
    boolean setAllNoticeIsHistoryNotice();

}
