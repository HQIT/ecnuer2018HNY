package com.cloume.ecnu.ecnuer2018.controller;

import com.cloume.ecnu.ecnuer2018.model.MyUser;
import com.cloume.ecnu.ecnuer2018.repository.UserRepository;
import com.cloume.ecnu.ecnuer2018.util.SignUtil;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/wx")
public class WxController {

    final WxMpService wxService;
    final UserRepository userRepository;

    @Autowired
    WxController(WxMpService wxService, UserRepository userRepository) {
        this.wxService = wxService;
        this.userRepository = userRepository;
    }

    @ResponseBody
    @RequestMapping(value = "/signature", method = RequestMethod.POST)
    public WxJsapiSignature getSignature(
            HttpServletRequest request,
            @RequestBody SignatureRequestBody body) {
        Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        try {
            return wxService.createJsapiSignature(body.url);
        } catch (WxErrorException e) {
            logger.warning("getSignature exp:" + e.getMessage());
            return null;
        }
    }

    /**
     * 微信公众平台验证
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @ResponseBody
    @GetMapping("/verify")
    public String verifyMpAccount(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
                                  @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) throws WxErrorException {
        if (SignUtil.checkSignature(wxService.getWxMpConfigStorage().getToken(), signature, timestamp, nonce)) {
            return echostr;
        } else {
            return "";
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String tokenVerify(
            String signature,
            String timestamp,
            String nonce,
            String echostr
    ) {

        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "bye";
    }

    /// login
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "back", defaultValue = "") String back
    ) throws IOException {
        String queryString = request.getQueryString();
//        StringBuffer redirectUri = request.getRequestURL().append("/result");
        StringBuilder redirectUri = new StringBuilder();
        redirectUri.append("https://hszjt.com/ecnuer2018/wx/login/result");
        String uri = (queryString == null) ?
                redirectUri.toString() :
                redirectUri.append('?').append(queryString).toString();
        String url = wxService.oauth2buildAuthorizationUrl(uri,
                WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                back);
        response.sendRedirect(url);
    }

    @RequestMapping(value = "/login/result", method = RequestMethod.GET)
    public void loginResult(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, WxErrorException {

        WxMpOAuth2AccessToken accessToken =
                wxService.oauth2getAccessToken(request.getParameter("code"));

        WxMpUser wxUser = wxService.oauth2getUserInfo(accessToken, "zh_CN");
        String headImgUrl = wxUser.getHeadImgUrl();
        if (headImgUrl.startsWith("http:")) {
            headImgUrl = headImgUrl.replaceFirst("^http:", "https:");
        }

        MyUser user = new MyUser();
        user.setNickname(wxUser.getNickname());
        user.setOpenId(wxUser.getOpenId());
        user.setUnionId(wxUser.getUnionId());
        user.setPortrait(headImgUrl);
        user.setCountry(wxUser.getCountry());
        user.setCity(wxUser.getCity());
        user.setProvince(wxUser.getProvince());
        user.setGender(wxUser.getSex());

        synchronized (userRepository) {
            if (!userRepository.existsByOpenId(user.getOpenId())) {
                userRepository.save(user);
            }
        }

        request.getSession().setAttribute("_MY_USER", user);

        String back = request.getParameter("state");
        Logger.getLogger(getClass().getName()).info("back: " + back);
        response.sendRedirect(back.isEmpty() ? "/" : back);
    }

    static class SignatureRequestBody {
        String url;

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
