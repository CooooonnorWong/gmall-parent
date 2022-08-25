package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.ActivityInfoService;
import com.atguigu.gmall.activity.service.ActivityRuleService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.ActivityInfo;
import com.atguigu.gmall.model.activity.ActivityRule;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/25
 */
@RestController
@Api(tags = "活动信息")
@RequestMapping("/admin/activity/activityInfo")
public class ActivityInfoController {
    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private ActivityRuleService activityRuleService;

    @GetMapping("/{page}/{limit}")
    @ApiOperation("查询分页信息")
    public Result<Page<ActivityInfo>> activityInfo(@PathVariable Integer page,
                                                   @PathVariable Integer limit) {
        Page<ActivityInfo> pages = new Page<>(page, limit);
        activityInfoService.page(pages);
        return Result.ok(pages);
    }

    @GetMapping("/get/{id}")
    @ApiOperation("根据id查询活动信息")
    public Result<ActivityInfo> getOne(@PathVariable Long id) {
        ActivityInfo info = activityInfoService.getById(id);
        return Result.ok(info);
    }

    @PutMapping("/update")
    @ApiOperation("更新")
    public Result<Object> update(@RequestBody ActivityInfo info) {
        activityInfoService.updateById(info);
        return Result.ok();
    }

    @PostMapping("/save")
    @ApiOperation("新增")
    public Result<Object> save(@RequestBody ActivityInfo info) {
        activityInfoService.save(info);
        return Result.ok();
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation("根据id删除活动信息")
    public Result<Object> remove(@PathVariable Long id) {
        activityInfoService.removeById(id);
        return Result.ok();
    }

    @DeleteMapping("/batchRemove")
    @ApiOperation("批量删除活动信息")
    public Result<Object> batchRemove(@RequestBody List<Long> idList) {
        activityInfoService.removeByIds(idList);
        return Result.ok();
    }

    @GetMapping("/findActivityRuleList/{actInfoId}")
    @ApiOperation("根据活动id获取活动规则列表")
    public Result<List<ActivityRule>> findActivityRuleList(@PathVariable Long actInfoId) {
        List<ActivityRule> list = activityRuleService.findActivityRuleList(actInfoId);
        return Result.ok(list);
    }
}
