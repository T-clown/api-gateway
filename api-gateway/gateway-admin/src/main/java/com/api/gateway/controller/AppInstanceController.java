package com.api.gateway.controller;

import com.api.gateway.model.dto.UpdateWeightDTO;
import com.api.gateway.model.vo.InstanceVO;
import com.api.gateway.model.vo.Result;
import com.api.gateway.service.AppInstanceService;
import com.api.gateway.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/app/instance")
public class AppInstanceController {

    @Autowired
    private AppInstanceService instanceService;

    @GetMapping("/list")
    public String list(@RequestParam("appId") Integer appId, ModelMap map) {
        List<InstanceVO> instanceVOS = instanceService.queryList(appId);
        map.put("instanceVOS", instanceVOS);
        return "instance";
    }

    @ResponseBody
    @PutMapping("")
    public Result<Void> updateWeight(@RequestBody @Validated UpdateWeightDTO updateWeightDTO){
        instanceService.updateWeight(updateWeightDTO);
        return ResultUtil.success();
    }
}
