package com.dxxbjl.community.controller;

import com.dxxbjl.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AIphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request , HttpServletResponse response)  {
         //通过request获取相关的请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        //获取请求头
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" +value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");//设置响应返回类型
        try (PrintWriter writer = response.getWriter();){
            writer.write("<h1>大星星</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get请求
     */

    //模拟分页
    //  /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",required = false,defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        /**
         * 获取输入的参数
         * @RequestParam(name = "current",required = false,defaultValue = "1") int current
         * 传入一个int类型的参数 current
         * 使用@RequestParam（）指定参数的name，是否可以不填，不填写的时候默认值是多少
         * required = false  可以不传入
         * defaultValue = "1" 如果没有手动传入，则默认current=1
         * */
        System.out.println(current);
        System.out.println(limit);
        return  "Some Students";
    }

    //模拟查询一个学生
    //  /student/id
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        /**
         * @PathVariable("id") int id
         * 从路径中获取参数
         * */
        System.out.println(id);
        return "a student";
    }

    /**
     * POST请求
     * */
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    /**
     * 响应HTML数据
     * */

    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",18);
        mav.setViewName("/demo/view");  //默认在templates文件下
        return mav;
    }

    //方法二
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){

        model.addAttribute("name","浙江师范大学");
        model.addAttribute("age",100);
        return "/demo/view";
    }

    /**
     * 响应JSON数据（异步请求）
     * Java对象 -》JSON字符串 -》 JS对象
     * */

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",18);
        emp.put("salary",15000.00);

        return emp;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",18);
        emp.put("salary",15000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","李四");
        emp.put("age",20);
        emp.put("salary",12000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","王五");
        emp.put("age",23);
        emp.put("salary",18000.00);
        list.add(emp);

        return list;
    }

    //cookies示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie =new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie的生效范围
        cookie.setPath("/community/alpha");
        //设置cookie生存时间
        cookie.setMaxAge(60*10); //60s*10
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    //Session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","sessionTest");
        return "set session";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "set session";
    }

}
