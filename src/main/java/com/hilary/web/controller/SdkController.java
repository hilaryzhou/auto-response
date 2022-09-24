package com.hilary.web.controller;

import com.hilary.web.model.Propaganda;
import com.hilary.web.model.commons.Response;
import com.hilary.web.service.SdkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 12:53
 * @description:
 **/
@RestController
@RequestMapping("/Propaganda")
public class SdkController {
    @Autowired
    SdkService sdkService;

    /**
     * 展示所有消息
     * @return Response
     */
    @GetMapping("/list")
    public Response<List<Propaganda>> list() {
        List<Propaganda> list = sdkService.list();
        return Response.ok(list);
    }

    @PostMapping("/add")
    public Response add(@RequestBody Propaganda propaganda) {
        sdkService.edit(propaganda);
        return Response.ok();
    }

    @PostMapping("/update")
    public Response update(@RequestBody Propaganda propaganda, @RequestHeader("code") String code) {
        sdkService.edit(propaganda);
        return Response.ok();
    }

    @PostMapping("/del")
    public Response delteById(@RequestParam("id") String id) {
        sdkService.delete(id);
        return Response.ok();
    }
}
