package com.wwq.toutiao.Service;

import com.wwq.toutiao.Models.LoginTicket;
import com.wwq.toutiao.Models.User;
import com.wwq.toutiao.DAO.UserDAO;
import com.wwq.toutiao.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private com.wwq.toutiao.DAO.LoginTicketDAO LoginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password)
    {
           Map<String,Object> map=new HashMap<String,Object>();
           if(username==null||username.length()==0)
           {
               map.put("messagename","用户名不能为空");
               return map;
           }

           if(password==null||password.length()==0)
           {
               map.put("messagepassword","密码不能为空");
               return map;
           }
           User user=userDAO.selectByName(username);
           if(user!=null)
           {
               map.put("messagename", "用户名已经被注册");
               return map;
           }
          user = new User();
          user.setName(username);
          user.setSalt(UUID.randomUUID().toString().substring(0, 5));
          String head = String.format("/head/%dt.png", new Random().nextInt(1000));
          user.setHeadUrl(head);
          user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
          userDAO.addUser(user);

          String ticket = addLoginTicket(user.getId());
          map.put("ticket", ticket);

          return map;
    }

    public Map<String, Object> login(String username, String password) {
         Map<String,Object> map=new HashMap<String,Object>();
         if(username==null||username.isEmpty())
         {
             map.put("messageusername","用户名不能为空");
             return map;
         }
         if(password==null||password.isEmpty())
         {
             map.put("messagepassword","密码不能为空");
             return map;
         }
        User user = userDAO.selectByName(username);

        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }

        if (!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }
    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        LoginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket) {
        LoginTicketDAO.updateStatus(ticket, 1);
    }
}
