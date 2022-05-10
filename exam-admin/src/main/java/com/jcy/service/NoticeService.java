package com.jcy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcy.entity.Notice;
import com.jcy.vo.PageResponse;

/**
 * @author JCY
 * @implNote 2022/02/10 9:05
 */
public interface NoticeService extends IService<Notice> {
    // 将所有公告设置为历史公告
    boolean setAllNoticeIsHistoryNotice();

    String getCurrentNotice();

    PageResponse<Notice> getAllNotices(String content, Integer pageNo, Integer pageSize);

    void publishNotice(Notice notice);

    void deleteNoticeByIds(String noticeIds);

    void updateNotice(Notice notice);
}
