package com.joyo.web;

import com.joyo.dto.Exposer;
import com.joyo.dto.SeckillExecution;
import com.joyo.dto.SeckillResult;
import com.joyo.entity.Seckill;
import com.joyo.enums.SeckillStateEnum;
import com.joyo.exception.RepeatKillException;
import com.joyo.exception.SeckillCloseException;
import com.joyo.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")  //url:模块/资源/{id}/细分  /seckill/list
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService ;

    /**
     * 显示秒杀活动列表
     * @param model
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)//限制请求的方式为GET
    public String list(Model model){
        //获取秒杀列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }

    /**
     * 进入秒杀活动详情页面
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId")Long seckillId, Model model) {
        if (seckillId==null)
        {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null)
        {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "datail";
    }

    //ajax 接口：返回类型是Json

    /**
     *  秒杀活动开启时暴露接口
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST,
            produces = {"application/json;charset=utf-8"}) /*告诉浏览器json的格式和编码，防止中文乱码*/
    @ResponseBody/*告诉spring返回一个json*/
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result = null;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e)
        {
            logger.error(e.getMessage());
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    /**
     * 执行秒杀，返回json
     * @param seckillId
     * @param md5
     * @param phone
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5")String md5,
                                                   @CookieValue(value = "killPhone")Long phone){
        SeckillResult<SeckillExecution> result = null;
        try {
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            result = new SeckillResult<SeckillExecution>(true,execution);
            return result;
        }catch (RepeatKillException e)
        {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            result = new SeckillResult<SeckillExecution>(true,execution);
            return result;
        }catch (SeckillCloseException e)
        {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            result = new SeckillResult<SeckillExecution>(true,execution);
            return result;
        }catch (Exception e)
        {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            result = new SeckillResult<SeckillExecution>(true,execution);
            return result;
        }
    }

    /**
     * 获取系统当前时间
     * @return
     */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }

}
