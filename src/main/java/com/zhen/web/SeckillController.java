package com.zhen.web;

import com.zhen.dto.Exposer;
import com.zhen.dto.SeckillExecution;
import com.zhen.dto.SeckillResult;
import com.zhen.entity.Seckill;
import com.zhen.enums.SeckillStateEnum;
import com.zhen.exception.RepeatKillException;
import com.zhen.exception.SeckillCloseException;
import com.zhen.exception.SeckillException;
import com.zhen.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    //列表页
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model){
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }

    //详情页
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if (seckillId == null){
            logger.info("id为null");
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null){
            logger.info("seckil为null");
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    //秒杀链接
    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody//返回json
    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    //执行秒杀
    @RequestMapping(value = "/{sekillId}/{md5}/execution",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("sekillId") Long seckillId,
                                                   //从cookie获取，让killPhone不是必须的，将错误放到程序中处理，而不是让spring直接返回错误
                                                   @CookieValue(value = "killPhone",required = false) Long phone,
                                                   @PathVariable("md5") String md5){
        if (phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }

        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,phone,md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (RepeatKillException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (SeckillCloseException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }

}
