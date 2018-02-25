package com.cloume.ecnu.ecnuer2018.controller;

import com.cloume.ecnu.ecnuer2018.model.Blessing;
import com.cloume.ecnu.ecnuer2018.model.MyUser;
import com.cloume.ecnu.ecnuer2018.repository.BlessingRepository;
import com.cloume.ecnu.ecnuer2018.repository.UserRepository;
import com.cloume.ecnu.ecnuer2018.service.IMessageService;
import com.cloume.ecnu.ecnuer2018.service.impl.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api")
public class ApiController extends CommonController {
    final UserRepository userRepository;
    final BlessingRepository blessingRepository;
    final IMessageService messageService;

    @Autowired
    public ApiController(UserRepository userRepository, BlessingRepository blessingRepository, MessageService messageService) {
        this.userRepository = userRepository;
        this.blessingRepository = blessingRepository;
        this.messageService = messageService;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public MyUser getUser(HttpServletRequest request) {
        return getCurrentUser(request);
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public Blessing postBlessing(
            @RequestBody Blessing body
    ) {
        synchronized (blessingRepository) {
            body.setIndex(blessingRepository.count() + 1);
        }
        return blessingRepository.save(body);
    }

    @RequestMapping(value = "/message/{id}", method = RequestMethod.GET)
    public Blessing getBlessing(
            @PathVariable(value = "id") String id
    ) {
        return blessingRepository.findOne(id);
    }

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public Collection<? extends Blessing> getBlessingList(
            HttpServletRequest request,
            @RequestParam(value = "f", defaultValue = "by-sender") String filter
    ) {
        MyUser u = getUser(request);
        if (u == null) return null;

        if (filter.equalsIgnoreCase("by-sender")) {
            return blessingRepository.findBySender(u.getOpenId(),
                    new PageRequest(0, Integer.MAX_VALUE, Sort.Direction.DESC, "timestamp")
            );
        } else {
            return blessingRepository.findByReceiver(u.getOpenId(),
                    new PageRequest(0, Integer.MAX_VALUE, Sort.Direction.DESC, "timestamp")
            );
        }
    }
}
