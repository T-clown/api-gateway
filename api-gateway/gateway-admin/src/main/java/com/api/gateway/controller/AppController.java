package com.api.gateway.controller;

import com.api.gateway.model.dto.ChangeStatusDTO;
import com.api.gateway.model.dto.RegisterAppDTO;
import com.api.gateway.model.dto.UnregisterAppDTO;
import com.api.gateway.model.vo.AppVO;
import com.api.gateway.model.vo.Result;
import com.api.gateway.service.AppService;
import com.api.gateway.utils.ResultUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Tag(name = "aaa参数")
@Controller
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppService appService;

    @ResponseBody
    @PostMapping("/register")
    public void register(@RequestBody @Validated RegisterAppDTO registerAppDTO) {
        appService.register(registerAppDTO);
    }

    @ResponseBody
    @PostMapping("/unregister")
    public void unregister(@RequestBody UnregisterAppDTO unregisterAppDTO) {
        appService.unregister(unregisterAppDTO);
    }

    @GetMapping("/list")
    public String list(ModelMap model) {
        List<AppVO> appVOList = appService.getList();
        model.put("appVOList", appVOList);
        return "applist";
    }

    @ResponseBody
    @PutMapping("/status")
    public Result<Void> updateEnabled(@RequestBody ChangeStatusDTO statusDTO){
        appService.updateEnabled(statusDTO);
        return ResultUtil.success();
    }

    @ResponseBody
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id")Integer id){
        appService.delete(id);
        return ResultUtil.success();
    }

    @ResponseBody
    @GetMapping("/all")
    public Result<List<AppVO>> getAppList(){
        return ResultUtil.success(appService.getList());
    }
}
