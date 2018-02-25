var _USER;
var _SHARING = {
    title: '', // 分享标题
    desc: '', // 分享描述
    link: '', // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
    imgUrl: '', // 分享图标
    success: function () {
        weui.toast('分享成功', {
            duration: 2000,
            callback: function() {
                window.location.href = baseUrl + '/message';
            }
        });
    },
    cancel: function () {
        console.log('share canceled');
    }
};
var _SHARING_TIMELINE = {
    title: '', // 分享标题
    link: '', // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
    imgUrl: '', // 分享图标
    success: function () {
        weui.toast('分享成功', {
            duration: 2000,
            callback: function() {
                window.location.href = baseUrl + '/message';
            }
        });
    },
    cancel: function () {
        console.log('share canceled');
    }
};
var config = {
    el: '#app',
    data: {
        message: {
            content: '',
            receivers: []
        },
        showShareHint: false,
        showModal: false
    },
    methods: {
        newBlessing: function () {
            this.showShareHint = true;
        },
        hideHint: function () {
            this.showShareHint = false;
            this.showModal = false;
        },
        modalShow: function () {
            this.showModal = true;
        },
        home: function () {
            window.location.href = baseUrl + '/message';
        }
    },
    created: function () {
        var vm = this;
        this.$http.get(baseUrl + `/api/message/${_MSG_ID}`).then(function (res) {
            if (res.status === 200) {
                vm.message = res.data;
                _SHARING.title = ['来自', res.data.author, '的新年祝福'].join('');
                _SHARING.desc = ['第', res.data.index, '份ECNU校友祝福,2018新年快乐!狗年大吉!'].join('');
                _SHARING_TIMELINE.title = ['第', res.data.index, '份ECNU校友祝福,', res.data.author, '祝你新年快乐！'].join('');
                _SHARING_TIMELINE.link = _SHARING.link = [baseUrl, '/message/' + _MSG_ID, '?random=' + (Math.random()*1000000).toFixed(0)].join('');
                _SHARING_TIMELINE.imgUrl = _SHARING.imgUrl = res.data.sender.portrait;
            } else {
                weui.toolTips('获取消息失败', 3000);
            }
        }).catch(err => {
            console.log('err', err);
            weui.toolTips('网络异常', 3000);
        });
    }
};
var on_login_success = function () {
    Vue.http.post(baseUrl + '/wx/signature', {
        url: window.location.href
    }).then(function (res) {
        if (res.status === 200) {
            var data = res.data;
            wx.config({
                debug: false,
                appId: data.appId,
                timestamp: data.timestamp,
                nonceStr: data.nonceStr,
                signature: data.signature,
                jsApiList: [
                    'onMenuShareAppMessage',
                    'onMenuShareTimeline',
                    'onMenuShareQQ',
                    'onMenuShareWeibo',
                    'onMenuShareQZone'
                ]
            });
        }
    }, function (e) {
        console.log('bad signature', e);
    });
};
wx.ready(function () {
    wx.showMenuItems({
        menuList: [
            'menuItem:share:appMessage',
            'menuItem:share:timeline',
            'menuItem:share:weiboApp',
            'menuItem:share:facebook',
            'menuItem:share:QZone'
        ]
    });
    wx.onMenuShareAppMessage(_SHARING);
    wx.onMenuShareTimeline(_SHARING_TIMELINE);
    wx.onMenuShareQQ(_SHARING);
    wx.onMenuShareWeibo(_SHARING);
    wx.onMenuShareQZone(_SHARING);
});
Vue.http.get(baseUrl + '/api/user').then(function (res) {
    _USER = res.data;
    if (!_USER) {
        var back = [window.location.pathname, window.location.search].join('');
        window.location.href = [baseUrl + '/wx/login', '?back=', encodeURIComponent(back)].join('');
        return false;
    } else {
        on_login_success();
    }
}, function (e) {
    console.error(e);
});
var app = new Vue(config);