package com.jcy.controller;

import com.jcy.dto.AddUserDto;
import com.jcy.entity.Notice;
import com.jcy.entity.UserRole;
import com.jcy.service.NoticeService;
import com.jcy.service.UserRoleService;
import com.jcy.service.UserService;
import com.jcy.vo.CommonResult;
import com.jcy.vo.PageResponse;
import com.jcy.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author JCY
 * @implNote 2022/01/26 19:07
 */
@Validated
@RestController
@RequiredArgsConstructor
@Api(tags = "超级管理员权限相关的接口")
@RequestMapping(value = "/admin")
public class AdminController {
    private final UserService userService;

    private final UserRoleService userRoleService;

    private final NoticeService noticeService;

    @GetMapping("/getUser")
    @ApiOperation("获取用户信息,可分页 ----> 查询条件(可无)(username,trueName),必须有的(pageNo,pageSize)")
    public CommonResult<PageResponse<UserInfoVo>> getUser(@RequestParam(required = false) String loginName,
                                                          @RequestParam(required = false) String trueName,
                                                          Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<UserInfoVo>>builder()
                .data(userService.getUser(loginName, trueName, pageNo, pageSize))
                .build();
    }

    @GetMapping("/handleUser/{type}")
    @ApiOperation("管理员操作用户: type=1(启用) 2(禁用) 3(删除) userIds(需要操作的用户id)")
    public CommonResult<Void> handleUser(@PathVariable("type") Integer type, String userIds) {
        userService.handlerUser(type, userIds);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/addUser")
    @ApiOperation("管理员用户新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "系统用户实体", required = true, dataType = "user", paramType = "body")
    })
    public CommonResult<Void> addUser(@RequestBody @Valid AddUserDto userDto) {
        userService.addUser(userDto);
        return CommonResult.<Void>builder().build();
    }

    @GetMapping("/getRole")
    @ApiOperation("查询系统存在的所有角色信息")
    public CommonResult<List<UserRole>> getRole() {
        return CommonResult.<List<UserRole>>builder()
                .data(userRoleService.getUserRole())
                .build();
    }

    @GetMapping("/getAllNotice")
    @ApiOperation("获取系统发布的所有公告(分页 条件查询  二合一接口)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeContent", value = "搜索公告内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "查询结果分页当前页面", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "查询结果的页面条数大小", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<PageResponse<Notice>> getAllNotice(@RequestParam(required = false, name = "noticeContent") String content,
                                                           Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<Notice>>builder()
                .data(noticeService.getAllNotices(content, pageNo, pageSize))
                .build();
    }

    @PostMapping("/publishNotice")
    @ApiOperation("发布新公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "notice", value = "通知实体对象", required = true, dataType = "notice", paramType = "body")
    })
    public CommonResult<Void> publishNotice(@RequestBody Notice notice) {
        noticeService.publishNotice(notice);
        return CommonResult.<Void>builder()
                .build();
    }

    @GetMapping("/deleteNotice")
    @ApiOperation("批量删除公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeIds", value = "系统公告id", required = true, dataType = "string", paramType = "query")
    })
    public CommonResult<Void> deleteNotice(@RequestParam(name = "ids") String noticeIds) {
        noticeService.deleteNoticeByIds(noticeIds);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/updateNotice")
    @ApiOperation("更新公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "notice", value = "通知实体对象", required = true, dataType = "notice", paramType = "body")
    })
    public CommonResult<Void> updateNotice(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return CommonResult.<Void>builder().build();
    }
}
