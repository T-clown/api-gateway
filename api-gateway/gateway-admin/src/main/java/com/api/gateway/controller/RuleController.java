package com.api.gateway.controller;

import com.api.gateway.model.dto.ChangeStatusDTO;
import com.api.gateway.model.dto.RuleDTO;
import com.api.gateway.model.vo.Result;
import com.api.gateway.model.vo.RuleVO;
import com.api.gateway.service.RuleService;
import com.api.gateway.utils.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.Resource;
import java.util.List;


@Controller
@RequestMapping("/rule")
public class RuleController {

    @Resource
    private RuleService ruleService;

    /**
     * add new route rule
     *
     * @param ruleDTO
     * @return
     */
    @ResponseBody
    @PostMapping("")
    public Result<Void> add(@Validated RuleDTO ruleDTO){
        ruleService.add(ruleDTO);
        return ResultUtil.success();
    }

    @ResponseBody
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Integer id) {
        ruleService.delete(id);
        return ResultUtil.success();
    }

    @GetMapping("/list")
    public String list(ModelMap map, @RequestParam(name = "appName", required = false) String appName) {
        List<RuleVO> ruleVOS = ruleService.queryList(appName);
        map.put("ruleVOS", ruleVOS);
        map.put("appName", appName);
        return "rule";
    }

    @ResponseBody
    @PutMapping("/status")
    public Result changeStatus(@RequestBody ChangeStatusDTO statusDTO) {
        ruleService.changeStatus(statusDTO);
        return ResultUtil.success();
    }
}
