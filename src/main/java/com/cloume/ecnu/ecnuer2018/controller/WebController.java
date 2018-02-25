package com.cloume.ecnu.ecnuer2018.controller;

import com.cloume.ecnu.ecnuer2018.model.MyUser;
import com.cloume.ecnu.ecnuer2018.repository.Received;
import com.cloume.ecnu.ecnuer2018.repository.ReceivedRepository;
import com.cloume.ecnu.ecnuer2018.service.IMessageService;
import com.cloume.ecnu.ecnuer2018.service.impl.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebController extends CommonController {

    final ReceivedRepository receivedRepository;
    final IMessageService messageService;

    @Autowired
    public WebController(ReceivedRepository receivedRepository, MessageService messageService) {
        this.receivedRepository = receivedRepository;
        this.messageService = messageService;
    }

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String getMessage(
            HttpServletRequest request,
            @RequestParam(value = "tab", defaultValue = "sent") String tab
    ) {
        request.setAttribute("_TAB", tab);
        return "message";
    }

    @RequestMapping(value = "/message/statistic/{id}", method = RequestMethod.GET)
    public String viewStatistics(HttpServletRequest request,
                                 @PathVariable("id") String id) {
        request.setAttribute("_MSG_ID", id);
        return "message.statistic";
    }

    @RequestMapping(value = "/message/{id}", method = RequestMethod.GET)
    public String viewMessage(
            HttpServletRequest request,
            @PathVariable(value = "id") String id,
            @RequestParam(value = "s", defaultValue = "received") String state
    ) {
        request.setAttribute("_MSG_ID", id);
        request.setAttribute("_STATE", state);

        MyUser u = getCurrentUser(request);
        if (u != null) {
            // if received for *me* and message(blessing) id not exists, save it
            messageService.setBlessingReceived(id, u);
        }

        return "message.viewer";
    }

    @RequestMapping(value = "/message/editor", method = RequestMethod.GET)
    public String getMessageEditor(
            HttpServletRequest request,
            @RequestParam(value = "tab", defaultValue = "sent") String tab
    ) {
        return "message.editor";
    }
}
